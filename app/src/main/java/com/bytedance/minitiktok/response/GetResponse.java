package com.bytedance.minitiktok.response;

import com.google.gson.annotations.SerializedName;
import com.bytedance.minitiktok.model.Video;
import java.util.List;

public class GetResponse {
    @SerializedName("feeds") List<Video>videos;
    @SerializedName("success") Boolean success;

    public List<Video> getVideos() { return videos; }

    public Boolean getSuccess() { return success; }
}
