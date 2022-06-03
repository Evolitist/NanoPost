package com.evolitist.nanopost.presentation.worker

import android.app.Notification
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.usecase.PrepareImageUseCase
import com.evolitist.nanopost.domain.usecase.UploadPostUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreatePostWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val prepareImageUseCase: PrepareImageUseCase,
    private val uploadPostUseCase: UploadPostUseCase,
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_PROGRESS = "progress"
        private const val KEY_TEXT = "com.evolitist.nanopost.extras.TEXT"
        private const val KEY_IMAGES = "com.evolitist.nanopost.extras.IMAGES"

        fun newRequest(text: String?, images: List<Uri>) = OneTimeWorkRequestBuilder<CreatePostWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                Data.Builder()
                    .putString(KEY_TEXT, text)
                    .putStringArray(KEY_IMAGES, images.map { it.toString() }.toTypedArray())
                    .build()
            )
            .build()
    }

    override suspend fun doWork(): Result {
        val text = inputData.getString(KEY_TEXT)
        val imageUris = inputData.getStringArray(KEY_IMAGES)?.map { Uri.parse(it) }.orEmpty()

        if (text.isNullOrBlank() && imageUris.isEmpty()) {
            return Result.failure()
        }

        runCatching {
            setForeground(getForegroundInfo())
        }

        val images = imageUris.map { prepareImageUseCase(it) }
        uploadPostUseCase(text, images)
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        createChannel()

        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        return NotificationCompat.Builder(applicationContext, CHANNEL_PROGRESS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Creating post...")
            .setOngoing(true)
            .setProgress(0, 0, true)
            .addAction(R.drawable.ic_close, "Cancel", intent)
            .build()
    }

    private fun createChannel() {
        val channel = NotificationChannelCompat.Builder(CHANNEL_PROGRESS, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName("Data upload")
            .build()
        NotificationManagerCompat.from(applicationContext)
            .createNotificationChannel(channel)
    }
}
