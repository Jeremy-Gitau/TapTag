package com.taptag.project.ui.screens.NFCScreen

import android.annotation.SuppressLint
import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.flow.update

data class NFCState(
    val isScanning: Boolean = false,
    val nfcResult: String? = null,
    val error: String? = null,
    val showErrorDialog: Boolean = false,
    val showResultDialog: Boolean = false
)

class NFCScreenModel : StateScreenModel<NFCState>(initialState = NFCState()) {


    fun toggleErrorDialog(state: Boolean) {
        mutableState.update {
            it.copy(
                showErrorDialog = state
            )
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
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

    fun readTag(tagData: String) {

        mutableState.update {
            it.copy(
                nfcResult = tagData
            )
        }
    }

}