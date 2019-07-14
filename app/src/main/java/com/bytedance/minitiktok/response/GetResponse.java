package com.bytedance.minitiktok.response;

import com.google.gson.annotations.SerializedName;
import com.bytedance.minitiktok.model.Video;
import java.util.List;

public class GetResponse {
    @SerializedName("feeds")
    private List<Video>videos;
    @SerializedName("success")
    private Boolean success;

    public List<Video> getVideos() { return videos; }

    public Boolean getSuccess() { return success; }
}
