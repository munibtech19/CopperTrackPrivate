package com.coppertrack.private.data.local.dao

import androidx.room.*
import com.coppertrack.private.data.local.entity.ContactEntity
import com.coppertrack.private.domain.model.ContactType
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE isArchived = 0 ORDER BY name ASC")
    fun getAllActive(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE isArchived = 0 AND type IN (:types) ORDER BY name ASC")
    fun getByTypes(types: List<ContactType>): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getById(id: Long): ContactEntity?

    @Query("""
        SELECT * FROM contacts 
        WHERE isArchived = 0 AND (
            name LIKE '%' || :query || '%' OR
            phone LIKE '%' || :query || '%' OR
            businessName LIKE '%' || :query || '%' OR
            city LIKE '%' || :query || '%'
        )
        ORDER BY name ASC
    """)
    fun search(query: String): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: ContactEntity): Long

    @Update
    suspend fun update(contact: ContactEntity)

    @Query("UPDATE contacts SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archive(id: Long, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM contacts WHERE isArchived = 0")
    suspend fun countActive(): Int
}
