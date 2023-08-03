package com.mytrip.myindiatrip.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mytrip.myindiatrip.databinding.ActivityCreateAccountBinding
import com.mytrip.myindiatrip.fragment.UserLoginFragment
import com.mytrip.myindiatrip.model.UserModelClass
import java.util.*


class CreateAccountActivity : AppCompatActivity() {

    private var image: String=""
    private lateinit var binding: ActivityCreateAccountBinding
    lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

     var filePath: Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        storageReference = FirebaseStorage.getInstance().reference

        initView()


    }

    private fun initView() {

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnChooseImage.setOnClickListener(View.OnClickListener {


                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                gallery_Launcher.launch(intent)

        })



        binding.cdSignUp.setOnClickListener {
            var firstName = binding.edtFirstC.text.toString()
            var lastName = binding.edtLastNameC.text.toString()
            var email = binding.edtEmailC.text.toString()
            var password = binding.edtPasswordC.text.toString()

            if (firstName.isEmpty()) {
                Toast.makeText(
                    this,
                    "FirstName value is empty. please fill firstName ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (lastName.isEmpty()) {
                Toast.makeText(
                    this,
                    "LastName value is empty. please fill LastName ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "email value is empty. please fill email ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "password value is empty. please fill password ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Account Created Successfully ", Toast.LENGTH_SHORT)
                            .show()


                        addUserToDatabase(image,firstName, lastName, email, auth.currentUser?.uid!!)


                      var i=Intent(this,MainActivity::class.java)
                        startActivity(i)


                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
                }

            }

        }

    }


    //gallery
    var gallery_Launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode === RESULT_OK) {


                val data: Intent? = result.getData()
                filePath= data?.data!!
                binding.imgUserDp.setImageURI(filePath)

                uploadImage()

            }
        })

    // UploadImage method
    private fun uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "user_images/"
                            + UUID.randomUUID().toString()
                )


            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath!!).addOnCompleteListener{

//                it.result.uploadSessionUri

                ref.downloadUrl.addOnSuccessListener {

                    image=it.toString()
                    Log.e("TAG", "uploadImage: "+image)
                }
            }
                .addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded " + progress.toInt() + "%"
                    )
                }
        }
    }


    private fun addUserToDatabase(image:String,firstName: String, lastName: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(UserModelClass(image,firstName, lastName, email, uid))


    }
}


