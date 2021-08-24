package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(election: Election)

    @Query("SELECT * FROM election_table")
    fun getSavedElections() : LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun get(id: Long): Election?

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election_table")
    suspend fun clear()

}