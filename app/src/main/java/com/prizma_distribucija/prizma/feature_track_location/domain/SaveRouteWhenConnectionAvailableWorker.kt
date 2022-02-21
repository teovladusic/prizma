package com.prizma_distribucija.prizma.feature_track_location.domain

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.SaveRouteToRemoteDatabaseUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltWorker
class SaveRouteWhenConnectionAvailableWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val saveRouteToRemoteDatabaseUseCase: SaveRouteToRemoteDatabaseUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val a = CoroutineScope(Dispatchers.IO).launch {
            val routeJson = inputData.getString("route")
            val gson = Gson()
            val route = gson.fromJson(routeJson, Route::class.java)

            saveRouteToRemoteDatabaseUseCase(route)
            Log.d("TAG", "doWork")
        }
        return Result.success()
    }
}