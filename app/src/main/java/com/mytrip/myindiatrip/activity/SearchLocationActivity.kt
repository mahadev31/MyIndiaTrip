package com.mytrip.myindiatrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.adapter.LocationSearchAdapter
import com.mytrip.myindiatrip.databinding.ActivitySearchLocationBinding
import com.mytrip.myindiatrip.model.LocationSearchModelClass

class SearchLocationActivity : AppCompatActivity() {

    lateinit var searchLocationBinding: ActivitySearchLocationBinding

    lateinit var mDbRef: DatabaseReference
    var searchList=ArrayList<LocationSearchModelClass>()
    lateinit var searchAdapter:LocationSearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchLocationBinding= ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(searchLocationBinding.root)


        mDbRef = FirebaseDatabase.getInstance().getReference()
        initView()
    }

    private fun initView() {

        searchLocationBinding.imgBackSearch.setOnClickListener {
            onBackPressed()
        }

//        searchLocationBinding.linCurrentLocation
        searchAdapter = LocationSearchAdapter(this, searchList)
       searchLocationBinding.rcvLocation.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchLocationBinding.rcvLocation.adapter = searchAdapter

        mDbRef.child("searchList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                searchList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(LocationSearchModelClass::class.java)
//                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                    currentUser?.cityName = postSnapshot.child("cityName").value.toString()
                    currentUser?.stateName = postSnapshot.child("stateName").value.toString()
                    searchList.add(currentUser!!)

//                    }
                }
                searchAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}