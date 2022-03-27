package com.test.test_karim.data.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "guests", primaryKeys = ["id"])
data class Guests(
        @ColumnInfo(name = "id")
        var id: Int,
        @ColumnInfo(name = "email")
        var email: String? = "",
        @ColumnInfo(name = "first_name")
        var first_name: String? = "",
        @ColumnInfo(name = "last_name")
        var last_name: String? = "",
        @ColumnInfo(name = "avatar")
        var avatar: String? = ""
)

@Dao
interface GueestsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSus(data: Guests)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLists(lists: List<Guests>)

    @Query("SELECT * FROM guests WHERE id = :id ORDER BY first_name DESC")
    fun allByIdFlow(id : Int): Flow<List<Guests>>

    @Query("DELETE FROM guests")
    fun deleteAll()

    @Query("DELETE FROM guests WHERE id = :id")
    suspend fun deleteByIdSus(id : Int)
}
