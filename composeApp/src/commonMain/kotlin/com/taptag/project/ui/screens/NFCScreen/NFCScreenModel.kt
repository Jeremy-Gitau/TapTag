package com.taptag.project.ui.screens.NFCScreen

import cafe.adriel.voyager.core.model.StateScreenModel
import com.taptag.project.domain.models.ContactDomain
import kotlinx.coroutines.flow.update
import org.koin.core.logger.MESSAGE

data class NFCState(
    val isScanning: Boolean = false,
    val nfcResult: String? = null,
    val error: String? = null,
    val showErrorDialog: Boolean = false,
    val showResultDialog: Boolean = false,
    val showWriteDialog: Boolean = false,
    val isWriteMode: Boolean = false,
    val writeStatus: String = "",
    val isSuccess: Boolean = false,
    val message: String = "",
    val writeResult: Boolean? = null,
    val currentContact: ContactDomain = ContactDomain(),
    val lastScannedContact: ContactDomain? = null
)

class NFCScreenModel : StateScreenModel<NFCState>(initialState = NFCState()) {


    fun toggleErrorDialog(state: Boolean) {
        mutableState.update {
            it.copy(
                showErrorDialog = state
            )
        }
    }

    fun toggleShowWriteDialog(state: Boolean) {
        mutableState.update {
            it.copy(
                showWriteDialog = state
            )
        }
    }

    fun toggleWriteResult(state: Boolean? = null) {
        mutableState.update {
            it.copy(
                writeResult = state
            )
        }
    }

    fun toggleIsWriteMode(state: Boolean) {
        mutableState.update {
            it.copy(
                isWriteMode = state
            )
        }
    }

    fun toggleWriteStatus(status: String) {
        mutableState.update {
            it.copy(
                writeStatus = status
            )
        }
    }

    fun isScanning(state: Boolean) {
        mutableState.update { it.copy(isScanning = state) }
    }

    fun toggleResultDialog(state: Boolean) {
        mutableState.update {
            it.copy(
                showResultDialog = state
            )
        }
    }

    fun observeCurrentContact(contact: ContactDomain){
        mutableState.update {
            it.copy(
                currentContact = contact
            )
        }
    }

    fun observeLastScannedContact(contact: ContactDomain){
        mutableState.update {
            it.copy(
                lastScannedContact = contact
            )
        }
    }

    fun readTag(tagData: String) {

        mutableState.update {
            it.copy(
                nfcResult = tagData
            )
        }
    }

    fun readTagContact(tagData: ContactDomain) {

        mutableState.update {
            it.copy(
                currentContact = tagData
            )
        }
    }

    fun isSuccess(state: Boolean, message: String){
        mutableState.update {
            it.copy(
                isSuccess = state,
                message = message
            )
        }
    }

}