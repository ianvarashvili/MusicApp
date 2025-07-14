package com.example.musicapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val title: String = "",
    val artist: String = "",
    val image: String = "",
    val url: String = ""
) : Parcelable

