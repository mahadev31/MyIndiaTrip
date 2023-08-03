package com.mytrip.myindiatrip.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mytrip.myindiatrip.activity.CreateAccountActivity
import com.mytrip.myindiatrip.databinding.FragmentUserLoginBinding

class UserLoginFragment : Fragment() {

    lateinit var userLoginBinding: FragmentUserLoginBinding

    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userLoginBinding = FragmentUserLoginBinding.inflate(layoutInflater, container, false)

        auth = Firebase.auth
        initView()
        // Inflate the layout for this fragment
        return userLoginBinding.root
    }

    private fun initView() {
        var sharedPreferences = requireActivity().getSharedPreferences("MySharePref", AppCompatActivity.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLogin", false) == true) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(com.mytrip.myindiatrip.R.id.container, UserDetailsFragment())
                .commit()
        }
        userLoginBinding.cdSignIn.setOnClickListener {

            var email = userLoginBinding.edtEmail.text.toString()
            var password = userLoginBinding.edtPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(
                    context,
                    "email value is empty. please fill email ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    context,
                    "password value is empty. please fill password ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(com.mytrip.myindiatrip.R.id.container, HomeFragment())
                            .commit()




                        var myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                        myEdit.putBoolean("isLogin", true)
                        myEdit.putString("email", email)

                        myEdit.commit()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }


        userLoginBinding.txtCreateAccountPage.setOnClickListener {
            var i=Intent(context,CreateAccountActivity::class.java)
            startActivity(i)

        }
    }

}