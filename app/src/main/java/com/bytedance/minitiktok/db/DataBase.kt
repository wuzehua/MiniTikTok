package com.bytedance.minitiktok.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bytedance.minitiktok.api.*
import com.bytedance.minitiktok.model.*

@Database(
    entities = [Video::class, User::class, Like::class, Comment::class, Favorite::class, FavoriteVideo::class],
    version = 1,
    exportSchema = false
)
abstract class DataBase : RoomDatabase() {

    abstract fun VideoDao(): VideoDAO

    abstract fun UserDao(): UserDAO

    abstract fun LikeDao(): LikeDAO

    abstract fun CommentDao(): CommentDAO

    abstract fun FavoriteDao(): FavoriteDAO

    abstract fun FavoriteVideoDao(): FavoriteVideoDAO

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
    fun getVideo(): List<Video> {
        return VideoDao().getVideo()
    }

    fun getVideo(videoID: String): Video {
        return VideoDao().getVideo(videoID)
    }

    fun getVideo(videoID: List<String>): List<Video> {
        return VideoDao().getVideo(videoID)
    }

    fun deleteVideo(id: String) {
        VideoDao().deleteVideo(id)
    }

    fun deleteVideo() {
        VideoDao().deleteVideo()
    }

    fun insertVideo(video: Video) {
        VideoDao().insertVideo(video)
    }

    fun insertVideo(videos: List<Video>) {
        VideoDao().insertVideo(videos)
    }

    // User
    fun getUser(): List<User> {
        return UserDao().getUser()
    }

    fun getUser(user_name: String): User {
        return UserDao().getUser(user_name)
    }

    fun getPasswd(user_name: String): String {
        return UserDao().getPasswd(user_name)
    }

    fun getUser(user_name: String, passwd: String): Boolean {
        return UserDao().getUser(user_name, passwd).isNotEmpty()
    }

    fun updateUserPasswd(user_name: String, passwd: String) {
        if (user_name != "default") {
            UserDao().updateUserPasswd(user_name, passwd)
        }
    }

    fun deleteUser(user_name: String) {
        UserDao().deleteUser(user_name)
    }

    fun deleteUser() {
        UserDao().deleteUser()
    }

    fun insertUser(user: User) {
        UserDao().insertUser(user)
    }

    fun insertUser(user: List<User>) {
        UserDao().insertUser(user)
    }

    // Like
    fun getLike(): List<Like> {
        return LikeDao().getLike()
    }

    fun getLike(user_name: String): List<String> {
        return LikeDao().getLike(user_name)
    }

    fun getUserLikeVideo(user_name: String): List<Video> {
        return LikeDao().getUserLikeVideo(user_name)
    }

    fun getLike(user_name: String, video_id: String): Boolean {
        return LikeDao().getLike(user_name, video_id).isNotEmpty()
    }

    fun getVideoLike(video_id: String): List<String> {
        return LikeDao().getVideoLike(video_id)
    }

    fun deleteLike(user_name: String, video_id: String) {
        LikeDao().deleteLike(user_name, video_id)
    }

    fun deleteLike(user_name: String) {
        LikeDao().deleteLike(user_name)
    }

    fun deleteVideoLike(video_id: String) {
        LikeDao().deleteVideoLike(video_id)
    }

    fun deleteLike() {
        LikeDao().deleteLike()
    }

    fun insertLike(like: Like) {
        LikeDao().insertLike(like)
    }

    fun insertLike(like: List<Like>) {
        LikeDao().insertLike(like)
    }

    // Favorite
    fun getFavorite(): List<Favorite> {
        return FavoriteDao().getFavorite()
    }

    fun getFavorite(user_name: String): List<String> {
        return FavoriteDao().getFavorite(user_name)
    }

    fun getFavorite(user_name: String, favorite_name: String): Boolean {
        return FavoriteDao().getFavorite(user_name, favorite_name).isNotEmpty()
    }

