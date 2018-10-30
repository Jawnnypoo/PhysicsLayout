package com.jawnnypoo.physicslayout.sample.github;

import com.google.gson.annotations.SerializedName;

public class Contributor {
    public String login;
    public int contributions;
    @SerializedName("avatar_url")
    public String avatarUrl;
}
