package com.riqsphere.myapplication.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.riqsphere.myapplication.model.recommendations.Recommendation

@Dao
interface RecommendationDao {
    @Query("select * from recommendation order by count desc")
    fun getRecommendations(): LiveData<List<Recommendation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recommendation: Recommendation)

    @Query("update recommendation set count = (count + :count) where id = :id")
    suspend fun addCount(id: Int, count: Int)

    @Query("select count from recommendation where id = :id")
    suspend fun getCountFor(id: Int): Int

    @Query("delete from recommendation where id = :id")
    suspend fun delete(id: Int)

    @Query("delete from recommendation")
    suspend fun deleteAll()
}