package com.gzeinnumer.chatapppart2_kt.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gzeinnumer.chatapppart2_kt.adapter.UserAdapter
import com.gzeinnumer.chatapppart2_kt.databinding.FragmentUsersBinding
import com.gzeinnumer.chatapppart2_kt.model.User

/**
 * A simple [Fragment] subclass.
 */
class UsersFragment : Fragment() {
    //todo 27
    lateinit var binding: FragmentUsersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo 28
        readUsers()

        //todo 64 part 13 start
        initSearchView()
    }

    //todo 29
    lateinit var userAdapter: UserAdapter
    private val mUsers: MutableList<User> = mutableListOf()
    private fun readUsers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (binding.searchUsers.text.isEmpty()){
                    mUsers.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user: User? = snapshot.getValue<User>(User::class.java)
                        user?.let {
                            if (!it.id.equals(firebaseUser!!.uid)) {
                                mUsers.add(it)
                            }
                        }
                    }
                    userAdapter = UserAdapter(mUsers, true)
                    binding.rvData.adapter = userAdapter
                    binding.rvData.layoutManager = LinearLayoutManager(activity)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    //todo 65
    private fun initSearchView() {
        binding.searchUsers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                searchUsers(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    //todo 66
    private fun searchUsers(s: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val query =
            FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(s)
                .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!
                    assert(firebaseUser != null)
                    if (!user.id.equals(firebaseUser!!.uid)) {
                        mUsers.add(user)
                    }
                }
                userAdapter = UserAdapter(mUsers, true)
                binding.rvData.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
