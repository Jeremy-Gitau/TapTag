package com.taptag.project.ui.screens.contact

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactStatus
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val contacts: List<ContactDomain> = emptyList(),
    val filteredContacts: List<ContactDomain> = emptyList(),
    val activeTab: ContactStatus? = null,
    val searchQuery: String = "",
    val expandedContactId: Int? = null,
    val networkCount: Int = 24,
    val newContactsThisMonth: Int = 5,
    val pendingFollowUps: Int = 5,
    val meetingsThisWeek: Int = 3,
    val searchState: Boolean = false
)

class ContactScreenModel(
    private val contactsRepository: ContactsRepository
) : StateScreenModel<ContactState>(initialState = ContactState()) {

    private fun isLoading(state: Boolean) {
        mutableState.update {
            it.copy(
                isLoading = state
            )
        }
    }

    private fun handleError(message: String) {
        mutableState.update {
            it.copy(
                error = message
            )
        }
    }

    fun onTabSelected(tab: ContactStatus?) {

        screenModelScope.launch {
            mutableState.update { it.copy(activeTab = tab) }
        }

    }

    fun updateSearchQuery(query: String) {

        screenModelScope.launch {
            mutableState.update { it.copy(searchQuery = query) }
        }
    }

    fun performSearch(query: String, contacts: List<ContactDomain>) {

        val filteredContacts = contacts.filter { contact ->
            contact.name.contains(query, ignoreCase = true) ||
                    contact.email.contains(query, ignoreCase = true) ||
                    contact.tags.any { it.contains(query, ignoreCase = true) }
        }
        mutableState.update { it.copy(filteredContacts = filteredContacts) }

    }

    fun toggleContactExpand(contactId: Int) {
        mutableState.update {
            val current = it.expandedContactId
            it.copy(expandedContactId = if (current == contactId) null else contactId)
        }
    }

    fun toggleSearchBar(state: Boolean) {
        mutableState.update {
            it.copy(
                searchState = state
            )
        }
    }

    suspend fun saveContact(contact: ContactsRequestDomain, token: String) {

        screenModelScope.launch {

            try {

                isLoading(true)

                when (val result = contactsRepository.saveNewContact(data = contact, token = token)) {

                    is DataResult.Success -> {

                        getAllContacts(token = token)
                        isLoading(false)
                    }

                    is DataResult.Error -> {

                        handleError("error saving contact: ${result.message}")
                    }
                }

                isLoading(false)
            } catch (e: Exception) {

                isLoading(false)
                handleError("error saving Contact: ${e.message}")
            }
        }
    }

    suspend fun getAllContacts(token: String) {
        screenModelScope.launch {
            try {

                when (val result = contactsRepository.getAllContacts(token = token)) {

                    is DataResult.Success -> {

                        mutableState.update {
                            it.copy(
                                contacts = result.data
                            )
                        }

                        isLoading(false)
                    }

                    is DataResult.Error -> {

                        handleError("error fetching contacts: ${result.message}")
                    }
                }
                isLoading(false)

            } catch (e: Exception) {

                isLoading(false)
                handleError("error fetching contacts: ${e.message}")
            }
        }
    }
}