package com.bytedance.minitiktok.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "comments",
    primaryKeys = ["user_name", "video_id", "comment"],
    foreignKeys = [ForeignKey(
        entity = Video::class,
        parentColumns = ["video_id"],
        childColumns = ["video_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = User::class, parentColumns = ["user_name"], childColumns = ["user_name"],
        onDelete = ForeignKey.CASCADE
    )]
)
public class Comment {
    @NonNull
    @SerializedName("user_name")
    @ColumnInfo(name = "user_name")
    var user_name: String? = null

    @NonNull
    @SerializedName("video_id")
    @ColumnInfo(name = "video_id")
    var video_id: String? = null

    @NonNull
    @SerializedName("comment")
    @ColumnInfo(name = "comment")
    var comment: String? = null
}
