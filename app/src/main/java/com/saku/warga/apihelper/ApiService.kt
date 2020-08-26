package com.saku.warga.apihelper

import com.saku.warga.apihelper.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("no_hp") no_hp: String?,
        @Field("password") password: String?,
        @Field("id_device") id_device: String?
//        @Field("password") password: String?
    ): Call<ResponseBody>

    @GET("notif-all")
    fun notifikasi(
    ): Call<ResponseBody>

    @GET("filter-tahun-bill")
    fun filter_tahun_bill(
    ): Call<ResponseBody>

    @GET("filter-bulan")
    fun list_bulan(
    ): Call<ResponseBody>

    @GET("rekap-rw-bulan")
    fun rekap_rw_bulan(
        @Query("tahun") tahun:String,
        @Query("bulan") bulan:String
    ): Call<ResponseBody>

    @GET("rekap-rw")
    fun rekap_rw_tahun(
        @Query("tahun") tahun:String
    ): Call<ResponseBody>

    @GET("bulan")
    fun rekap_rw_list_bulan(
    ): Call<ResponseBody>

    @GET("filter-tahun")
    fun list_tahun(
    ): Call<ResponseBody>

    @GET("iuran-detail")
    fun kartu_iuran(
        @Query("tahun") tahun:String
    ): Call<ResponseBody>

    @GET("riwayat-trans")
    fun riwayat_transaksi(
        @Query("periode") periode:String
    ): Call<ResponseBody>

    @GET("riwayat-iuran")
    fun riwayat_iuran(
        @Query("kode_pp") kode_pp:String,
        @Query("kode_rumah") kode_rumah:String,
        @Query("periode") periode:String
    ): Call<ResponseBody>

    @GET("info")
    fun informasi(
    ): Call<ResponseBody>

    @GET("profile")
    fun profile_warga(
    ): Call<ResponseBody>

    @GET("iuran-detail")
    fun iuranDetail(
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("ubah-profile")
    fun ubahProfile(
        @Field("nama") nama: String?,
        @Field("alias") alias: String?,
        @Field("no_hp") no_hp: String?
    ): Call<ResponseBody>


    @Multipart
    @POST("ubah-profile")
    fun ubahProfileWithImage(
        @Part foto: MultipartBody.Part,
        @Part("nama") nama: RequestBody?,
        @Part("alias") alias: RequestBody?,
        @Part("no_hp") no_hp: RequestBody?
    ): Call<ResponseBody>
}