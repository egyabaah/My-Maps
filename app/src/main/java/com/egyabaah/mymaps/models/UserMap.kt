package com.egyabaah.mymaps.models

import java.io.Serializable

data class UserMap(val title: String, val places: List<Place>, val id: Int) : Serializable