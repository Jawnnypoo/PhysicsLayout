package com.jawnnypoo.physicslayout.sample.github;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jawn on 6/14/2015.
 */
public class Contributor {
    public String login;
    public int contributions;
    @SerializedName("avatar_url")
    public String avatarUrl;
}
