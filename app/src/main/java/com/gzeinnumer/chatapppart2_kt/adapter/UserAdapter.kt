package com.gzeinnumer.chatapppart2_kt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gzeinnumer.chatapppart2_kt.MessageActivity
import com.gzeinnumer.chatapppart2_kt.R
import com.gzeinnumer.chatapppart2_kt.databinding.UserItemBinding
import com.gzeinnumer.chatapppart2_kt.model.Chat
import com.gzeinnumer.chatapppart2_kt.model.User


//todo 26
class UserAdapter(var listUser: List<User>, var isChat: Boolean) :
    RecyclerView.Adapter<UserAdapter.MyHolder>() {
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding: UserItemBinding =
            UserItemBinding.inflate(LayoutInflater.from(parent.context))
        context = parent.context
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(context, listUser[position])
        //todo 73 part 17 start
        if (isChat) {
            lastMessage(listUser[position].id!!, holder.binding.lastMsg)
        } else {
            holder.binding.lastMsg.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class MyHolder internal constructor(itemView: UserItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: UserItemBinding = itemView
        fun bind(context: Context?, user: User) {
            binding.username.text = user.username
            if (user.imageURL.equals("default")) {
                binding.profileImage.setImageResource(R.mipmap.ic_launcher)
            } else {
                Glide.with(context!!).load(user.imageURL).into(binding.profileImage)
            }

            if (isChat) {
                if (user.status.equals("online")) {
                    binding.imgOn.visibility = View.VISIBLE
                    binding.imgOff.visibility = View.GONE
                } else {
                    binding.imgOn.visibility = View.GONE
                    binding.imgOff.visibility = View.VISIBLE
                }
            } else {
                binding.imgOn.visibility = View.GONE
                binding.imgOff.visibility = View.INVISIBLE
            }

            //todo 32
            itemView.setOnClickListener {
                context?.startActivity(Intent(context, MessageActivity::class.java).apply {
                    putExtra("userId", user.id)
                })
            }
            //end todo 32
        }
    }

    //todo 74
    var theLastMessage: String? = null

    private fun lastMessage(userId: String, lastMsg: TextView) {
        theLastMessage = "default"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)!!
                    if (chat.receiver.equals(firebaseUser!!.uid) && chat.sender.equals(
                            userId
                        ) ||
                        chat.receiver.equals(userId) && chat.sender.equals(
                            firebaseUser.uid
                        )
                    ) {
                        theLastMessage = chat.message
                    }
                }
                if ("default" == theLastMessage) {
                    lastMsg.text = "No Mesaage"
                } else {
                    lastMsg.text = theLastMessage
                }
                theLastMessage = "default"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}