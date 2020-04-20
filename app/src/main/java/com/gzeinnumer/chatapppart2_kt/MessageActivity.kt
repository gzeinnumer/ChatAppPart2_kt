package com.gzeinnumer.chatapppart2_kt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.gzeinnumer.chatapppart2_kt.adapter.MessageAdapter
import com.gzeinnumer.chatapppart2_kt.databinding.ActivityMessageBinding
import com.gzeinnumer.chatapppart2_kt.model.Chat
import com.gzeinnumer.chatapppart2_kt.model.User
import com.gzeinnumer.chatapppart2_kt.notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

//todo 30 part 6 start
class MessageActivity : AppCompatActivity() {
    //todo 33
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    lateinit var binding: ActivityMessageBinding
    lateinit var userId: String
    var notify = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo 34
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        userId = intent.getStringExtra("userId")!!
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        userId.let {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(it)
        }

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue<User>(User::class.java)
                binding.username.text = user?.username
                if (user?.imageURL.equals("default")) {
                    binding.profileImage.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(applicationContext).load(user?.imageURL)
                        .into(binding.profileImage)
                }

                //todo 44
                readMessage(firebaseUser.uid, userId, user?.imageURL!!)
                //end todo 44
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //todo 36
        //proses sent message
        initSentMsg()

        //todo 68 part 14 start
        seenMessage(userId)

        //todo 85
        initFCM()
    }
    //todo 37
    private fun initSentMsg() {
        binding.btnSent.setOnClickListener {
            notify = true
            Log.d("MyZein", "1.");
            val msg: String = binding.msg.text.toString()
            if (msg.isNotEmpty()) {
                sendMessage(firebaseUser.uid, userId, msg)
            } else {
                Toast.makeText(
                    this@MessageActivity,
                    "Isi pesan terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.msg.setText("")
        }
    }

    //todo 38
    private fun sendMessage(
        sender: String,
        receiver: String,
        message: String
    ) {
        var reference = FirebaseDatabase.getInstance().reference
        val hashMap = mapOf("sender" to sender, "receiver" to receiver, "message" to message, "isseen" to false)
        reference.child("Chats").push().setValue(hashMap)

        //todo 72 part 16 start
        val chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(firebaseUser.uid)
            .child(userId)
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(userId)
            .child(firebaseUser.uid)
        chatRef2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef2.child("id").setValue(firebaseUser.uid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        //end todo 72

        //todo 86
        val msg: String = message
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Log.d("MyZein", "1.$notify")
                if (notify) {
                    sendNotification(receiver, user?.username!!, msg)
                }
                notify = false
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //todo 45
    var messageAdapter: MessageAdapter? = null
    var mChat: MutableList<Chat> = mutableListOf()
    private fun readMessage(
        myId: String,
        userId: String,
        imageURL: String
    ) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mChat.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat: Chat = snapshot.getValue<Chat>(Chat::class.java)!!
                    if (chat.receiver.equals(myId) && chat.sender.equals(userId)
                        || chat.receiver.equals(userId) && chat.sender.equals(myId)
                    ) {
                        mChat.add(chat)
                    }
                }
                messageAdapter = MessageAdapter(applicationContext, mChat, imageURL)
                binding.rvData.adapter = messageAdapter
                binding.rvData.layoutManager = LinearLayoutManager(applicationContext)
                binding.rvData.hasFixedSize()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //todo 68 part 14 start
    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)!!
                    if (chat.receiver.equals(firebaseUser.uid) && chat.sender.equals(
                            userId
                        )
                    ) {
                        val hashMap =
                            HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //todo 69
    private fun status(status: String) {
        reference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        val hashMap =
            HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }

    //todo 70
    override fun onResume() {
        super.onResume()
        status("online")
        //todo 92
        currentUser(userId)
        //end todo 92
    }

    //todo 71
    override fun onPause() {
        super.onPause()
        seenListener?.let { reference.removeEventListener(it) }
        status("offline")
        //todo 93
        currentUser("none")
        //end todo 93
    }

    //todo 85
    var apiService: APIService? = null
    fun initFCM() {
        apiService = Client.getCLient("https://fcm.googleapis.com/")?.create(APIService::class.java)
    }

    //todo 87
    private fun sendNotification(
        receiver: String,
        username: String,
        msg: String
    ) {
        Log.d("MyZein", "2.")

        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
        val query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("MyZein", "3.")

                for (snapshot in dataSnapshot.children) {
                    val token: Token? = snapshot.getValue(Token::class.java)
                    Log.d("MyZein", "4.")

                    val data = Data(
                        firebaseUser.uid,
                        R.mipmap.ic_launcher,
                        "$username : $msg",
                        "New Message",
                        userId
                    )
                    val sender = Sender(data, token?.token)
                    apiService!!.sendNotification(sender)
                        ?.enqueue(object : Callback<MyResponse?> {
                            override fun onResponse(
                                call: Call<MyResponse?>?,
                                response: Response<MyResponse?>?
                            ) {
                                Log.d("MyZein", response?.code().toString())
                                if (response?.code() == 200) {
                                    if (response.body()?.success != 1) {
                                        Toast.makeText(
                                            this@MessageActivity,
                                            "Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<MyResponse?>?,
                                t: Throwable
                            ) {
                                Log.d("MyZein", "5.")
                            }
                        })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //todo 91 part 20 start
    private fun currentUser(userId: String) {
        val editor =
            getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        editor.putString("currentUser", userId)
        editor.apply()
    }
}
