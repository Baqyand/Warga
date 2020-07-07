package com.saku.warga

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.saku.warga.fragments.BerandaFragment
import com.saku.warga.fragments.KartuFragment
import com.saku.warga.fragments.NotifikasiFragment
import com.saku.warga.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.frame_layout, BerandaFragment()).commit()
        bottom_navigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            when(item.itemId){
                R.id.menu_beranda -> {
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.frame_layout, BerandaFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_kartu -> {
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.frame_layout, KartuFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_notifikasi -> {
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.frame_layout, NotifikasiFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.frame_layout, ProfileFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }

    fun encrypt(){
        /* User types in their password: */

        /* User types in their password: */
        val password = "password"

        /* Store these things on disk used to derive key later: */

        /* Store these things on disk used to derive key later: */
        val iterationCount = 1000
        val saltLength = 32 // bytes; should be the same size as the output (256 / 8 = 32)

        val keyLength = 256 // 256-bits for AES-256, 128-bits for AES-128, etc

//        var salt: ByteArray // Should be of saltLength


        /* When first creating the key, obtain a salt with this: */

        /* When first creating the key, obtain a salt with this: */
        val random = SecureRandom()
        val salt = ByteArray(saltLength)
        random.nextBytes(salt)

        /* Use this to derive the key from the password: */

        /* Use this to derive the key from the password: */
        val keySpec: KeySpec = PBEKeySpec(
            password.toCharArray(), salt,
            iterationCount, keyLength
        )
        val keyFactory: SecretKeyFactory = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA1")
        val keyBytes: ByteArray = keyFactory.generateSecret(keySpec).encoded
        val key: SecretKey = SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt2(){

    }



    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Yakin ingin keluar?")
        builder.setCancelable(true)
        builder.setPositiveButton("Ya") { _, _ -> super.onBackPressed() }
        builder.setNegativeButton("Tidak") { _, _ ->

        }
        val dialog = builder.create()
        dialog.show()
    }


}
