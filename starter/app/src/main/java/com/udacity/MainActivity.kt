package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadRequestNumber: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MainActivity", "Inside onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            createChannel(getString(R.string.download_notification_channel_id),
                getString(R.string.notification_channel))
            if (radio_group.checkedRadioButtonId == -1){
                Toast.makeText(
                applicationContext,
                "Radio button needs to be selected",Toast.LENGTH_SHORT).show()

            }
            else {
                when (radio_group.checkedRadioButtonId) {
                    radio_loadapp.id -> download("https://github.com/bumptech/glide")
                    radio_bumptech.id -> download(
                        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter")
                    radio_retrofit.id -> download("https://github.com/square/retrofit")
                }
            }
        }
    }


    @SuppressLint("Range")
    private fun download(link: String) {
        Log.i("MainActivity", "Inside download:${link}")
        val request =
            DownloadManager.Request(Uri.parse(link))
                .setVisibleInDownloadsUi(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadRequestNumber =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("MainActivity", "After Download Queuing request#: ${downloadRequestNumber}")
        //@TODO - to be deleted: Temporary code to check whether download manager is working or not
        val query = DownloadManager.Query()
        val downloadManager1 = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        Log.i("MainActivity", "Getting hold of hte cursor")
        val cursor: Cursor = downloadManager1.query(query)
        Log.i("MainActivity", "cursor: ${cursor.moveToFirst()}")
        if (cursor.moveToFirst()) {
            Log.i("MainActivity", "if cursor has moved to first: ${cursor.moveToFirst()}")
            val success = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            Log.i("MainActivity", "success:${success}")
            val isSuccess = success == DownloadManager.STATUS_SUCCESSFUL
            Log.i("MainActivity", "isSuccess: ${isSuccess}")
            val downloadTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
            Log.i("MainActivity", "downloadTitle: ${downloadTitle}")

        }
    }

//    companion object {
//        private const val URL =
//            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
//        private const val CHANNEL_ID = "channelId"
//    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("MainActivity", "Inside Broadcast Receiver - onReceive")
            Log.i("MainActivity", "Context:${context.let { it.toString() }}")
            Log.i("MainActivity", "Context:${intent.let { it.toString() }}")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.i("MainActivity", "DownloadRequestNumber:${id}")
            if (downloadRequestNumber == id) {
                Log.i("MainActivity", "Inside processing of specific download request")
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    Log.i("MainActivity", "Inside if check of cursor.moveToFirst")
                    val success = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = success == DownloadManager.STATUS_SUCCESSFUL
                    val downloadTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    sendNotificaiton(isSuccess, downloadTitle)
                }
                custom_button.downloadCompleted()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotificaiton(isSuccess: Boolean, downlaodTitle: String) {
        Log.i("MainActivity", "inside sendNotification")
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(this,
            downlaodTitle, isSuccess)
    }

}
