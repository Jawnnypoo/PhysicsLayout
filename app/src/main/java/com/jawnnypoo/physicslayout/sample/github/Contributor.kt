package com.jawnnypoo.physicslayout.sample.github

import com.squareup.moshi.Json

data class Contributor (
    @Json(name = "avatar_url")
    var avatarUrl: String? = null
)
