package com.mytrip.myindiatrip.model

class UserModelClass {

    var image: String?=null
    var firstName: String?=null
    var lastName: String?=null
    var email: String?=null
    var uid: String?=null

    constructor(){}

    constructor( image: String, firstName: String, lastName: String, email: String, uid: String){
        this.image=image
        this.firstName=firstName
        this.lastName=lastName
        this.email=email
        this.uid=uid
    }
}