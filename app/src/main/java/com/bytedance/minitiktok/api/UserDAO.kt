package com.bytedance.minitiktok.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytedance.minitiktok.model.User

@Dao
interface UserDAO {
    @Query("SELECT * FROM  users ORDER BY user_name")
    fun getUser(): List<User>

    @Query("SELECT * FROM users WHERE user_name = :user_name")
    fun getUser(user_name: String): User

    @Query("SELECT passwd FROM users WHERE user_name = :user_name")
    fun getPasswd(user_name: String): String

    @Query("SELECT * FROM users WHERE user_name = :user_name and passwd = :passwd")
    fun getUser(user_name: String, passwd: String): List<User>

    @Query("DELETE FROM users WHERE user_name = :user_name")
    fun deleteUser(user_name: String)

    @Query("DELETE FROM users")
    fun deleteUser()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(users: List<User>)

    @Query("UPDATE users SET passwd = :passwd WHERE user_name = :user_name")
    fun updateUserPasswd(user_name: String, passwd: String)
}