package com.taptag.project.domain.repository

import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.domain.models.ContactsResponseDomain
import com.taptag.project.sources.remote.dtos.ContactsRequestData

interface ContactsRepository {

    suspend fun saveNewContact(data: ContactsRequestDomain, token: String):
            DataResult<ContactDomain>

    suspend fun getAllContacts(token: String):
            DataResult<List<ContactDomain>>

}