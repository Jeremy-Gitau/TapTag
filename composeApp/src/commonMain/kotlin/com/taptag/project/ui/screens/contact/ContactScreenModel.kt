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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ContactScreenModel"

// Error constants
private object ErrorCodes {
    const val TOKEN_INVALID = "token_invalid"
    const val UNAUTHENTICATED = "unauthenticated"
    const val NO_AUTH_TOKEN = "No authentication token found"
}

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
    val showErrorDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val showSavedConfirmation: Boolean = false,
    val contactProfile: ContactDomain = ContactDomain()
)

class ContactScreenModel(
    private val contactsRepository: ContactsRepository,
    private val preferenceRepository: PreferenceRepository
) : StateScreenModel<ContactState>(initialState = ContactState()) {

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()

    fun validateRequiredField(value: String): Boolean = value.isNotEmpty()


    fun validateEmailFormat(email: String): Boolean = email.isEmpty() || emailPattern.matches(email)

    fun validateTags(tags: List<String>): Boolean = tags.isNotEmpty()

    private fun updateState(update: (ContactState) -> ContactState) {
        mutableState.update { update(it) }
    }

    private fun setLoading(loading: Boolean) {
        println("$TAG: Setting loading state to $loading")
        updateState { it.copy(isLoading = loading) }
    }

    private fun handleError(message: String, showDialog: Boolean = true) {
        println("$TAG: Error: $message")
        updateState {
            it.copy(
                error = message,
                showErrorDialog = showDialog
            )
        }
    }

    private suspend fun getToken(): String? {
        return preferenceRepository.readAccessToken().firstOrNull()
    }

    fun toggleShowErrorDialog(state: Boolean) {
        println("$TAG: Toggle error dialog: $state")
        updateState { it.copy(showErrorDialog = state) }
    }

    fun toggleIsEditMode(state: Boolean) {

        mutableState.update {
            it.copy(
                isEditMode = state
            )
        }
    }

    fun handleProfileUpdate(profile: ContactDomain) {
        mutableState.update {
            it.copy(
                contactProfile = profile
            )
        }
    }

    fun toggleShowSavedConfirmation(state: Boolean) {

        mutableState.update {
            it.copy(
                showSavedConfirmation = state
            )
        }
    }

    fun onTabSelected(tab: ContactStatus?) {
        println("$TAG: Tab selected: $tab")
        this@ContactScreenModel.screenModelScope.launch {
            updateState { it.copy(activeTab = tab) }
        }
    }

    fun updateSearchQuery(query: String) {
        println("$TAG: Search query updated: $query")
        this@ContactScreenModel.screenModelScope.launch {
            updateState { it.copy(searchQuery = query) }
        }
    }

    fun performSearch(query: String, contacts: List<ContactDomain>) {
        println("$TAG: Performing search with query: $query")
        val filteredContacts = contacts.filter { contact ->
            contact.name.contains(query, ignoreCase = true) ||
                    contact.email.contains(query, ignoreCase = true) ||
                    contact.tags.any { it.contains(query, ignoreCase = true) }
        }
        updateState { it.copy(filteredContacts = filteredContacts) }
    }

    fun toggleContactExpand(contactId: Int) {
        println("$TAG: Toggling contact expand for ID: $contactId")
        updateState {
            val current = it.expandedContactId
            it.copy(expandedContactId = if (current == contactId) null else contactId)
        }
    }

    fun toggleSearchBar(state: Boolean) {
        println("$TAG: Toggle search bar: $state")
        updateState { it.copy(searchState = state) }
    }

    // More robust error detection
    private fun isTokenError(errorMessage: String): Boolean {
        val tokenErrorPhrases = listOf(
            "token is not valid",
            "token invalid",
            "user not authenticated",
            "unauthorized",
            "unauthenticated",
            "expired token"
        )
        return tokenErrorPhrases.any { errorMessage.contains(it, ignoreCase = true) }
    }

    fun refreshAccessToken() {

        screenModelScope.launch {
            try {
                println("$TAG: Refreshing access token")

                val refreshToken = preferenceRepository.readRefreshToken().firstOrNull()

                println("$TAG: refresh token found: $refreshToken")
                try {
                    val result = contactsRepository.refreshToken(
                        RefreshTokenRequestDomain(refreshToken = refreshToken.toString())
                    )

                    when (result) {
                        is DataResult.Success -> {
                            println("$TAG: Token refresh successful")
                            preferenceRepository.saveAccessToken(result.data.accessToken)
                            preferenceRepository.saveRefreshToken(result.data.refreshToken)
                        }

                        is DataResult.Error -> {
                            println("$TAG: Token refresh failed: ${result.message}")
                            preferenceRepository.deleteAccessToken()
                            preferenceRepository.deleteRefreshToken()
                            handleError(result.message)
                        }
                    }
                } catch (e: Exception) {
                    println("$TAG: Exception during token refresh: ${e.message}")
                    if (e !is CancellationException) {
                        handleError("Failed to refresh token: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                println("$TAG: Mutex exception during refresh: ${e.message}")
            }
        }
    }

    private suspend fun <T> withAuthRetry(block: suspend (String) -> DataResult<T>): DataResult<T> {
        val token = getToken() ?: return DataResult.Error(ErrorCodes.NO_AUTH_TOKEN)

        val result = block(token)

        if (result is DataResult.Error && isTokenError(result.message)) {
            println("$TAG: Token error detected, attempting refresh")
            refreshAccessToken()

            // Try again with new token
            val newToken = getToken() ?: return DataResult.Error(ErrorCodes.NO_AUTH_TOKEN)
            return block(newToken)
        }

        return result
    }

    fun saveContact(contact: ContactsRequestDomain) {
        println("$TAG: Saving contact")
        screenModelScope.launch {
            try {
                setLoading(true)

                val result = withAuthRetry { token ->
                    contactsRepository.saveNewContact(token = token, data = contact)
                }

                println("$TAG: saved contact result: $result")

                when (result) {
                    is DataResult.Success -> {
                        println("$TAG: Contact saved successfully")
                        fetchAllContacts()
                    }

                    is DataResult.Error -> {
                        println("$TAG: Error saving contact: ${result.message}")
                        handleError("Error saving contact: ${result.message}")
                    }
                }
                setLoading(false)
            } catch (e: Exception) {
                println("$TAG: Exception saving contact: ${e.message}")
                if (e !is CancellationException) {
                    handleError("Error saving contact: ${e.message}")
                    setLoading(false)
                }
            }
        }
    }

    fun fetchAllContacts() {
        println("$TAG: Fetching all contacts")
        screenModelScope.launch {
            try {
                setLoading(true)

                val result = withAuthRetry { token ->
                    contactsRepository.getAllContacts(token)
                }

                delay(2000)

                println("fetched contact data: $result")
                when (result) {
                    is DataResult.Success -> {
                        println("$TAG: Successfully fetched ${result.data.size} contacts")
                        updateState { it.copy(contacts = result.data) }
                    }

                    is DataResult.Error -> {
                        println("$TAG: Error fetching contacts: ${result.message}")
                        handleError("Error fetching contacts: ${result.message}")
                    }
                }
                setLoading(false)
            } catch (e: Exception) {
                println("$TAG: Exception fetching contacts: ${e.message}")
                if (e !is CancellationException) {
                    handleError("Error fetching contacts: ${e.message}")
                    setLoading(false)
                }
            }
        }
    }

    fun updateContact(data: ContactsRequestDomain, id: String) {
        println("$TAG: Updating contact with ID: $id")
        screenModelScope.launch {
            try {
                setLoading(true)

                val result = withAuthRetry { token ->
                    contactsRepository.updateContacts(data = data, id = id)
                }

                when (result) {
                    is DataResult.Success -> {
                        println("$TAG: Contact updated successfully")
                        fetchAllContacts()
                    }

                    is DataResult.Error -> {
                        println("$TAG: Error updating contact: ${result.message}")
                        handleError("Error updating contact: ${result.message}")
                    }
                }
                setLoading(false)
            } catch (e: Exception) {
                println("$TAG: Exception updating contact: ${e.message}")
                if (e !is CancellationException) {
                    handleError("Error updating contact: ${e.message}")
                    setLoading(false)
                }
            }
        }
    }

    fun deleteContact(id: String) {
        println("$TAG: Deleting contact with ID: $id")
        screenModelScope.launch {
            try {
                setLoading(true)

                val result = withAuthRetry { token ->
                    contactsRepository.deleteContacts(id = id)
                }

                when (result) {
                    is DataResult.Success -> {
                        println("$TAG: Contact deleted successfully")
                        fetchAllContacts()
                    }

                    is DataResult.Error -> {
                        println("$TAG: Error deleting contact: ${result.message}")
                        handleError("Error deleting contact: ${result.message}")
                    }
                }
                setLoading(false)
            } catch (e: Exception) {
                println("$TAG: Exception deleting contact: ${e.message}")
                if (e !is CancellationException) {
                    handleError("Error deleting contact: ${e.message}")
                    setLoading(false)
                }
            }
        }
    }
}