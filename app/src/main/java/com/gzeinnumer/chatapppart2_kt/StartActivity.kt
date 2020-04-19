package com.gzeinnumer.chatapppart2_kt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gzeinnumer.chatapppart2_kt.databinding.ActivityStartBinding

//todo 11
class StartActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //todo 13
        binding.btnLogin.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    LoginActivity::class.java
                )
            )
        }
        binding.btnRegister.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    RegisterActivity::class.java
                )
            )
        }
    }

    //todo 14
    private var firebaseUser : FirebaseUser? = null
    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }
}
