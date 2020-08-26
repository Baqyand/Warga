package com.saku.warga.apihelper

import android.content.Context
import com.saku.warga.Preferences
import com.saku.warga.apihelper.Constant.Companion.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SimpleBuilder {
    companion object {
        private lateinit var context: Context
        fun setContext(con: Context) {
            context = con
        }

        private val retrofit by lazy {

            val preferences = Preferences()
            preferences.setPreferences(context)
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val intercept: Interceptor = object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader(
                            "Authorization",
                            preferences.getTokenType() + " " + preferences.getToken()!!
                        )
                        .build()
                    return chain.proceed(newRequest)
                }
            }
            val client =
                OkHttpClient.Builder().addInterceptor(intercept).addInterceptor(interceptor)
                    .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }

        val api by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}