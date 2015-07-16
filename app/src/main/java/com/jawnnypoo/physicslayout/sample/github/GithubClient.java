package com.jawnnypoo.physicslayout.sample.github;


import com.jawnnypoo.physicslayout.sample.BuildConfig;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Jawn on 6/30/2015.
 */
public class GithubClient {

    public static final String API_URL = "https://api.github.com";

    public interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        void contributors(
                @Path("owner") String owner,
                @Path("repo") String repo,
                Callback<List<Contributor>> callback);
    }

    private static GitHub mGithub;
    public static GitHub instance() {
        if (mGithub == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                    .build();
            mGithub = restAdapter.create(GitHub.class);
        }
        return mGithub;
    }
}
