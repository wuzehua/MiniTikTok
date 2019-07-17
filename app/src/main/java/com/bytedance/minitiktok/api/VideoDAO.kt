package com.bytedance.minitiktok.api

import androidx.room.*
import com.bytedance.minitiktok.model.Video

@Dao
interface VideoDAO
{
    @Query("SELECT * FROM  videos ORDER BY update_date DESC")
    fun getAllVideos(): List<Video>

    @Query("DELETE FROM videos WHERE _id = :videoID")
    fun deleteVideo(videoID: String)

    @Query("DELETE FROM videos")
    fun deleteAllVideos()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addVideo(video: Video)

    @Query("UPDATE videos SET liked = :likeStat WHERE _id = :id")
    fun updateVideoStat(likeStat:Int, id: String)

    @Query("SELECT * FROM videos WHERE liked = 1")
    fun getLikedVideos(): List<Video>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideos(videos: List<Video>)



}