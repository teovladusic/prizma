package com.prizma_distribucija.prizma.feature_login.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UserDto(
    val code: String = "",
    val lastName: String = "",
    val name: String = "",
    val userId: String = ""
) : Parcelable