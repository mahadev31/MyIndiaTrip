package com.mytrip.myindiatrip.fragment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mytrip.myindiatrip.activity.DataDisplayActivity
import com.mytrip.myindiatrip.activity.HotelAndActivityDataActivity
import com.mytrip.myindiatrip.adapter.HotelSearchAdapter
import com.mytrip.myindiatrip.adapter.TripAdapter
import com.mytrip.myindiatrip.databinding.FragmentMyTripPlanBinding
import com.mytrip.myindiatrip.databinding.ProgressBarBinding
import com.mytrip.myindiatrip.model.ModelClass
import java.io.IOException
import java.util.*


class MyTripPlanFragment : Fragment() {

    lateinit var tripBinding: FragmentMyTripPlanBinding

    private lateinit var locationManager: LocationManager

    lateinit var mDbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    var placeList = ArrayList<ModelClass>()
    lateinit var adapter: HotelSearchAdapter
    lateinit var dialog: Dialog

    lateinit var tripAdapter: TripAdapter

    var search: String? = null

    // Initialize variables
    var city: String = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //    var client: FusedLocationProviderClient? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tripBinding = FragmentMyTripPlanBinding.inflate(layoutInflater, container, false)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        auth = Firebase.auth

        dialog = Dialog(requireContext())
        var progressBarBinding = ProgressBarBinding.inflate(layoutInflater)
        dialog.setContentView(progressBarBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()


        // Get the fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create(),
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            // Request location permission
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }


//        locationFunction()
        searchItem()
        defaultPlaceAdapter()

