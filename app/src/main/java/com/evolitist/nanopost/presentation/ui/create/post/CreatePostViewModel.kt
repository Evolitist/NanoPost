package com.evolitist.nanopost.presentation.ui.create.post

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import com.evolitist.nanopost.presentation.worker.CreatePostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val workManager: WorkManager,
) : ViewModel() {

    fun launchCreatePost(text: String, images: List<Uri>) {
        workManager.enqueue(CreatePostWorker.newRequest(text, images))
    }
}
