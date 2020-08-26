package com.saku.warga.models

import com.google.gson.annotations.SerializedName

data class ModelKartu (
    @SerializedName("bulan")
    val bulan:String,
    @SerializedName("tagihan")
    val tagihan:String,
    @SerializedName("bayar")
    val bayar:String
//    ,
//    @SerializedName("total")
//    val total:String
)