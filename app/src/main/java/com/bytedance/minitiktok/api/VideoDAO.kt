package com.bytedance.minitiktok.api

import androidx.room.*
import com.bytedance.minitiktok.model.Video

@Dao
interface VideoDAO {
    @Query("SELECT * FROM  videos ORDER BY update_date DESC")
    fun getVideo(): List<Video>

    @Query("SELECT * FROM  videos WHERE video_id = :videoID")
    fun getVideo(videoID: String): Video

    @Query("SELECT * FROM  videos WHERE video_id IN (:videoID)")
    fun getVideo(videoID: List<String>): List<Video>

    @Query("DELETE FROM videos WHERE video_id = :videoID")
    fun deleteVideo(videoID: String)

    @Query("DELETE FROM videos")
    fun deleteVideo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideo(video: Video)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideo(video: List<Video>)
}