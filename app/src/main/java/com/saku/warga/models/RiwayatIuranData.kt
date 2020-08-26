package com.saku.warga.models

import com.google.gson.annotations.SerializedName

data class RiwayatIuranData (
    @SerializedName("no_bukti")
    val noBukti:String,
    @SerializedName("keterangan")
    val keterangan:String,
    @SerializedName("tgl")
    val tgl:String,
    @SerializedName("nilai1")
    val nilai:String
)