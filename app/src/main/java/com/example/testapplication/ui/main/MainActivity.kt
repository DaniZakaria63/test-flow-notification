package com.example.testapplication.ui.main

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.testapplication.R
import com.example.testapplication.TestApp.Companion.NOTIFICATION_CHANNEL_ID
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                /* to open detail activity, trigger by ListFragment->adapter callback */
                launch {
                    mainViewModel.intentExtra.collect { data ->
                        processIntent(data["ID"] ?: 0)
                    }
                }

                /* to show notification */
                launch {
                    mainViewModel.notificationTrigger.collect { model ->
                        showNotification(model)
                    }
                }
            }
        }
        notificationPermission()
    }


    /* to push notification by checking the permission first
    * args: model: NotificationModel [MainFragment->createNotification()] [MainViewModel->notificationTrigger]
    * */
    private fun showNotification(model: NotificationModel) {
        val builder: NotificationCompat.Builder = createNotification(model)
        with(NotificationManagerCompat.from(this@MainActivity)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermission()
            }
            notify(model.id, builder.build())
        }
    }


    /* to build notification as NotificationCompat.Builder
    * args: model: NotificationModel [showNotification()]
    * */
    private fun createNotification(model: NotificationModel): NotificationCompat.Builder {
        val pendingIntent = buildPendingIntent(model.mealId)
//        val image: Bitmap? = processImage(model.img_remote)

        return NotificationCompat.Builder(this@MainActivity, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(model.title)
            .setContentText(model.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }


    /* registering permission for Tiramisu Version and above
    * */
    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            }.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    /* to makes notification provide detail while clicked
    * args: id: Int? [notification->mealId] [createNotification()]
    * */
    private fun buildPendingIntent(id: Int? = 0): PendingIntent {
        val resultIntent = Intent(this, DetailActivity::class.java).putExtra("ID", id)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        return resultPendingIntent!!
    }


    /* to see detailed notification from list fragment
    * args: id: Int [notification->mealId] [ListFragment]
    * */
    private fun processIntent(id: Int = 0) {
        if (id > 10) { // meal id more than 10 are from the API
            startActivity(Intent(this, DetailActivity::class.java).putExtra("ID", id))
        } else {
            showDialogLocalNotification()
        }
    }


    private fun showDialogLocalNotification() {
        //TODO: Showing local dialog notification each time dummy type got clicked
    }


    /* to get online image as bitmap synchronously inside coroutines with Glide library
    * args: url: String [notification->img_remote] [createNotification()]
    *
    private fun processImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        Glide.with(this@MainActivity)
            .asBitmap()
            .load(url)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    bitmap = resource
                    return false
                }

            }).submit()
        return bitmap
    }
     */
}