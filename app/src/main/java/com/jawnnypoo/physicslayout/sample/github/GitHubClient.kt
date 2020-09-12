package com.jawnnypoo.physicslayout.sample.github

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object GitHubClient {

    private val github: GitHub by lazy {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl("https://api.github.com/")
                .build()
        retrofit.create(GitHub::class.java)
    }

    fun instance(): GitHub {
        return github
    }

    interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        suspend fun contributors(
                @Path("owner") owner: String,
                @Path("repo") repo: String
        ): List<Contributor>
    }
}
