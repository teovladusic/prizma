package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.SaveUriToRemoteDatabaseUseCase
import javax.inject.Inject

class SendUriWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @Inject
    lateinit var saveUriToRemoteDatabaseUseCase: SaveUriToRemoteDatabaseUseCase

    override suspend fun doWork(): Result {
        val path = inputData.getString("path").toString()
        val uri = inputData.getString("uri").toString().toUri()
        saveUriToRemoteDatabaseUseCase(path, uri)
        return Result.success()
    }
}