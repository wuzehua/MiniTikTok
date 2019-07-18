package com.bytedance.minitiktok.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytedance.minitiktok.model.FavoriteVideo
import com.bytedance.minitiktok.model.Video

@Dao
interface FavoriteVideoDAO {
    @Query("SELECT * FROM favorite_videos ORDER BY user_name")
    fun getFavoriteVideo(): List<FavoriteVideo>

    @Query("SELECT * FROM favorite_videos WHERE user_name = :user_name")
    fun getFavoriteVideo(user_name: String): List<FavoriteVideo>

    @Query("SELECT * FROM favorite_videos WHERE video_id = :video_id")
    fun getVideoFavoriteVideo(video_id: String): List<FavoriteVideo>

    @Query("SELECT video_id, student_id, user_name, image_url, video_url, update_date FROM favorite_videos NATURAL JOIN videos WHERE user_name = :user_name and favorite_name = :favorite_name")
    fun getFavoriteVideo(user_name: String, favorite_name: String): List<Video>

    @Query("SELECT * FROM favorite_videos WHERE user_name = :user_name and favorite_name = :favorite_name and video_id = :video_id")
    fun getFavoriteVideo(user_name: String, favorite_name: String, video_id: String): List<FavoriteVideo>

    @Query("DELETE FROM favorite_videos WHERE user_name = :user_name and favorite_name = :favorite_name and video_id = :video_id")
    fun deleteFavoriteVideo(user_name: String, favorite_name: String, video_id: String)

    @Query("DELETE FROM favorite_videos WHERE user_name = :user_name and favorite_name = :favorite_name")
    fun deleteFavoriteVideo(user_name: String, favorite_name: String)

    @Query("DELETE FROM favorite_videos WHERE user_name = :user_name")
    fun deleteFavoriteVideo(user_name: String)

    @Query("DELETE FROM favorite_videos WHERE video_id = :video_id")
    fun deleteVideoFavoriteVideo(video_id: String)

    @Query("DELETE FROM favorite_videos")
    fun deleteFavoriteVideo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteVideo(favorite_video: FavoriteVideo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteVideo(favorite_video: List<FavoriteVideo>)
}