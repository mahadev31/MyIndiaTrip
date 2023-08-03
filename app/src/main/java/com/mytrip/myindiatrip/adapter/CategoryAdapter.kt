package com.mytrip.myindiatrip.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.fragment.HomeFragment
import com.mytrip.myindiatrip.model.ModelClass

class CategoryAdapter(
    var homeFragment: HomeFragment,
    var categoryList: ArrayList<ModelClass>,
    var click:((ModelClass)-> Unit)
) : RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
     var selectedItemPosition = 0

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.imgCategory)
        var categoryName: TextView = itemView.findViewById(R.id.txtCategory)
        var linCategory: LinearLayout = itemView.findViewById(R.id.linCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.categoryName.text = categoryList[position].category_name

        Glide.with(homeFragment).load(categoryList[position].category_image)
            .placeholder(R.drawable.ic_image).into(holder.image)

        Log.e("TAG", "onBindViewHolder: " + categoryList[position].category_image)

        holder.linCategory.setOnClickListener {
            selectedItemPosition = position
            click.invoke(categoryList[position])

            notifyDataSetChanged()


        }
        if (selectedItemPosition == position) {
            holder.linCategory.setBackgroundColor(Color.parseColor("#F6AA50"))
            holder.categoryName.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.linCategory.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.categoryName.setTextColor(Color.parseColor("#000000"))
        }
    }


}