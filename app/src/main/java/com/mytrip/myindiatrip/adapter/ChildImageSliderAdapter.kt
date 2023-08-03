package com.mytrip.myindiatrip.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.activity.DataDisplayActivity
import com.mytrip.myindiatrip.model.ModelClass
import java.util.*
import kotlin.collections.ArrayList

class ChildImageSliderAdapter(
   var context: Context,
 var   childSliderList: ArrayList<ModelClass>
) : PagerAdapter() {
    override fun getCount(): Int {
        return childSliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {


        val itemView: View =
            LayoutInflater.from(container.context).inflate(R.layout.image_silder_list, container, false)


        val imageView: ImageView = itemView.findViewById<View>(R.id.imgSliderView) as ImageView



        Glide.with(context).load(childSliderList[position].slider_image)
            .placeholder(R.drawable.ic_image).into(imageView)

        Log.e("TAG", "image slider: " + childSliderList[position].slider_image)


        Objects.requireNonNull(container).addView(itemView)


        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as RelativeLayout)
    }
}