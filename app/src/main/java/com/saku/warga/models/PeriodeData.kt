package com.saku.warga.models

import com.google.gson.annotations.SerializedName

data class PeriodeData (
    @SerializedName("periode")
    val periode:String,
    @SerializedName("tahun")
    val tahun:String,
    @SerializedName("bulan")
    val bulan:String,
    @SerializedName("nama")
    val nama:String


    )