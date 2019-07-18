package com.bytedance.minitiktok.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "favorite_videos",
    primaryKeys = ["user_name", "favorite_name", "video_id"],
    indices = [Index("video_id"), Index("user_name")],
    foreignKeys = [ForeignKey(
        entity = Video::class,
        parentColumns = ["video_id"],
        childColumns = ["video_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Favorite::class,
        parentColumns = ["user_name", "favorite_name"],
        childColumns = ["user_name", "favorite_name"],
        onDelete = ForeignKey.CASCADE
    )]
)
public class FavoriteVideo {
    @NonNull
    @SerializedName("user_name")
    @ColumnInfo(name = "user_name")
    var user_name: String? = null

    @NonNull
    @SerializedName("favorite_name")
    @ColumnInfo(name = "favorite_name")
    var favorite_name: String? = null

    @NonNull
    @SerializedName("video_id")
    @ColumnInfo(name = "video_id")
    var video_id: String? = null
}
