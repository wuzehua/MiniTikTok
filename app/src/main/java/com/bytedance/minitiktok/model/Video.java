package com.bytedance.minitiktok.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;


@Entity(tableName = "videos")
public class Video {

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @ColumnInfo(name = "video_id")
    public String videoId;

    @SerializedName("student_id")
    @ColumnInfo(name = "student_id")
    public String studentId;


    @SerializedName("user_name")
    @ColumnInfo(name = "user_name")
    public String userName;


    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @SerializedName("video_url")
    @ColumnInfo(name = "video_url")
    public String videoUrl;


    @SerializedName("updatedAt")
    @ColumnInfo(name = "update_date")
    public String updateDate;
}
