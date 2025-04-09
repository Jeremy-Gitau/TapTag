package com.taptag.project.ui.screens.settings

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.taptag.project.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val isDarkMode: Flow<Boolean> = flowOf(false),
    val accessToken: Flow<String> = flowOf("")
)

class SettingsScreenModel(
    private val preferenceRepository: PreferenceRepository
) : StateScreenModel<SettingsState>(initialState = SettingsState()) {

    init {
        observeDarkMode()
        observeAccessToken()
    }

    fun observeDarkMode() {

        screenModelScope.launch {

            val state = preferenceRepository.isDarkModeEnabled

            mutableState.update {
                it.copy(
                    isDarkMode = state
                )
            }
        }


    }

    fun toggleDarkMode() {
        screenModelScope.launch {
            preferenceRepository.toggleDarkMode()

            observeDarkMode()
        }
    }

    fun observeAccessToken() {

        screenModelScope.launch {
            mutableState.update {
                it.copy(
                    accessToken = preferenceRepository.readAccessToken()
                )
            }
        }
    }


}