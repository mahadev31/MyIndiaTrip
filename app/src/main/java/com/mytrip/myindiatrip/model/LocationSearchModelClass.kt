package com.mytrip.myindiatrip.model

class LocationSearchModelClass {

    lateinit var cityName:String
    lateinit var stateName:String

    constructor(){

    }

    constructor(cityName:String,stateName:String){
        this.cityName=cityName
        this.stateName=stateName
    }
}