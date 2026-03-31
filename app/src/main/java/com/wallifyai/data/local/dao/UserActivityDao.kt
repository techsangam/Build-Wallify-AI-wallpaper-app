package com.wallifyai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wallifyai.data.local.entity.UserActivityEntity

@Dao
interface UserActivityDao {

    @Insert
    suspend fun insert(activity: UserActivityEntity)

    @Query(
        """
        SELECT 
            category,
            SUM(
                CASE
                    WHEN action_type = 'CLICK' THEN 1
                    WHEN action_type = 'DOWNLOAD' THEN 1
                    WHEN action_type = 'FAVORITE' THEN 2
                    ELSE 0
                END
            ) AS score
        FROM user_activity
        GROUP BY category
        ORDER BY score DESC, MAX(timestamp) DESC
        LIMIT :limit
        """,
    )
    suspend fun getTopCategories(limit: Int = 3): List<CategoryScore>
}

data class CategoryScore(
    val category: String,
    val score: Int,
)

