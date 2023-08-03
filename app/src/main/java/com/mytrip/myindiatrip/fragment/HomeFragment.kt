package com.mytrip.myindiatrip.fragment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.activity.DataDisplayActivity
import com.mytrip.myindiatrip.activity.MainActivity
import com.mytrip.myindiatrip.activity.SearchActivity
import com.mytrip.myindiatrip.adapter.CategoryAdapter
import com.mytrip.myindiatrip.adapter.CategoryListAdapter
import com.mytrip.myindiatrip.adapter.ImageSliderAdapter
import com.mytrip.myindiatrip.adapter.PopularPlaceAdapter
import com.mytrip.myindiatrip.databinding.FragmentHomeBinding
import com.mytrip.myindiatrip.databinding.ProgressBarBinding
import com.mytrip.myindiatrip.model.ModelClass
import java.util.*


class HomeFragment : Fragment() {


    lateinit var homeBinding: FragmentHomeBinding

    lateinit var mDbRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    var categoryList = ArrayList<ModelClass>()
    var categoryItemList = ArrayList<ModelClass>()
    var imageSliderList = ArrayList<ModelClass>()

    lateinit var popularAdapter: PopularPlaceAdapter
    var popularList = ArrayList<ModelClass>()

    lateinit var dialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()

        dialog = Dialog(requireContext())
        var progressBarBinding = ProgressBarBinding.inflate(layoutInflater)
        dialog.setContentView(progressBarBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()



        homeBinding.imgSearch.setOnClickListener {
            var i = Intent(context, SearchActivity::class.java)
            startActivity(i)
        }


        navigationDrawer()
        autoVideoPlay()
        category()

        autoImageSlider()
        popularPlace()
        return homeBinding.root


    }

    private fun navigationDrawer() {
        homeBinding.imgNavMenu.setOnClickListener {
            homeBinding.layDraw.openDrawer(GravityCompat.START)

        }


        if (mAuth.currentUser?.email == null) {
            homeBinding.layProfileNav.visibility = View.GONE
            homeBinding.txtLoginNav.visibility = View.VISIBLE

            homeBinding.txtLoginNav.setOnClickListener {
                val manager: FragmentManager = requireActivity().supportFragmentManager
                val transaction: FragmentTransaction = manager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                transaction.replace(R.id.container, UserLoginFragment())
                transaction.commit()
            }

        } else {
//           user information
            var query: Query = mDbRef.child("user").orderByChild("email").equalTo(mAuth.currentUser?.email)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {

                        var image = postSnapshot.child("image").value
                        var firstName = postSnapshot.child("firstName").value
                        var lastName = postSnapshot.child("lastName").value
                        var email = postSnapshot.child("email").value

                        context?.let { Glide.with(it).load(image).placeholder(R.drawable.user2).into(homeBinding.imgUserDp) }
                        homeBinding.txtUserFirstName.text = firstName.toString()
                        homeBinding.txtUserLastName.text = lastName.toString()
                        homeBinding.txtUserEmail.text = email.toString()


                    }
                }


                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        homeBinding.layHomeNav.setOnClickListener {
            val manager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            transaction.replace(R.id.container, HomeFragment())
            transaction.commit()
        }
//            homeBinding.laySaveNav.setOnClickListener {
//
//                val manager: FragmentManager =      requireActivity(). supportFragmentManager
//                val transaction: FragmentTransaction = manager.beginTransaction()
//                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
//                transaction.replace(R.id.container, SaveFragment())
//
//                transaction.commit()
//            }

        //Shear Link
        homeBinding.layShareNav.setOnClickListener {
            homeBinding.layDraw.closeDrawer(GravityCompat.START)
            val ShareIntent = Intent(Intent.ACTION_SEND)
            ShareIntent.type = "text/plain"
            ShareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=com.infinitytechapps.allshayari.hindi.shayari"
            )
            startActivity(ShareIntent)
        }

        //Visit privacy policy define
        homeBinding.layPrivacyNav.setOnClickListener {
            homeBinding.layDraw.closeDrawer(GravityCompat.START)
            var browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data =
                Uri.parse("https://knowledgeworldiswelth.blogspot.com/2023/04/privacy-and-policy.html");
            startActivity(browserIntent)

        }
//Logout
        homeBinding.layLogoutNav.setOnClickListener {
            var sharedPreferences = requireActivity().getSharedPreferences(
                "MySharePref",
                AppCompatActivity.MODE_PRIVATE
            )
            var myEdit: SharedPreferences.Editor = sharedPreferences.edit()
            myEdit.remove("isLogin")
            myEdit.commit()
            mAuth.signOut()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(com.mytrip.myindiatrip.R.id.container, HomeFragment())
                .commit()
        }

