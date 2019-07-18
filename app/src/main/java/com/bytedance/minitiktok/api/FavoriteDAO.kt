package com.bytedance.minitiktok.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytedance.minitiktok.model.Favorite

@Dao
interface FavoriteDAO {
    @Query("SELECT * FROM favorites ORDER BY user_name")
    fun getFavorite(): List<Favorite>

    @Query("SELECT favorite_name FROM favorites WHERE user_name = :user_name")
    fun getFavorite(user_name: String): List<String>

    @Query("SELECT * FROM favorites WHERE user_name = :user_name and favorite_name = :favorite_name")
    fun getFavorite(user_name: String, favorite_name: String): List<Favorite>

    @Query("DELETE FROM favorites WHERE user_name = :user_name and favorite_name = :favorite_name")
    fun deleteFavorite(user_name: String, favorite_name: String)

    @Query("DELETE FROM favorites WHERE user_name = :user_name")
    fun deleteFavorite(user_name: String)

    @Query("DELETE FROM favorites")
    fun deleteFavorite()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: Favorite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: List<Favorite>)
}