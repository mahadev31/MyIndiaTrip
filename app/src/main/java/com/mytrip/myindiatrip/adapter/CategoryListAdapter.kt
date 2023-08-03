package com.mytrip.myindiatrip.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.fragment.HomeFragment
import com.mytrip.myindiatrip.model.ModelClass

class CategoryListAdapter(
    var homeFragment: HomeFragment,
    var categoryItemList: ArrayList<ModelClass>,
    var click:(ModelClass)->Unit
) : RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>() {
//    private var selectedItemPosition: Int = -1

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.imgPopular)
        var PopularName: TextView = itemView.findViewById(R.id.txtPopular)
        var layPopular: RelativeLayout = itemView.findViewById(R.id.layPopular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.popular_item_list, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return categoryItemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.PopularName.text = categoryItemList[position].name

        Glide.with(homeFragment).load(categoryItemList[position].image)
            .placeholder(R.drawable.ic_image).into(holder.image)

        Log.e("TAG", "image place_image: " + categoryItemList[position].image)

        holder.layPopular.setOnClickListener {
            click.invoke(categoryItemList[position])
        }



    }
}