    fun deleteFavorite(user_name: String, video_id: String) {
        FavoriteDao().deleteFavorite(user_name, video_id)
    }

    fun deleteFavorite(user_name: String) {
        FavoriteDao().deleteFavorite(user_name)
    }

    fun deleteFavorite() {
        FavoriteDao().deleteFavorite()
    }

    fun insertFavorite(favorite: Favorite) {
        FavoriteDao().insertFavorite(favorite)
    }

    fun insertFavorite(favorite: List<Favorite>) {
        FavoriteDao().insertFavorite(favorite)
    }

    // Comment
    fun getComment(): List<Comment> {
        return CommentDao().getComment()
    }

    fun getComment(video_id: String): List<Comment> {
        return CommentDao().getComment(video_id)
    }

    fun getUserComment(user_name: String): List<Comment> {
        return CommentDao().getUserComment(user_name)
    }

    fun getComment(user_name: String, video_id: String): List<String> {
        return CommentDao().getComment(user_name, video_id)
    }

    fun getComment(user_name: String, video_id: String, comment: String): Boolean {
        return CommentDao().getComment(user_name, video_id, comment).isNotEmpty()
    }

    fun deleteComment(user_name: String, video_id: String, comment: String) {
        CommentDao().deleteComment(user_name, video_id, comment)
    }

    fun deleteComment(user_name: String, video_id: String) {
        CommentDao().deleteComment(user_name, video_id)
    }

    fun deleteComment(user_name: String) {
        CommentDao().deleteComment(user_name)
    }

    fun deleteVideoComment(video_id: String) {
        CommentDao().deleteVideoComment(video_id)
    }

    fun deleteComment() {
        CommentDao().deleteComment()
    }

    fun insertComment(comment: Comment) {
        CommentDao().insertComment(comment)
    }

    fun insertComment(comment: List<Comment>) {
        CommentDao().insertComment(comment)
    }

    // FavoriteVideo
    fun getFavoriteVideo(): List<FavoriteVideo> {
        return FavoriteVideoDao().getFavoriteVideo()
    }

    fun getFavoriteVideo(user_name: String): List<FavoriteVideo> {
        return FavoriteVideoDao().getFavoriteVideo(user_name)
    }

    fun getVideoFavoriteVideo(video_id: String): List<FavoriteVideo> {
        return FavoriteVideoDao().getVideoFavoriteVideo(video_id)
    }

    fun getFavoriteVideo(user_name: String, favorite_name: String): List<Video> {
        return FavoriteVideoDao().getFavoriteVideo(user_name, favorite_name)
    }

    fun getFavoriteVideo(user_name: String, favorite_name: String, video_id: String): Boolean {
        return FavoriteVideoDao().getFavoriteVideo(user_name, favorite_name, video_id).isNotEmpty()
    }

    fun deleteFavoriteVideo(user_name: String, favorite_name: String, video_id: String) {
        FavoriteVideoDao().deleteFavoriteVideo(user_name, favorite_name, video_id)
    }

    fun deleteFavoriteVideo(user_name: String, favorite_name: String) {
        FavoriteVideoDao().deleteFavoriteVideo(user_name, favorite_name)
    }

    fun deleteFavoriteVideo(user_name: String) {
        FavoriteVideoDao().deleteFavoriteVideo(user_name)
    }

    fun deleteVideoFavoriteVideo(video_id: String) {
        FavoriteVideoDao().deleteVideoFavoriteVideo(video_id)
    }

    fun deleteFavoriteVideo() {
        FavoriteVideoDao().deleteFavoriteVideo()
    }

    fun insertFavoriteVideo(favoriteVideo: FavoriteVideo) {
        FavoriteVideoDao().insertFavoriteVideo(favoriteVideo)
    }

    fun insertFavoriteVideo(favoriteVideo: List<FavoriteVideo>) {
        FavoriteVideoDao().insertFavoriteVideo(favoriteVideo)
    }

}
