package com.gzeinnumer.chatapppart2_kt.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.gzeinnumer.chatapppart2_kt.adapter.UserAdapter
import com.gzeinnumer.chatapppart2_kt.databinding.FragmentChatsBinding
import com.gzeinnumer.chatapppart2_kt.model.ChatList
import com.gzeinnumer.chatapppart2_kt.model.User
import com.gzeinnumer.chatapppart2_kt.notification.Token

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {

    //todo 47
    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference
    var usersList: MutableList<ChatList>? = mutableListOf()
    var mUsers: MutableList<User>? = mutableListOf()
    lateinit var userAdapter: UserAdapter
    lateinit var binding: FragmentChatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //todo 48
        binding = FragmentChatsBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo 49
        readMessageNew()

        //todo 81
        updateToken(FirebaseInstanceId.getInstance().token)
    }
    //todo 50
    private fun readMessageNew() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference =
            FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val chatList = snapshot.getValue(ChatList::class.java)!!
                    usersList?.add(chatList)
                }
                chatList()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //todo 51
    private fun chatList() {
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!
                    for (chatList in usersList!!) {
                        if (user.id.equals(chatList.id)) {
                            mUsers?.add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(mUsers!!, true)
                binding.rvData.adapter = userAdapter
                binding.rvData.layoutManager = LinearLayoutManager(activity)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //todo 82
    fun updateToken(token: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        reference.child(firebaseUser.uid).setValue(Token(token))
    }
}
