package com.gzeinnumer.chatapppart2_kt.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gzeinnumer.chatapppart2_kt.MessageActivity


//todo 84
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        Log.d("MyZein", "6. $s")
        val token = FirebaseInstanceId.getInstance().token
        if (firebaseUser != null) {
            updateToken(token)
        }
    }

    fun updateToken(token: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token1 = Token(token)
        reference.child(firebaseUser!!.uid).setValue(token1)
        Log.d("MyZein", "7. ")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val sented = remoteMessage.data["sented"]

        Log.d("MyZein", "8. "+sented)
        val firebaseUser = FirebaseAuth.getInstance().currentUser

//        if(firebaseUser != null && sented.equals(firebaseUser.getUid())){
//            sendNotification(remoteMessage);
//        }
        //todo 89
        //komentarkan yang diatas
        if (firebaseUser != null && sented == firebaseUser.uid) {
            //todo 94
            val preferences =
                getSharedPreferences("PREFS", Context.MODE_PRIVATE)
            val currentUser = preferences.getString("currentUser", "none")
            val user = remoteMessage.data["user"]
            //end todo 94
            //todo 95-1
            if (currentUser != user) {
                //end todo 95-1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendNotificationOreo(remoteMessage)
                } else {
                    sendNotification(remoteMessage)
                }
                //todo 95-2
            }
            //end todo 95-2
        }
        //end todo 89
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        Log.d("MyZein", "9. ")
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val notification = remoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )
        val builder =
            NotificationCompat.Builder(this)
                .setSmallIcon(icon!!.toInt())
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
        val noti =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var i = 0
        if (j > 0) {
            i = j
        }
        assert(noti != null)
        noti.notify(i, builder.build())
    }

    //todo 90
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun sendNotificationOreo(remoteMessage: RemoteMessage) {
        Log.d("MyZein", "10. ")
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val notification = remoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = OreoNotification(this)
        val builder =
            oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon!!)
        var i = 0
        if (j > 0) {
            i = j
        }
        oreoNotification.notiManager?.notify(i, builder.build())
    }

}
