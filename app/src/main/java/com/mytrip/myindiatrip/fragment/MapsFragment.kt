package com.mytrip.myindiatrip.fragment

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.adapter.CategoryAdapter
import com.mytrip.myindiatrip.model.ModelClass
import java.io.IOException

class MapsFragment : Fragment() {
    var addedMarker: Marker? = null

    lateinit var mDbRef: DatabaseReference
    private val callback = OnMapReadyCallback { googleMap ->


        mDbRef = FirebaseDatabase.getInstance().reference

        // Gets the data from the passed bundle
        val bundle = arguments
        var key = arguments?.getString("Key")
        val child_key = arguments?.getString("child_key")
        val search = arguments?.getString("search")
        val selectItemName = arguments?.getString("selectItemName")

        Log.e("TAG", "map - child key " + child_key)
        Log.e("TAG", "map - key " + key)

        if (arguments?.getBoolean("category") == true) {
            mDbRef.child("category_data").child(key!!).child("place").child(child_key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        var location = snapshot.child("location").value.toString()


                        //                searchAdapter.notifyDataSetChanged()
                        Log.e("Try", "location: " + location)

                        var addressList: List<Address>? = null

                        // checking if the entered location is null or not.
                        if (location != null || location == "") {
                            // on below line we are creating and initializing a geo coder.
                            val geocoder = Geocoder(requireContext())
                            try {
                                // on below line we are getting location from the
                                // location name and adding that location to address list.
                                addressList = geocoder.getFromLocationName(location, 1)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            // on below line we are getting the location
                            // from our list a first position.
                            val address = addressList!![0]

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            val latLng = LatLng(address.latitude, address.longitude)

                            Log.e(
                                "TAG",
                                "latitude:-  " + address.latitude + " " + "longitude:- " + address.longitude
                            )


                            // on below line we are adding marker to that position.
                            addedMarker =
                                googleMap.addMarker(
                                    MarkerOptions().position(latLng).title(location)
                                )!!

                            // below line is to animate camera to that position.
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    16f
                                )
                            )


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }
        else if (arguments?.getBoolean("imageSliderList") == true) {

            mDbRef.child("image_slider").child(key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        Log.e("TAG", "image_slider: " + key)
                        var location = snapshot.child("location").value.toString()


                        Log.e("Try", "location: " + location)

                        var addressList: List<Address>? = null

                        // checking if the entered location is null or not.
                        if (location != null || location == "") {
                            // on below line we are creating and initializing a geo coder.
                            val geocoder = Geocoder(requireContext())
                            try {
                                // on below line we are getting location from the
                                // location name and adding that location to address list.
                                addressList = geocoder.getFromLocationName(location, 1)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            // on below line we are getting the location
                            // from our list a first position.
                            val address = addressList!![0]

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            val latLng = LatLng(address.latitude, address.longitude)

                            Log.e(
                                "TAG",
                                "latitude:-  " + address.latitude + " " + "longitude:- " + address.longitude
                            )


                            // on below line we are adding marker to that position.
                            addedMarker =
                                googleMap.addMarker(
                                    MarkerOptions().position(latLng).title(location)
                                )!!

                            // below line is to animate camera to that position.
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else if (arguments?.getBoolean("popularList") == true) {

            mDbRef.child("popular_place").child(key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        Log.e("TAG", "popular_place: " + key)
                        var location = snapshot.child("location").value.toString()


                        Log.e("Try", "location: " + location)

                        var addressList: List<Address>? = null

                        // checking if the entered location is null or not.
                        if (location != null || location == "") {
                            // on below line we are creating and initializing a geo coder.
                            val geocoder = Geocoder(requireContext())
                            try {
                                // on below line we are getting location from the
                                // location name and adding that location to address list.
                                addressList = geocoder.getFromLocationName(location, 1)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            // on below line we are getting the location
                            // from our list a first position.
                            val address = addressList!![0]

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            val latLng = LatLng(address.latitude, address.longitude)

                            Log.e(
                                "TAG",
                                "latitude:-  " + address.latitude + " " + "longitude:- " + address.longitude
                            )


                            // on below line we are adding marker to that position.
                            addedMarker =
                                googleMap.addMarker(
                                    MarkerOptions().position(latLng).title(location)
                                )!!

                            // below line is to animate camera to that position.
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else if (arguments?.getBoolean("myTrip") == true) {

            mDbRef.child("my_trip_plan").child(search!!).child(selectItemName!!).child(key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        Log.e("TAG", "popular_place: " + key)
                        var location = snapshot.child("location").value.toString()


                        Log.e("Try", "location: " + location)

                        var addressList: List<Address>? = null

                        // checking if the entered location is null or not.
                        if (location != null || location == "") {
                            // on below line we are creating and initializing a geo coder.
                            val geocoder = Geocoder(requireContext())
                            try {
                                // on below line we are getting location from the
                                // location name and adding that location to address list.
                                addressList = geocoder.getFromLocationName(location, 1)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            // on below line we are getting the location
                            // from our list a first position.
                            val address = addressList!![0]

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            val latLng = LatLng(address.latitude, address.longitude)

                            Log.e(
                                "TAG",
                                "latitude:-  " + address.latitude + " " + "longitude:- " + address.longitude
                            )


                            // on below line we are adding marker to that position.
                            addedMarker =
                                googleMap.addMarker(
                                    MarkerOptions().position(latLng).title(location)
                                )!!

                            // below line is to animate camera to that position.
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}