package com.mytrip.myindiatrip.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mytrip.myindiatrip.adapter.TripAdapter
import com.mytrip.myindiatrip.databinding.ActivitySearchBinding
import com.mytrip.myindiatrip.databinding.ProgressBarBinding
import com.mytrip.myindiatrip.model.ModelClass

class SearchActivity : AppCompatActivity() {
    lateinit var searchBinding: ActivitySearchBinding
    var placeList = ArrayList<ModelClass>()
    lateinit var tripAdapter: TripAdapter
    lateinit var mDbRef: DatabaseReference
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)

        mDbRef = FirebaseDatabase.getInstance().getReference()

        initView()
    }

    private fun initView() {
        searchBinding.imgBackSearch.setOnClickListener {
            onBackPressed()
        }

        searchBinding.edtSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFunction()
            }
            true
        }
        searchBinding.imgSearch.setOnClickListener {
            searchBinding.edtSearchText
            searchFunction()

        }
    }

    private fun searchFunction() {
        dialog = Dialog(this)
        var progressBarBinding = ProgressBarBinding.inflate(layoutInflater)
        dialog.setContentView(progressBarBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        var search = searchBinding.edtSearchText.text.toString()

        var selectItemName: String = "place"

        if (search.isEmpty()) {
            Toast.makeText(this, "Pleas Enter value", Toast.LENGTH_SHORT).show()
        } else {


            tripAdapter = TripAdapter(this,{
                var clickIntent = Intent(this, DataDisplayActivity::class.java)
                clickIntent.putExtra("search", search)
                clickIntent.putExtra("selectItemName", selectItemName)
                clickIntent.putExtra("Key", it.key)
                clickIntent.putExtra("myTrip", true)
                Log.e("TAG", "myTripKey: " + it.key)
                Log.e("TAG", "myTrip_selected: " + selectItemName)
                startActivity(clickIntent)
            },{save,key->

            })
            searchBinding.rcvSearchPlace.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
            searchBinding.rcvSearchPlace.adapter = tripAdapter

            mDbRef.child("my_trip_plan").child(search).child(selectItemName)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        placeList.clear()
                        for (postSnapshot in snapshot.children) {
                            val currentUser =
                                postSnapshot.getValue(ModelClass::class.java)
                            currentUser?.let { placeList.add(it) }

                        }
                        tripAdapter.updateList(placeList)
                        dialog.dismiss()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }
}