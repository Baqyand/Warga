package com.saku.warga.apihelper

import com.google.gson.annotations.SerializedName

class LoginResponse {


    @SerializedName("token")
    var token: String? = null
    @SerializedName("token_type")
    var token_type: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("expires_in")
    var expires_in: String? = null
        get() = field
        set(value) {
            field = value
        }

}