package com.example.testapplication.ui.main

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.testapplication.TestApp
import com.example.testapplication.TestApp.Companion.NOTIFICATION_CHANNEL_ID
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.databinding.ActivityMainBinding
import com.example.testapplication.ui.detail.DetailActivity
import com.example.testapplication.ui.list.ListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var application: TestApp
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = applicationContext as TestApp
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* to open detail activity, trigger by ListFragment->adapter callback */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.intentExtra.collect { data ->
                    processIntent(data["ID"])
                }
            }
        }

        /* to build the notification, trigger by MainFragment->createNotification()*/
        application.appCoroutine.launch {
            mainViewModel.notificationTrigger.collect { model ->
                launch { showNotification(model) }
            }
        }

    }


    /* to push notification by checking the permission first
    * args: model: NotificationModel [MainFragment->createNotification()] [MainViewModel->notificationTrigger]
    * */
    private suspend fun showNotification(model: NotificationModel) {
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
    private suspend fun createNotification(model: NotificationModel): NotificationCompat.Builder {
        val pendingIntent = buildPendingIntent(model.mealId)
        val image: Bitmap? = processImage(model.img_remote)
        val builder = NotificationCompat.Builder(this@MainActivity, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(model.title)
            .setContentText(model.body)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(image)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return builder
    }


    /* registering permission for Tiramisu Version and above
    * */
    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                Log.d("ASD", "notificationPermission: Permission Granted ")
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
    private fun processIntent(id: Int? = 0) {
        val intent = Intent(this, DetailActivity::class.java).putExtra("ID", id)
        startActivity(intent)
    }


    /* to get online image as bitmap synchronously inside coroutines with Glide library
    * args: url: String [notification->img_remote] [createNotification()]
    * */
    private suspend fun processImage(url: String): Bitmap? = withContext(Dispatchers.Default) {
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

        return@withContext bitmap
    }
}