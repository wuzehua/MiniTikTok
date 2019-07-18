package com.bytedance.minitiktok.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytedance.minitiktok.model.Comment

@Dao
interface CommentDAO {
    @Query("SELECT * FROM comments ORDER BY user_name")
    fun getComment(): List<Comment>

    @Query("SELECT * FROM comments WHERE video_id = :video_id")
    fun getComment(video_id: String): List<Comment>

    @Query("SELECT * FROM comments WHERE user_name = :user_name")
    fun getUserComment(user_name: String): List<Comment>

    @Query("SELECT comment FROM comments WHERE user_name = :user_name and video_id = :video_id")
    fun getComment(user_name: String, video_id: String): List<String>

    @Query("SELECT * FROM comments WHERE user_name = :user_name and video_id = :video_id and comment = :comment")
    fun getComment(user_name: String, video_id: String, comment: String): List<Comment>

    @Query("DELETE FROM comments WHERE user_name = :user_name and video_id = :video_id and comment = :comment")
    fun deleteComment(user_name: String, video_id: String, comment: String)

    @Query("DELETE FROM comments WHERE user_name = :user_name and video_id = :video_id")
    fun deleteComment(user_name: String, video_id: String)

    @Query("DELETE FROM comments WHERE user_name = :user_name")
    fun deleteComment(user_name: String)

    @Query("DELETE FROM comments WHERE video_id = :video_id")
    fun deleteVideoComment(video_id: String)

    @Query("DELETE FROM comments")
    fun deleteComment()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: List<Comment>)
}