        //app version
        val pack: PackageManager = requireActivity().packageManager
        val info: PackageInfo = pack.getPackageInfo(requireActivity().packageName, 0)
        val version: String = info.versionName
        homeBinding.txtAppVersionNav.text = version
    }


    private fun autoVideoPlay() {
        val videoUrl =
            "https://firebasestorage.googleapis.com/v0/b/my-india-trip.appspot.com/o/itro1_360.mp4?alt=media&token=598e0bc3-df56-43fa-a3cb-16777d313957"

        //video play
        // Uri object to refer the
        // resource from the videoUrl
        val uri = Uri.parse(videoUrl)
        // sets the resource from the
        // videoUrl to the videoView
        homeBinding.videoView.setVideoURI(uri)
        homeBinding.videoView.setOnPreparedListener { mp -> setVolumeControl(mp) }
        homeBinding.videoView.start()


    }

    private fun setVolumeControl(mp: MediaPlayer) {

        mp.setVolume(0f, 0f) //Mute

        var mVolumePlaying = true
        homeBinding.imgVolume.setOnClickListener {
            if (mVolumePlaying) {
                Log.d("TAG", "setVolume ON")
                homeBinding.imgVolume.setImageResource(R.drawable.ic_volume_up)
                mp.setVolume(1f, 1f)//UnMute

            } else {

                Log.d("TAG", "setVolume OFF")
                homeBinding.imgVolume.setImageResource(R.drawable.ic_volume_off)
                mp.setVolume(0f, 0f) //Mute


            }
            mVolumePlaying = !mVolumePlaying

        }
    }

    private fun category() {
        mDbRef.child("category_data").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                categoryList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(ModelClass::class.java)
                    currentUser?.let { categoryList.add(it) }

                }
                dialog.dismiss()

                var adapter = CategoryAdapter(this@HomeFragment, categoryList) {
                    var key = it.key!!
                    Log.e("TAG", "categoryList: " + it.key)
                    Log.e("TAG", "categoryListView: $id")
                    categoryListView(key)
                }
                homeBinding.rcvCategory.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                homeBinding.rcvCategory.adapter = adapter

                adapter.notifyDataSetChanged()

                val id = "1"
                categoryListView(id)  //static key
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun categoryListView(key: String) {
        var newId = key

        Log.e("TAG", "sczc:" + newId)
        if (newId != null) {
            mDbRef.child("category_data").child(newId.toString()).child("place")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        categoryItemList.clear()
                        for (postSnapshot in snapshot.children) {
                            val currentUser = postSnapshot.getValue(ModelClass::class.java)

                            categoryItemList.add(currentUser!!)

                            Log.e("TAG", "image: " + currentUser?.image)
                            //                    }
                        }


                        var categoryListAdapter =
                            CategoryListAdapter(this@HomeFragment, categoryItemList) {
                                var i = Intent(context, DataDisplayActivity::class.java)
                                i.putExtra("Key", newId)
                                i.putExtra("child_key", it.child_key)
                                i.putExtra("category", true)
                                startActivity(i)
                            }
                        homeBinding.rcvCategoryList.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        homeBinding.rcvCategoryList.adapter = categoryListAdapter

                        categoryListAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }


    }

    private fun autoImageSlider() {
        //Image Slider
        homeBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.e("TAG", "onPageScrolled: ")
            }

            override fun onPageSelected(position: Int) {
                Log.e("TAG", "onPageSelected: ")
//                homeBinding.txtCount.text = " ${position + 1}/6"
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.e("TAG", "onPageScrollStateChanged: ")
            }
        })

        var sliderAdapter = ImageSliderAdapter(this, imageSliderList) {
            var i = Intent(context, DataDisplayActivity::class.java)
            i.putExtra("Key", it.key)
            i.putExtra("imageSliderList", true)
            startActivity(i)
        }
//        homeBinding.rcvCategory.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        homeBinding.viewPager.adapter = sliderAdapter

        homeBinding.wormDotsIndicator.attachTo(homeBinding.viewPager)



        mDbRef.child("image_slider").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imageSliderList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(ModelClass::class.java)
//                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                    currentUser?.image = postSnapshot.child("image").value.toString()
                    currentUser?.name = postSnapshot.child("name").value.toString()
                    imageSliderList.add(currentUser!!)

//                    }
                }
                dialog.dismiss()
                sliderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

//
//        /*After setting the adapter use the timer */
//
//         sliderRunnable = Runnable {
//            if (currentPage ===  - 1) {
//                currentPage = 0
//            }
//            homeBinding.viewPager.setCurrentItem(currentPage++, true)
//        }
//
//        timer = Timer() // This will create a new Thread
//
//        timer!!.schedule(object : TimerTask() {
//            // task to be scheduled
//            override fun run() {
//                handler.post(sliderRunnable)
//            }
//        }, DELAY_MS, PERIOD_MS)

    }

    private fun popularPlace() {


        popularAdapter = PopularPlaceAdapter(this, popularList) {
            var i = Intent(context, DataDisplayActivity::class.java)
            i.putExtra("Key", it.key)
            i.putExtra("popular", true)
            startActivity(i)
        }
        homeBinding.rcvPopularPlace.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        homeBinding.rcvPopularPlace.adapter = popularAdapter

        mDbRef.child("popular_place").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                popularList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(ModelClass::class.java)
//                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                    popularList.add(currentUser!!)

//                    }
                }
                dialog.dismiss()
                popularAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}