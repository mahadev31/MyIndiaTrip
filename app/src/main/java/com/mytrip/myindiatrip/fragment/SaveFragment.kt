package com.mytrip.myindiatrip.fragment

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.activity.DataDisplayActivity
import com.mytrip.myindiatrip.adapter.SaveAdapter
import com.mytrip.myindiatrip.databinding.FragmentSaveBinding
import com.mytrip.myindiatrip.model.ModelClass

class SaveFragment : Fragment() {

    lateinit var saveBinding: FragmentSaveBinding

    lateinit var mDbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    var placeList = ArrayList<ModelClass>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        saveBinding = FragmentSaveBinding.inflate(layoutInflater, container, false)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        auth = Firebase.auth
        initView()
        return saveBinding.root
    }

    private fun initView() {

//        saveBinding.tabLayout.addTab(saveBinding.tabLayout.newTab().setText("place"))  //tabLayout
//        saveBinding.tabLayout.addTab(saveBinding.tabLayout.newTab().setText("hotel"))//tabLayout
//        saveBinding.tabLayout.addTab(saveBinding.tabLayout.newTab().setText("activity"))//tabLayout


            var search: String = "surat"
            var selectItemName: String = "place"
            var adapter = SaveAdapter(requireContext(), {

                var clickIntent = Intent(context, DataDisplayActivity::class.java)
                clickIntent.putExtra("search", search)
                clickIntent.putExtra("selectItemName", selectItemName)
                clickIntent.putExtra("Key", it.key)
                clickIntent.putExtra("myTrip", true)
                Log.e("TAG", "myTripKey: " + it.key)
                Log.e("TAG", "myTrip_selected: " + selectItemName)
                startActivity(clickIntent)
            }, { save, key ->


                mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                    .child("place").child(key).removeValue()
            })

            saveBinding.rcvSaveList.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            saveBinding.rcvSaveList.adapter = adapter

            if (auth.currentUser?.uid  != null){
            mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                .child("place")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        placeList.clear()
                        for (postSnapshot in snapshot.children) {
                            val currentUser =
                                postSnapshot.getValue(ModelClass::class.java)
                            currentUser?.let { placeList.add(it) }
                            saveBinding.txtNoSave.visibility=View.GONE
                        }
                            adapter.updateList(placeList)

                    }
                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
            else{
                saveBinding.txtNoSave.text = "Make Sure Your Logged In "
            }


    }
}


