package com.prizma_distribucija.prizma.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val code: String = "",
    val lastName: String = "",
    val name: String = "",
    val userId: String = ""
) : Parcelable