package com.bytedance.minitiktok.response;

import com.google.gson.annotations.SerializedName;


import java.util.Map;

public class PostResponse {
    @SerializedName("result") private Map<String,String> result;
    @SerializedName("url") private String url;
    @SerializedName("success") private Boolean success;

    public Map<String,String> getResult() {return result;}

    public void setResult(Map<String,String> result) {this.result = result;}

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public Boolean getSuccess() { return success; }

    public void setSuccess(Boolean success) { this.success = success; }
}
