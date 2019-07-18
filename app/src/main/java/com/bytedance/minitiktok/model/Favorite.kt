package com.bytedance.minitiktok.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "favorites",
    primaryKeys = ["user_name", "favorite_name"],
    indices = [Index("user_name")],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_name"],
        childColumns = ["user_name"],
        onDelete = ForeignKey.CASCADE
    )]
)
public class Favorite {
    @NonNull
    @SerializedName("user_name")
    @ColumnInfo(name = "user_name")
    var user_name: String? = null

    @NonNull
    @SerializedName("favorite_name")
    @ColumnInfo(name = "favorite_name")
    var favorite_name: String? = null
}
