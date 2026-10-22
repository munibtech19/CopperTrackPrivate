package com.coppertrack.private.data.repository

import com.coppertrack.private.data.local.dao.ContactDao
import com.coppertrack.private.data.local.entity.ContactEntity
import com.coppertrack.private.domain.model.ContactType
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val dao: ContactDao) {
    fun getAllActive(): Flow<List<ContactEntity>> = dao.getAllActive()
    fun getByTypes(types: List<ContactType>): Flow<List<ContactEntity>> = dao.getByTypes(types)
    fun search(query: String): Flow<List<ContactEntity>> = dao.search(query)
    suspend fun getById(id: Long): ContactEntity? = dao.getById(id)
    suspend fun save(contact: ContactEntity): Long =
        if (contact.id == 0L) dao.insert(contact)
        else {
            dao.update(contact.copy(updatedAt = System.currentTimeMillis()))
            contact.id
        }
    suspend fun archive(id: Long) = dao.archive(id)
}
