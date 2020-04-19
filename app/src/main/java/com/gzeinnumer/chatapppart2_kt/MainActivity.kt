package com.gzeinnumer.chatapppart2_kt

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.gzeinnumer.chatapppart2_kt.adapter.ViewPagerAdapter
import com.gzeinnumer.chatapppart2_kt.databinding.ActivityMainBinding
import com.gzeinnumer.chatapppart2_kt.fragment.ChatsFragment
import com.gzeinnumer.chatapppart2_kt.fragment.ProfileFragment
import com.gzeinnumer.chatapppart2_kt.fragment.UsersFragment
import com.gzeinnumer.chatapppart2_kt.model.User
import java.util.*

class MainActivity : AppCompatActivity() {
    //todo 16
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo 17
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue<User>(User::class.java)
                user?.let {
                    binding.username.text = it.username
                    if (it.imageURL == "default") {
                        binding.profileImage.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        Glide.with(applicationContext).load(it.imageURL)
                            .into(binding.profileImage)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        //todo 22
        //buat 3 fragment, ChatsFragment, ProfileFragment, UsersFragment
        initPager()
    }

    //todo 18
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    //todo 19
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, StartActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
            return true
        }
        return false
    }

    //todo 23
    private fun initPager() {
        val viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
        viewPagerAdapter.addFragment(UsersFragment(), "Users")
        viewPagerAdapter.addFragment(ProfileFragment(), "Profile")
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }


    //todo 61 part 12 start
    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        val hashMap = mapOf("status" to status)
        reference.updateChildren(hashMap)
    }

    //todo 62
    override fun onResume() {
        super.onResume()
        status("online")
    }

    //todo 63
    override fun onPause() {
        super.onPause()
        status("offline")
    }
}
