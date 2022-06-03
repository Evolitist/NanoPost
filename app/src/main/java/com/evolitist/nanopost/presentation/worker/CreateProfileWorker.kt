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
import com.evolitist.nanopost.domain.usecase.UploadProfileUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreateProfileWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val prepareImageUseCase: PrepareImageUseCase,
    private val uploadProfileUseCase: UploadProfileUseCase,
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val NOTIFICATION_ID = 2
        private const val CHANNEL_PROGRESS = "progress"
        private const val KEY_USERNAME = "com.evolitist.nanopost.extras.USERNAME"
        private const val KEY_DISPLAY_NAME = "com.evolitist.nanopost.extras.DISPLAY_NAME"
        private const val KEY_BIO = "com.evolitist.nanopost.extras.BIO"
        private const val KEY_AVATAR = "com.evolitist.nanopost.extras.AVATAR"

        fun newRequest(
            username: String,
            displayName: String?,
            bio: String?,
            avatar: Uri?,
        ) = OneTimeWorkRequestBuilder<CreateProfileWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                Data.Builder()
                    .putString(KEY_USERNAME, username)
                    .putString(KEY_DISPLAY_NAME, displayName)
                    .putString(KEY_BIO, bio)
                    .putString(KEY_AVATAR, avatar?.toString())
                    .build()
            )
            .build()
    }

    override suspend fun doWork(): Result {
        val username = inputData.getString(KEY_USERNAME) ?: return Result.failure()
        val displayName = inputData.getString(KEY_DISPLAY_NAME)
        val bio = inputData.getString(KEY_BIO)
        val avatarUri = inputData.getString(KEY_AVATAR)

        runCatching {
            setForeground(getForegroundInfo())
        }

        val avatar = avatarUri?.let { prepareImageUseCase(Uri.parse(it)) }
        uploadProfileUseCase(username, displayName, bio, avatar)
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
            .setContentTitle("Creating profile...")
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
