package com.taptag.project.ui.screens.settings

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.flow.update

data class SettingsState(
    val isDarkMode: Boolean = false
)

class SettingsScreenModel() :
    StateScreenModel<SettingsState>(initialState = SettingsState()) {

    fun observeDarkMode(state: Boolean) {

        mutableState.update {
            it.copy(
                isDarkMode = state
            )
        }

    }
}