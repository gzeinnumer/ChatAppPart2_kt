package com.gzeinnumer.chatapppart2_kt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gzeinnumer.chatapppart2_kt.R
import com.gzeinnumer.chatapppart2_kt.model.Chat

class MessageAdapter(val context:Context, val mChats: List<Chat>, val imageURL: String) :
    RecyclerView.Adapter<MessageAdapter.MyHolder>() {
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_right, parent, false)
            MyHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_left, parent, false)
            MyHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(context, mChats[position], imageURL, position, mChats.size)
    }

    override fun getItemCount(): Int {
        return mChats.size
    }

    class MyHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var showMessage: TextView = itemView.findViewById(R.id.show_message)
        var profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        var seen: TextView = itemView.findViewById(R.id.txt_seen)
        fun bind(
            context: Context?,
            chat: Chat,
            imageURL: String,
            position: Int,
            size: Int
        ) {
            showMessage.text = chat.message
            if (imageURL == "default") {
                profileImage.setImageResource(R.mipmap.ic_launcher)
            } else {
                Glide.with(context!!).load(imageURL).into(profileImage)
            }
            if (position == size- 1) {
                if (chat.isseen!!) {
                    seen.text = "Seen"
                } else {
                    seen.text = "Delivered"
                }
            } else {
                seen.visibility = View.GONE
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (mChats[position].sender.equals(firebaseUser!!.uid)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    companion object {
        private const val MSG_TYPE_LEFT = 0
        private const val MSG_TYPE_RIGHT = 1
    }
}