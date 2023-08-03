package com.mytrip.myindiatrip.model

 class HotelSearchModelClass{
      var  hotelImage:String?=null
      var  hotelName:String?=null
      var  hotelRent:String?=null
      var  hotelRating:String?=null

     constructor(){ }

     constructor(hotelImage:String?,hotelName:String?, hotelRent:String?, hotelRating:String?){
         this.hotelImage=hotelImage
         this.hotelName=hotelName
         this.hotelRent=hotelRent
         this.hotelRating=hotelRating
     }
}

