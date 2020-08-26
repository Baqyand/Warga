package com.saku.warga.models

import com.google.gson.annotations.SerializedName

data class TransaksiData(
    val id:String,
    @SerializedName("keterangan")
    val keterangan:String,
    @SerializedName("gambar")
    val gambar: Int,
    @SerializedName("tgl")
    val tgl:String,
    @SerializedName("nilai1")
    val nilai:String,
    @SerializedName("status")
    val status:String,
    @SerializedName("jenis")
    val jenis:String,
    @SerializedName("pesan")
    val pesan:String
)