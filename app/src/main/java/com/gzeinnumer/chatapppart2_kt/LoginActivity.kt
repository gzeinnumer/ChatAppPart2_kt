package com.gzeinnumer.chatapppart2_kt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.gzeinnumer.chatapppart2_kt.databinding.ActivityLoginBinding

//todo 8
class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo 9
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar!!.title = "Login Kotlin"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.btn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()
            when {
                email.isEmpty() -> {
                    binding.email.error = "Masih Kosong"
                }
                pass.isEmpty() -> {
                    binding.pass.error = "Masih Kosong"
                }
                else -> {
                    login(email, pass)
                }
            }
        }

        //todo 72 part 15 start
        binding.forgotPass.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    ForgotPasswordActivity::class.java
                )
            )
        })
    }

    //todo 10
    private fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Tidak bisa register dengan email ini!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
