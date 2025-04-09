package com.taptag.project.ui.screens.contact

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactStatus
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.repository.ContactsRepository
import com.taptag.project.domain.repository.PreferenceRepository
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
    val searchState: Boolean = false,
    val showErrorDialog: Boolean = false
)

class ContactScreenModel(
    private val contactsRepository: ContactsRepository,
    private val preferenceRepository: PreferenceRepository
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

    suspend fun getToken(): String? {
        return preferenceRepository.readAccessToken().toString()
    }

    suspend fun <T> withAuthToken(block: suspend (String) -> DataResult<T>): DataResult<T> {
        val token = getToken() ?: return DataResult.Error("No authentication token found")
        return block(token)
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

    fun toggleShowErrorDialog(state: Boolean) {
        mutableState.update {
            it.copy(
                showErrorDialog = state
            )
        }
    }

    fun saveContact(contact: ContactsRequestDomain) {

        screenModelScope.launch {

            try {

                isLoading(true)

                val result = withAuthToken { token ->
                    contactsRepository.getAllContacts(token)
                }

                when (result) {

                    is DataResult.Success -> {

                        fetchAllContacts()
                        isLoading(false)
                    }

                    is DataResult.Error -> {

                        if (result.message.contains("Token is not valid") || result.message.contains(
                                "User not authenticated"
                            )
                        ) {

                            preferenceRepository.deleteAccessToken()

                            val newToken = contactsRepository.refreshToken(
                                data = RefreshTokenRequestDomain(
                                    refreshToken = preferenceRepository.readRefreshToken()
                                        .toString()
                                )
                            )

                            when (newToken) {
                                is DataResult.Success -> {
                                    preferenceRepository.saveAccessToken(token = newToken.data.accessToken)
                                    preferenceRepository.deleteRefreshToken()
                                    preferenceRepository.saveRefreshToken(token = newToken.data.refreshToken)
                                }

                                is DataResult.Error -> {

                                    handleError(newToken.message)
                                }
                            }


                        } else
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

    fun fetchAllContacts() {
        screenModelScope.launch {
            try {

                isLoading(true)

                val result = withAuthToken { token ->
                    contactsRepository.getAllContacts(token)
                }

                when (result) {

                    is DataResult.Success -> {

                        mutableState.update {
                            it.copy(
                                contacts = result.data
                            )
                        }

                        isLoading(false)
                    }

                    is DataResult.Error -> {

                        if (result.message.contains("Token is not valid") || result.message.contains(
                                "User not authenticated"
                            )
                        ) {

                            preferenceRepository.deleteAccessToken()

                            val newToken = contactsRepository.refreshToken(
                                data = RefreshTokenRequestDomain(
                                    refreshToken = preferenceRepository.readRefreshToken()
                                        .toString()
                                )
                            )

                            when (newToken) {
                                is DataResult.Success -> {
                                    preferenceRepository.saveAccessToken(token = newToken.data.accessToken)
                                    preferenceRepository.deleteRefreshToken()
                                    preferenceRepository.saveRefreshToken(token = newToken.data.refreshToken)
                                }

                                is DataResult.Error -> {

                                    handleError(newToken.message)
                                }
                            }


                        } else
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

    fun updateContact(data: ContactsRequestDomain, id: String) {

        screenModelScope.launch {

            isLoading(true)

            contactsRepository.updateContacts(data = data, id = id)

            fetchAllContacts()

            isLoading(false)
        }
    }

    fun deleteContact(id: String) {

        screenModelScope.launch {
            isLoading(true)

            contactsRepository.deleteContacts(id = id)

            isLoading(false)
        }
    }
}