package com.bytedance.minitiktok.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bytedance.minitiktok.api.VideoDAO
import com.bytedance.minitiktok.model.Video

@Database(entities = [Video::class], version = 1, exportSchema = false)
abstract class VideoDataBase : RoomDatabase()
{

    abstract fun VideoDao(): VideoDAO

    companion object
    {
        @Volatile
        private var instance: VideoDataBase? = null

        fun getInstance(context: Context): VideoDataBase
        {
            if(instance == null)
            {
                synchronized(VideoDataBase::class)
                {
                    if(instance == null)
                    {
                        instance = Room.databaseBuilder(context.applicationContext, VideoDataBase::class.java, "videos.db")
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }

            return instance!!
        }
    }

    fun getAllVideos(): ArrayList<Video>
    {
        return VideoDao().getAllVideos() as ArrayList<Video>
    }

    fun getLikedVideos(): ArrayList<Video>
    {
        return VideoDao().getLikedVideos() as ArrayList<Video>
    }

    fun updateVideoStat(likeStat:Int, id: String)
    {
        VideoDao().updateVideoStat(likeStat,id)
    }

    fun deleteVideo(id: String)
    {
        VideoDao().deleteVideo(id)
    }

    fun deleteAllVideos()
    {
        VideoDao().deleteAllVideos()
    }

    fun insertAVideo(video: Video)
    {
        VideoDao().addVideo(video)
    }

    fun insertVideos(videos: List<Video>)
    {
        VideoDao().insertVideos(videos)
    }

}
