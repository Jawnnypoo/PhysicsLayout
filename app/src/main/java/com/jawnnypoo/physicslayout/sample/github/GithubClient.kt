package com.jawnnypoo.physicslayout.sample.github


import com.jawnnypoo.physicslayout.sample.BuildConfig

import retrofit.Callback
import retrofit.RestAdapter
import retrofit.http.GET
import retrofit.http.Path

object GithubClient {

    private const val API_URL = "https://api.github.com"

    private val github: GitHub by lazy {
        val restAdapter = RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setLogLevel(if (BuildConfig.DEBUG) RestAdapter.LogLevel.FULL else RestAdapter.LogLevel.NONE)
                .build()
        restAdapter.create(GitHub::class.java)
    }

    fun instance(): GitHub {
        return github
    }

    interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        fun contributors(
                @Path("owner") owner: String,
                @Path("repo") repo: String,
                callback: Callback<List<Contributor>>)
    }
}
