package com.bytedance.minitiktok.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytedance.minitiktok.model.Like

@Dao
interface LikeDAO {
    @Query("SELECT * FROM likes ORDER BY user_name")
    fun getAllLikes(): List<Like>

    @Query("SELECT * FROM likes WHERE user_name = :user_name and video_id = :video_id")
    fun getisLiked(user_name: String, video_id: String): List<Like>

    @Query("SELECT video_id FROM likes WHERE user_name = :user_name")
    fun getLikedVideo(user_name: String): String

    @Query("SELECT user_name FROM likes WHERE video_id = :video_id")
    fun getLikedUser(video_id: String): String

    @Query("DELETE FROM likes WHERE user_name = :user_name and video_id = :video_id")
    fun deleteLike(user_name: String, video_id: String)

    @Query("DELETE FROM users")
    fun deleteAllLikes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLike(like: Like)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLikes(likes: List<Like>)
}