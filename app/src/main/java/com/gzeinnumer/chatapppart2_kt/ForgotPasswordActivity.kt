package com.gzeinnumer.chatapppart2_kt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.gzeinnumer.chatapppart2_kt.databinding.ActivityForgotPasswordBinding

//todo 73
class ForgotPasswordActivity : AppCompatActivity() {
    //todo 74
    lateinit var binding: ActivityForgotPasswordBinding
    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo 75
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btn.setOnClickListener(View.OnClickListener {
            val email: String = binding.email.getText().toString()
            if (email.isEmpty()) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Email tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                firebaseAuth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Silahkan cek email",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                applicationContext,
                                StartActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
