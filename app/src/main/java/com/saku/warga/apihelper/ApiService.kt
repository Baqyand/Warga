package com.saku.warga.apihelper

import com.saku.warga.apihelper.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST("login_warga")
    fun login(
        @Field("no_hp") no_hp: String?,
        @Field("password") password: String?
//        @Field("password") password: String?
    ): Call<ResponseBody>

    @GET("profile_warga")
    fun profile_warga(
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("ubah_profile")
    fun ubahProfile(
        @Field("nama") nama: String?,
        @Field("alias") alias: String?,
        @Field("no_hp") no_hp: String?
    ): Call<ResponseBody>


    @Multipart
    @POST("ubah_profile")
    fun ubahProfileWithImage(
        @Part foto: MultipartBody.Part,
        @Part("nama") nama: RequestBody?,
        @Part("alias") alias: RequestBody?,
        @Part("no_hp") no_hp: RequestBody?
    ): Call<ResponseBody>
}