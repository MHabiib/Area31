package com.skripsi.area31.utils

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skripsi.area31.utils.Constants.Companion.MY_FIREBASE_MESSAGING
import com.skripsi.area31.utils.Constants.Companion.STUDENT_NAME

class MyFirebaseMessagingService : FirebaseMessagingService() {
  override fun onMessageReceived(remoteMessage: RemoteMessage?) {
    if (remoteMessage?.data != null) {
      val intent = Intent(MY_FIREBASE_MESSAGING)
      intent.putExtra(STUDENT_NAME, remoteMessage.data[STUDENT_NAME])

      LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
  }
}
