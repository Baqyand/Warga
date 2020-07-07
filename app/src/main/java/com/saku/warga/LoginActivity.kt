package com.saku.warga

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saku.warga.apihelper.UtilsApi
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    var preferences  = Preferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        preferences.setPreferences(this)
        checkLogin()
        passwordText()
        btn_login.setOnClickListener {
            login(et_no_hp.text.toString(),et_password.text.toString())
        }
    }

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
                    Toast.makeText(this@LoginActivity, "Maksimal password 6 digit.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun login(no_hp:String,password:String){
        val utilsapi= UtilsApi().getAPIService(this@LoginActivity)
        utilsapi?.login(no_hp,password)?.enqueue(object : Callback<ResponseBody?> {
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
