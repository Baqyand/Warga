package com.saku.warga

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.saku.warga.apihelper.UtilsApi
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    var preferences  = Preferences()
    var idDevice : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        preferences.setPreferences(this)
        firebaseInstance()
        checkLogin()
        passwordText()
        app_version.text = "Version " + BuildConfig.VERSION_NAME
        btn_login.setOnClickListener {
            login(et_no_hp.text.toString(),et_password.text.toString(),idDevice!!)
        }
    }

    fun firebaseInstance(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FIREBASE", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                idDevice = task.result?.token
//                Log.e("FIREBASE",idDevice)
                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }

//    private fun sendNotif(
//        to: String,
//        title: String,
//        body: String
//    ) {
//        val mainObj = JSONObject()
//        try {
//            mainObj.put("to", to)
//            val notifObj = JSONObject()
//            notifObj.put("title", title)
//            notifObj.put("body", body)
//            mainObj.put("notification", notifObj)
//            val request: JsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, URL,
//                mainObj,
//                object : Listener<JSONObject?>() {
//                    fun onResponse(response: JSONObject?) {
//                        Toast.makeText(
//                            applicationContext,
//                            "Notifikasi Berhasil Dikirim",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }, object : ErrorListener() {
//                    fun onErrorResponse(error: VolleyError?) {
//                        Toast.makeText(
//                            applicationContext,
//                            "Notifikasi Gagal Dikirim",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            ) {
//                //                    return super.getHeaders();
//                @get:Throws(AuthFailureError::class)
//                val headers: Map<String, String>?
//                    get() {
//                        //                    return super.getHeaders();
//                        val header: MutableMap<String, String> =
//                            HashMap()
//                        header["content-type"] = "application/json"
//                        header["authorization"] = "key=AIzaSyA3ZBLGd1_RAe17Ra29FhZR2ShMhTH4TnE"
//                        return header
//                    }
//            }
//            requestQue.add(request)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//    }

    private fun passwordText(){
        et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if(et_password.isFocused && et_password.text.toString().trim().length > 6){
                    et_password.setText(s.toString().substring(0, 6))
                    et_password.setSelection(s.length-1)
                    Snackbar.make(findViewById(android.R.id.content),"Maksimal password 6 digit",
                        Snackbar.LENGTH_SHORT).show()

//                    Toast.makeText(this@LoginActivity, "Maksimal password 6 digit.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun login(no_hp:String,password:String,id_device:String){
        val utilsapi= UtilsApi().getAPIService(this@LoginActivity)
        utilsapi?.login(no_hp,password,id_device)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            preferences.saveToken(obj.optString("token"))
                            preferences.saveExpires(obj.optString("expires"))
                            preferences.saveTokenType(obj.optString("token_type"))
                            preferences.saveNoHp(no_hp)
                            preferences.saveLogStatus(true)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.zoom_enter,0)
                            finishAffinity()
                        } catch (e: Exception) {

                            Toast.makeText(this@LoginActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@LoginActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    }
                } else if(response.code() == 422) {
                    Toast.makeText(this@LoginActivity, "Username/Password masih kosong", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 401){
                    Toast.makeText(this@LoginActivity, "Username/Password salah", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 403){
                    Toast.makeText(this@LoginActivity, "Token Invalid", Toast.LENGTH_SHORT).show()
                } else if(response.code() == 404 || response.code() == 405){
                    Toast.makeText(this@LoginActivity, "Terjadi kesalahan server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkLogin(){
        if(preferences.getLogStatus()){
            val intent =
                Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    override fun afterTextChanged(s: Editable?) {
//        if(et_password.isFocused && et_password.text.toString().trim().length > 6){
//            et_password.setText(s.toString().substring(0, 6));
//            s?.length?.minus(1)?.let { et_password.setSelection(it) }
//            Toast.makeText(this, "Maximum number of characters reached.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//    }
//
//    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//    }
}