        return tripBinding.root
    }


    private fun searchItem() {

        tripBinding.tabLayout.addTab(tripBinding.tabLayout.newTab().setText("place"))  //tabLayout
        tripBinding.tabLayout.addTab(tripBinding.tabLayout.newTab().setText("hotel"))//tabLayout
        tripBinding.tabLayout.addTab(tripBinding.tabLayout.newTab().setText("activity"))//tabLayout


        tripBinding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setAdapter()
            }
            true
        }
        tripBinding.imgSearchT.setOnClickListener {
            setAdapter()


        }

    }

    private fun setAdapter() {

        dialog = Dialog(requireContext())
        var progressBarBinding = ProgressBarBinding.inflate(layoutInflater)
        dialog.setContentView(progressBarBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()


        search = tripBinding.edtSearch.text.toString()

        if (search!!.isEmpty()) {
            Toast.makeText(context, "Pleas Enter value", Toast.LENGTH_SHORT).show()
        } else {
            placeFun()

            tripBinding.tabLayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener { //change tab with view pager
                override fun onTabSelected(tab: TabLayout.Tab) {

                    when (tab.position) {
                        0 -> {
                            placeFun()
                        }
                        1 -> {
                            hotelFun()
                        }
                        2 -> {
                            activityFun()
                        }


                    }


                    Log.e("TAG", "onTabSelected: " + tab.position)


                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }

            })
        }
    }

    private fun placeFun() {

        val selectItemName = "place"

        tripAdapter = TripAdapter(requireContext(), {
            var clickIntent = Intent(context, DataDisplayActivity::class.java)
            clickIntent.putExtra("search", search)
            clickIntent.putExtra("selectItemName", selectItemName)
            clickIntent.putExtra("Key", it.key)
            clickIntent.putExtra("myTrip", true)
            Log.e("TAG", "myTripKey: " + it.key)
            Log.e("TAG", "myTrip_selected: " + selectItemName)
            startActivity(clickIntent)
        }, { save, key ->


            mDbRef.child("my_trip_plan").child(search!!).child(selectItemName).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        var name = snapshot.child("name").value.toString()
                        var image = snapshot.child("image").value.toString()
                        var location = snapshot.child("location").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var rent = snapshot.child("rent").value.toString()
                        var rating = snapshot.child("rating").value.toString()

                        Log.e("TAG", "onDataChange:name " + name)

                        mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                            .child("place").child(key).setValue(
                                SaveModelClass(
                                    name,
                                    image,
                                    location,
                                    description,
                                    rating,
                                    rent,
                                    key,
                                    save
                                )
                            )
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        })
        tripBinding.rcvSuggestionItem.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        tripBinding.rcvSuggestionItem.adapter = tripAdapter

        mDbRef.child("my_trip_plan").child(search!!).child(selectItemName)
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

    private fun hotelFun() {
        val selectItemName = "hotel"
        adapter = HotelSearchAdapter(this, placeList, {
            var clickIntent = Intent(context, HotelAndActivityDataActivity::class.java)
            clickIntent.putExtra("search", search)
            clickIntent.putExtra("selectItemName", selectItemName)
            clickIntent.putExtra("Key", it.key)
            clickIntent.putExtra("myTrip", true)
            Log.e("TAG", "myTripKey: " + it.key)
            Log.e("TAG", "myTrip_selected: " + selectItemName)
            startActivity(clickIntent)
        }, { save, key ->


            mDbRef.child("my_trip_plan").child(search!!).child(selectItemName).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        var name = snapshot.child("name").value.toString()
                        var image = snapshot.child("image").value.toString()
                        var location = snapshot.child("location").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var rent = snapshot.child("rent").value.toString()
                        var rating = snapshot.child("rating").value.toString()

                        Log.e("TAG", "onDataChange:name " + name)

                        mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                            .child("hotel").child(key).setValue(
                                SaveModelClass(
                                    name,
                                    image,
                                    location,
                                    description,
                                    rating,
                                    rent,
                                    key,
                                    save
                                )
                            )
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        })
        tripBinding.rcvSuggestionItem.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        tripBinding.rcvSuggestionItem.adapter = adapter

        mDbRef.child("my_trip_plan").child(search!!).child(selectItemName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    placeList.clear()
                    for (postSnapshot in snapshot.children) {
                        val currentUser =
                            postSnapshot.getValue(ModelClass::class.java)
                        currentUser?.let { placeList.add(it) }

                    }
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun activityFun() {
        val selectItemName = "activity"
        adapter = HotelSearchAdapter(this, placeList, {
            var clickIntent = Intent(context, HotelAndActivityDataActivity::class.java)
            clickIntent.putExtra("search", search)
            clickIntent.putExtra("selectItemName", selectItemName)
            clickIntent.putExtra("Key", it.key)
            clickIntent.putExtra("myTrip", true)
            Log.e("TAG", "myTripKey: " + it.key)
            Log.e("TAG", "myTrip_selected: " + selectItemName)
            startActivity(clickIntent)
        }, { save, key ->


            mDbRef.child("my_trip_plan").child(search!!).child(selectItemName).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        var name = snapshot.child("name").value.toString()
                        var image = snapshot.child("image").value.toString()
                        var location = snapshot.child("location").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var rent = snapshot.child("rent").value.toString()
                        var rating = snapshot.child("rating").value.toString()

                        Log.e("TAG", "onDataChange:name " + name)

                        mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                            .child("activity").child(key).setValue(
                                SaveModelClass(
                                    name,
                                    image,
                                    location,
                                    description,
                                    rating,
                                    rent,
                                    key,
                                    save
                                )
                            )
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        })
        tripBinding.rcvSuggestionItem.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        tripBinding.rcvSuggestionItem.adapter = adapter

        mDbRef.child("my_trip_plan").child(search!!).child(selectItemName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    placeList.clear()
                    for (postSnapshot in snapshot.children) {
                        val currentUser =
                            postSnapshot.getValue(ModelClass::class.java)
                        currentUser?.let { placeList.add(it) }

                    }
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    private fun defaultPlaceAdapter() {
        var search: String = "surat"
        var selectItemName: String = "place"
        tripAdapter = TripAdapter(requireContext(), {
            var clickIntent = Intent(context, DataDisplayActivity::class.java)
            clickIntent.putExtra("search", search)
            clickIntent.putExtra("selectItemName", selectItemName)
            clickIntent.putExtra("Key", it.key)
            clickIntent.putExtra("myTrip", true)
            Log.e("TAG", "myTripKey: " + it.key)
            Log.e("TAG", "myTrip_selected: " + selectItemName)
            startActivity(clickIntent)
        }, { save, key ->

            mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var name = snapshot.child("name").value.toString()
                        var image = snapshot.child("image").value.toString()
                        var location = snapshot.child("location").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var rent = snapshot.child("rent").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        Log.e("TAG", "onDataChange:name " + name)

                        mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                            .child("save_data").child(auth.currentUser?.uid!!)
                            .child(key).child("save").setValue(save)

                        mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                            .child("place").child(key).setValue(
                                SaveModelClass(
                                    name,
                                    image,
                                    location,
                                    description,
                                    rating,
                                    rent,
                                    key,
                                    save
                                )
                            )


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        })
        tripBinding.rcvSuggestionItem.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        tripBinding.rcvSuggestionItem.adapter = tripAdapter

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

    private fun defaultHotelAdapter() {
        var search: String = "surat"
        var selectItemName: String = "hotel"
        tripAdapter = TripAdapter(requireContext(), {
            var clickIntent = Intent(context, DataDisplayActivity::class.java)
            clickIntent.putExtra("search", search)
            clickIntent.putExtra("selectItemName", selectItemName)
            clickIntent.putExtra("Key", it.key)
            clickIntent.putExtra("myTrip", true)
            Log.e("TAG", "myTripKey: " + it.key)
            Log.e("TAG", "myTrip_selected: " + selectItemName)
            startActivity(clickIntent)
        }, { save, key ->

            mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var name = snapshot.child("name").value.toString()
                        var image = snapshot.child("image").value.toString()
                        var location = snapshot.child("location").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var rent = snapshot.child("rent").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        Log.e("TAG", "onDataChange:name " + name)

                        mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                            .child("save_data").child(auth.currentUser?.uid!!)
                            .child(key).child("save").setValue(save)

                        mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                            .child("place").child(key).setValue(
                                SaveModelClass(
                                    name,
                                    image,
                                    location,
                                    description,
                                    rating,
                                    rent,
                                    key,
                                    save
                                )
                            )


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        })
        tripBinding.rcvSuggestionItem.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        tripBinding.rcvSuggestionItem.adapter = tripAdapter

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

    private fun defaultActivityAdapter() {
        var search: String = "surat"
        var selectItemName: String = "activity"
        tripAdapter = TripAdapter(requireContext(), {
            var clickIntent = Intent(context, DataDisplayActivity::class.java)
            clickIntent.putExtra("search", search)
            clickIntent.putExtra("selectItemName", selectItemName)
            clickIntent.putExtra("Key", it.key)
            clickIntent.putExtra("myTrip", true)
            Log.e("TAG", "myTripKey: " + it.key)
            Log.e("TAG", "myTrip_selected: " + selectItemName)
            startActivity(clickIntent)
        }, { save, key ->

            mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var name = snapshot.child("name").value.toString()
                        var image = snapshot.child("image").value.toString()
                        var location = snapshot.child("location").value.toString()
                        var description = snapshot.child("description").value.toString()
                        var rent = snapshot.child("rent").value.toString()
                        var rating = snapshot.child("rating").value.toString()
                        Log.e("TAG", "onDataChange:name " + name)

                        mDbRef.child("my_trip_plan").child(search).child(selectItemName).child(key)
                            .child("save_data").child(auth.currentUser?.uid!!)
                            .child(key).child("save").setValue(save)

                        mDbRef.child("user").child(auth.currentUser?.uid!!).child("save_data")
                            .child("place").child(key).setValue(
                                SaveModelClass(
                                    name,
                                    image,
                                    location,
                                    description,
                                    rating,
                                    rent,
                                    key,
                                    save
                                )
                            )


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        })
        tripBinding.rcvSuggestionItem.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        tripBinding.rcvSuggestionItem.adapter = tripAdapter

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

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // Get the last known location
            val location = locationResult.locations.last()

            // Set the location text
            val geocoder = Geocoder(requireContext())
            try {
                // on below line we are getting location from the
                // location name and adding that location to address list.
                val addresses: List<Address>? = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1
                )

                var address =
                    addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                city = addresses!![0].locality
                val state = addresses!![0].adminArea
                val country = addresses!![0].countryName
                val postalCode = addresses!![0].postalCode
                val knownName = addresses!![0].featureName

                tripBinding.txtCurrentLocation.text = city.toString()

                Log.e(
                    "TAG",
                    "Your current location is: ${location.latitude}, ${location.longitude}"
                )

            } catch (e: IOException) {
                e.printStackTrace()
            }
//            val geocoder: Geocoder =
//                Geocoder(context!!, Locale.getDefault())
//            val addresses: List<Address>? = geocoder.getFromLocation(
//                location.latitude, location.longitude, 1
//            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

//            var address =
//                addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//
//            city = addresses!![0].locality
//            val state = addresses!![0].adminArea
//            val country = addresses!![0].countryName
//            val postalCode = addresses!![0].postalCode
//            val knownName = addresses!![0].featureName
//
//            tripBinding.txtCurrentLocation.text = city.toString()
//
//            Log.e("TAG", "Your current location is: ${location.latitude}, ${location.longitude}")


        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Request location updates
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                fusedLocationClient.requestLocationUpdates(
                    LocationRequest.create(),
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                // Location permission is denied
                Toast.makeText(
                    requireActivity(),
                    "Location permission is denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

class SaveModelClass(
    var name: String,
    var image: String,
    var location: String,
    var description: String,
    var rating: String,
    var rent: String,
    var key: String,
    var save: Int
) {

}

