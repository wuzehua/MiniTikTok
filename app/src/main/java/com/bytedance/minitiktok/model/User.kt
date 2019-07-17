package com.bytedance.minitiktok.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    @SerializedName("user_name")
    @ColumnInfo(name = "user_name")
    var userName: String? = null

    @NonNull
    @SerializedName("passwd")
    @ColumnInfo(name = "passwd")
    var passwd: String? = null
}
