package com.gzeinnumer.chatapppart2_kt.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.gzeinnumer.chatapppart2_kt.R
import com.gzeinnumer.chatapppart2_kt.databinding.FragmentProfileBinding
import com.gzeinnumer.chatapppart2_kt.model.User
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    //todo 53
    lateinit var binding: FragmentProfileBinding
    lateinit var reference: DatabaseReference
    lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    //todo 54
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue<User>(User::class.java)
                binding.username.text = user?.username
                if (user?.imageURL.equals("default")) {
                    binding.profileImage.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(activity!!).load(user?.imageURL).into(binding.profileImage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //todo 55 part 11 start
        initStorageReference()
    }

    //todo 56
    lateinit var storageReference: StorageReference
    private val IMAGE_REQUEST = 1
    private var imageURI: Uri? = null
    private var uploadTask: StorageTask<*>? = null

    private fun initStorageReference() {
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        binding.profileImage.setOnClickListener { openImage() }
    }

    //todo 57
    private fun openImage() {
        Log.d("MyZein", "openImage")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    //todo 58
    private fun getFileExtention(uri: Uri): String? {
        Log.d("MyZein", "getFileExtention")
        val contentResolver = activity!!.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    //todo 59
    private fun uploadImage() {
        Log.d("MyZein", "uploadImage")
        val pd = ProgressDialog(context)
        pd.setMessage("Uploading...")
        pd.show()
        if (imageURI != null) {
            //            Toast.makeText(getContext(), getFileExtention(imageURI), Toast.LENGTH_SHORT).show();
            val fileReference = storageReference.child(
                System.currentTimeMillis().toString() + "." + getFileExtention(imageURI!!)
            )
            uploadTask = fileReference.putFile(imageURI!!)
            (uploadTask as UploadTask).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                fileReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val mUri = downloadUri.toString()
                    reference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(firebaseUser.uid)
                    val hashMap =
                        HashMap<String, Any>()
                    hashMap["imageURL"] = mUri
                    reference.updateChildren(hashMap)
                    pd.dismiss()
                } else {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                    pd.dismiss()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    //todo 60
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MyZein", "onActivityResult")
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageURI = data.data
            Toast.makeText(context, getFileExtention(imageURI!!), Toast.LENGTH_SHORT).show()
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }
        }
    }
}
