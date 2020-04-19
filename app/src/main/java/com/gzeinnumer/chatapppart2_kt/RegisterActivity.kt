package com.gzeinnumer.chatapppart2_kt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gzeinnumer.chatapppart2_kt.databinding.ActivityRegisterBinding
import java.util.*

//todo 2
class RegisterActivity : AppCompatActivity() {

    //todo 4
    lateinit var binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth
    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //todo 5
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar!!.title = "Register Kotlin"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.btn.setOnClickListener(View.OnClickListener {
            val username: String = binding.username.text.toString()
            val email: String = binding.email.text.toString()
            val pass: String = binding.pass.text.toString()
            when {
                username.isEmpty() -> {
                    binding.username.error = "Masih Kosong"
                }
                email.isEmpty() -> {
                    binding.email.error = "Masih Kosong"
                }
                pass.isEmpty() -> {
                    binding.pass.error = "Masih Kosong"
                }
                else -> {
                    register(username, email, pass)
                }
            }
        })
    }

    //todo 6
    private fun register(
        username: String,
        email: String,
        pass: String
    ) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser!!
                val userId = firebaseUser.uid
                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                val hashMap = mapOf(
                    "id" to userId,
                    "username" to username,
                    "imageURL" to "default",
                    "status" to "offline",
                    "search" to username.toLowerCase()
                )
                reference.setValue(hashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent =
                            Intent(applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Tidak bisa register dengan email ini!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
