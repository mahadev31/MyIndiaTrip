package com.mytrip.myindiatrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.adapter.ChildImageSliderAdapter
import com.mytrip.myindiatrip.databinding.ActivityDataDisplayBinding
import com.mytrip.myindiatrip.fragment.MapsFragment
import com.mytrip.myindiatrip.model.ModelClass
import java.util.*
import kotlin.collections.ArrayList

class DataDisplayActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var displayBinding: ActivityDataDisplayBinding

    private var textToSpeech: TextToSpeech? = null
    lateinit var mDbRef: DatabaseReference
    var childSliderList = ArrayList<ModelClass>()
    lateinit var search: String
    lateinit var selectItemName: String
    lateinit var child_key: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayBinding = ActivityDataDisplayBinding.inflate(layoutInflater)
        setContentView(displayBinding.root)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        initView()
//        mapView()
    }

    private fun initView() {
        displayBinding.imgBackDisplay.setOnClickListener {
            onBackPressed()
        }

        search = intent.getStringExtra("search").toString()
        selectItemName = intent.getStringExtra("selectItemName").toString()
        var key = intent.getStringExtra("Key").toString()
        child_key = intent.getStringExtra("child_key").toString()

        // Declaring fragment manager from making data
        // transactions using the custom fragment
        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = MapsFragment()



        if (child_key != null && intent.hasExtra("category")) {
            var childSliderAdapter = ChildImageSliderAdapter(this, childSliderList)
            displayBinding.viewPager.adapter = childSliderAdapter
            displayBinding.wormDotsIndicator.attachTo(displayBinding.viewPager)

            mDbRef.child("category_data").child(key).child("place").child(child_key).child("slider")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        childSliderList.clear()
                        for (postSnapshot in snapshot.children) {
                            val currentUser = postSnapshot.getValue(ModelClass::class.java)
                            childSliderList.add(currentUser!!)

                        }
                        childSliderAdapter.notifyDataSetChanged()

                        val mBundle = Bundle()
                        mBundle.putString("Key", key)
                        mBundle.putString("child_key", child_key)
                        mBundle.putBoolean("category", true)
                        mFragment.arguments = mBundle
                        mFragmentTransaction.add(R.id.frameMap, mFragment).commit()

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            mDbRef.child("category_data").child(key).child("place").child(child_key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        var name = snapshot.child("name").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var location = snapshot.child("location").value.toString()


                        displayBinding.txtPlaceTitle.text = name
                        displayBinding.txtPlaceRating.text = rating
                        displayBinding.txtPlaceDescription.text = description
                        displayBinding.txtPlaceLocation.text = location
//                searchAdapter.notifyDataSetChanged()
                        Log.e("Try", "key: " + key)
                        Log.e("Try", "child_key: " + child_key)
                        Log.e("Try", "name: " + name)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else if (key != null && intent.hasExtra("imageSliderList")) {

            var childSliderAdapter = ChildImageSliderAdapter(this, childSliderList)
            displayBinding.viewPager.adapter = childSliderAdapter
            displayBinding.wormDotsIndicator.attachTo(displayBinding.viewPager)

            mDbRef.child("image_slider").child(key).child("slider")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        childSliderList.clear()
                        for (postSnapshot in snapshot.children) {
                            val currentUser = postSnapshot.getValue(ModelClass::class.java)
                            childSliderList.add(currentUser!!)

                        }
                        childSliderAdapter.notifyDataSetChanged()

                    val mBundle = Bundle()
                    mBundle.putString("Key",key)
                    mBundle.putBoolean("imageSliderList",true)
                    mFragment.arguments = mBundle
                    mFragmentTransaction.add(R.id.frameMap, mFragment).commit()
                    }


                    override fun onCancelled(error: DatabaseError) {

                    }

                })




            mDbRef.child("image_slider").child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        var name = snapshot.child("name").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var location = snapshot.child("location").value.toString()



                        displayBinding.txtPlaceTitle.text = name
                        displayBinding.txtPlaceRating.text = rating
                        displayBinding.txtPlaceDescription.text = description
                        displayBinding.txtPlaceLocation.text = location
//                searchAdapter.notifyDataSetChanged()
                        Log.e("Try", "key: " + key)
                        Log.e("Try", "child_key: " + child_key)
                        Log.e("Try", "name: " + name)


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else if (key != null && intent.hasExtra("popular")) {

            var childSliderAdapter = ChildImageSliderAdapter(this, childSliderList)
            displayBinding.viewPager.adapter = childSliderAdapter
            displayBinding.wormDotsIndicator.attachTo(displayBinding.viewPager)

            mDbRef.child("popular_place").child(key).child("slider")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        childSliderList.clear()
                        for (postSnapshot in snapshot.children) {
                            val currentUser = postSnapshot.getValue(ModelClass::class.java)
                            childSliderList.add(currentUser!!)

                        }
                        childSliderAdapter.notifyDataSetChanged()

                        val mBundle = Bundle()
                        mBundle.putString("Key",key)
                        mBundle.putBoolean("popularList",true)
                        mFragment.arguments = mBundle
                        mFragmentTransaction.add(R.id.frameMap, mFragment).commit()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            mDbRef.child("popular_place").child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        var name = snapshot.child("name").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var location = snapshot.child("location").value.toString()


                        displayBinding.txtPlaceTitle.text = name
                        displayBinding.txtPlaceRating.text = rating
                        displayBinding.txtPlaceDescription.text = description
                        displayBinding.txtPlaceLocation.text = location
//                searchAdapter.notifyDataSetChanged()
                        Log.e("Try", "key: " + key)
                        Log.e("Try", "child_key: " + child_key)
                        Log.e("Try", "name: " + name)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else if (key != null && intent.hasExtra("myTrip")) {

            var childSliderAdapter = ChildImageSliderAdapter(this, childSliderList)
            displayBinding.viewPager.adapter = childSliderAdapter
            displayBinding.wormDotsIndicator.attachTo(displayBinding.viewPager)

            mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                .child("slider").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    childSliderList.clear()
                    for (postSnapshot in snapshot.children) {
                        val currentUser = postSnapshot.getValue(ModelClass::class.java)
                        childSliderList.add(currentUser!!)

                    }
                    childSliderAdapter.notifyDataSetChanged()

                    val mBundle = Bundle()
                    mBundle.putString("search",search)
                    mBundle.putString("selectItemName",selectItemName)
                    mBundle.putString("Key",key)
                    mBundle.putBoolean("myTrip",true)
                    mFragment.arguments = mBundle
                    mFragmentTransaction.add(R.id.frameMap, mFragment).commit()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })




            mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        var name = snapshot.child("name").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var location = snapshot.child("location").value.toString()



                        displayBinding.txtPlaceTitle.text = name
                        displayBinding.txtPlaceRating.text = rating
                        displayBinding.txtPlaceDescription.text = description
                        displayBinding.txtPlaceLocation.text = location
//                searchAdapter.notifyDataSetChanged()
                        Log.e("Try", "key: " + key)
                        Log.e("Try", "search: " + search)
                        Log.e("Try", "selectItemName: " + selectItemName)
                        Log.e("Try", "name: " + name)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else {
            mDbRef.child("search_bar").child(search).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        var name = snapshot.child("name").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var location = snapshot.child("location").value.toString()



                        displayBinding.txtPlaceTitle.text = name
                        displayBinding.txtPlaceRating.text = rating
                        displayBinding.txtPlaceDescription.text = description
                        displayBinding.txtPlaceLocation.text = location
//                searchAdapter.notifyDataSetChanged()
                        Log.e("Try", "search: " + search)
                        Log.e("Try", "key: " + key)
                        Log.e("Try", "title: " + title)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }

        displayBinding.imgVolumeSpeech!!.isEnabled = false
        textToSpeech = TextToSpeech(this, this)


        displayBinding.imgVolumeSpeech!!.setOnClickListener { speakOut() }

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech!!.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
            } else {
                displayBinding.imgVolumeSpeech!!.isEnabled = true
            }
        }
    }

    private fun speakOut() {
        val text = displayBinding.txtPlaceDescription!!.text.toString()
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

    fun mapView() {

    }
    //loading the another fragment in viewPager
//    private fun callFragment(fragment: Fragment) {
//        val manager: FragmentManager = supportFragmentManager
//        val transaction: FragmentTransaction = manager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
//        transaction.replace(R.id.frameMap, fragment)
//        transaction.commit()
//    }
}