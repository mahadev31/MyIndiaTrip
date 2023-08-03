package com.mytrip.myindiatrip.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.model.ModelClass


class SaveAdapter(
    var context: Context,
    var click: (ModelClass) -> Unit,
    var save: (Int, String) -> Unit
) :   //create invoke
    RecyclerView.Adapter<SaveAdapter.MyViewHolder>() {
    var placeList = ArrayList<ModelClass>()
    lateinit var mDbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    //set array list in model class

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPlace: ImageView = itemView.findViewById(R.id.imgPlace)
        var txtPlaceName: TextView = itemView.findViewById(R.id.txtPlaceName)
        var txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        var txtPlaceRating: TextView = itemView.findViewById(R.id.txtPlaceRating)
        var cdSearchView: CardView = itemView.findViewById(R.id.cdSearchView)
        var imgSave: ImageView = itemView.findViewById(R.id.imgSave)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item_list, parent, false)  //set xml file
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return placeList.size   //array list size set
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(placeList[position].image)
            .placeholder(R.drawable.ic_image).into(holder.imgPlace)

        holder.txtPlaceName.text = placeList[position].name
        holder.txtLocation.text = placeList[position].location
        holder.txtPlaceRating.text = placeList[position].rating


        holder.cdSearchView.setOnClickListener {
            click.invoke(placeList[position])
        }
        //like
        holder.imgSave.setImageResource(R.drawable.save_fill)


        //like
        holder.imgSave.setOnClickListener {


            save.invoke(0, placeList[position].key!!)
            placeList[position].save = 0

            Log.e("TAG", "Favorite: " + placeList[position].save)

            //click button and set unlike

            deleteItem(position)  //create function and set position







        }
    }




    //pass function in activity
    fun updateList(placeList: ArrayList<ModelClass>) {
        this.placeList = placeList
        notifyDataSetChanged()

    }

    private fun deleteItem(position: Int) {


        placeList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, placeList.size)
    }




}