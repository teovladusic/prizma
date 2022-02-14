package com.prizma_distribucija.prizma.feature_track_location.domain.model

data class TaskResult(
    val isComplete: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String?,
)