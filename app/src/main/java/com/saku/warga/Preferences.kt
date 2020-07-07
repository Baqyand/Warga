package com.saku.warga

import android.content.Context
import android.content.SharedPreferences

class Preferences {

    var APP_NAME = "PORTAL"

    private var log_status = "log_status"
    private var token_type = "token_type"
    private var expires = "expires"
    private var token = "token"

    var sp: SharedPreferences? = null
    var spEditor: SharedPreferences.Editor? = null

    fun setPreferences(context: Context) {
        sp = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        spEditor = sp?.edit()
    }

    fun preferencesLogout() {
        spEditor!!.clear()
        spEditor!!.commit()
    }



    fun saveLogStatus(value: Boolean) {
        spEditor!!.putBoolean(log_status, value)
        spEditor!!.commit()
    }

    fun saveToken(value: String?) {
        spEditor!!.putString(token, value)
        spEditor!!.commit()
    }

    fun saveTokenType(value: String?) {
        spEditor!!.putString(token_type, value)
        spEditor!!.commit()
    }

    fun saveExpires(value: String?) {
        spEditor!!.putString(expires, value)
        spEditor!!.commit()
    }

    fun getLogStatus(): Boolean {
        return sp!!.getBoolean(log_status, false)
    }

    fun getExpires(): String? {
        return sp!!.getString(expires, "N/A")
    }

    fun getTokenType(): String? {
        return sp!!.getString(token_type, "N/A")
    }

    fun getToken(): String? {
        return sp!!.getString(token, "N/A")
    }
}