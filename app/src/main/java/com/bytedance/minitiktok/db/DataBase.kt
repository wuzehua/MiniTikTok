package com.bytedance.minitiktok.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bytedance.minitiktok.api.UserDAO
import com.bytedance.minitiktok.api.VideoDAO
import com.bytedance.minitiktok.model.User
import com.bytedance.minitiktok.model.Video

@Database(entities = [Video::class, User::class], version = 1, exportSchema = false)
abstract class DataBase : RoomDatabase() {

    abstract fun VideoDao(): VideoDAO

    abstract fun UserDao(): UserDAO

    companion object {
        @Volatile
        private var instance: DataBase? = null

        fun getInstance(context: Context): DataBase {
            if (instance == null) {
                synchronized(DataBase::class)
                {
                    if (instance == null) {
                        instance =
                            Room.databaseBuilder(context.applicationContext, DataBase::class.java, "MiniTikTok.db")
                                .allowMainThreadQueries()
                                .build()
                    }
                }
            }

            return instance!!
        }
    }

    // Video
    fun getAllVideos(): List<Video> {
        return VideoDao().getAllVideos()
    }

    fun deleteVideo(id: String) {
        VideoDao().deleteVideo(id)
    }

    fun deleteAllVideos() {
        VideoDao().deleteAllVideos()
    }

    fun insertVideo(video: Video) {
        VideoDao().insertVideo(video)
    }

    fun insertVideos(videos: List<Video>) {
        VideoDao().insertVideos(videos)
    }

    // User
    fun getAllUsers(): List<User> {
        return UserDao().getAllUsers()
    }

    fun getUser(user_name: String): User {
        return UserDao().getUser(user_name)
    }

    fun getPasswd(user_name: String): String {
        return UserDao().getPasswd(user_name)
    }

    fun updateUserPasswd(user_name: String, passwd: String) {
        UserDao().updateUserPasswd(user_name, passwd)
    }

    fun deleteUser(user_name: String) {
        UserDao().deleteUser(user_name)
    }

    fun deleteAllUsers() {
        UserDao().deleteAllUsers()
    }

    fun insertUser(user: User) {
        UserDao().insertUser(user)
    }

    fun insertUsers(users: List<User>) {
        UserDao().insertVideos(users)
    }

}
