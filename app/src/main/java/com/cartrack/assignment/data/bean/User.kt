package com.cartrack.assignment.data.bean

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String,
    @SerializedName("username") val userName : String,
    @SerializedName("email") val email : String,
    @SerializedName("address") val address : Address,
    @SerializedName("phone") val phone : String,
    @SerializedName("website") val website : String,
    @SerializedName("company") val company : Company
){
    data class Address(
        @SerializedName("street") val street : String,
        @SerializedName("suite") val suite : String,
        @SerializedName("city") val city : String,
        @SerializedName("zipcode") val zipCode : String,
        @SerializedName("geo") val geo : Geo
    ){
        data class Geo(
            @SerializedName("lat") val lat : String,
            @SerializedName("lng") val lng : String
        )
    }

    data class Company(
        @SerializedName("name") val name : String,
        @SerializedName("catchPhrase") val catchPharse : String,
        @SerializedName("bs") val bs : String
    )
}