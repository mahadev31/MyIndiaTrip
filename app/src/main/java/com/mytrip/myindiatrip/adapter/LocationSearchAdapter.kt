package com.mytrip.myindiatrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytrip.myindiatrip.R
import com.mytrip.myindiatrip.activity.SearchLocationActivity
import com.mytrip.myindiatrip.model.LocationSearchModelClass

class LocationSearchAdapter(
   var searchLocationActivity: SearchLocationActivity,
 var   searchList: ArrayList<LocationSearchModelClass>
) :RecyclerView.Adapter<LocationSearchAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var txtCityName:TextView=itemView.findViewById(R.id.txtCityName)
        var txtStateCountry:TextView=itemView.findViewById(R.id.txtStateCountry)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v=LayoutInflater.from(parent.context).inflate(R.layout.location_search_list,parent,false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
       return  searchList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      holder.txtCityName.text=searchList[position].cityName
      holder.txtStateCountry.text=searchList[position].stateName
    }
}