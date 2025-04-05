package com.taptag.project.ui.composables.contact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.taptag.project.ui.screens.contact.ContactScreenModel
import com.taptag.project.ui.screens.contact.ContactState

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit = {},
    onCancelSearch: () -> Unit = {},
    modifier: Modifier = Modifier,
    placeholder: String = "Search contacts, or notes...",
    searchHint: String? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isSearching by remember { mutableStateOf(false) }

    // Automatically focus on search bar when it appears
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
    ) {
        Box {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    onQueryChange(it)
                    isSearching = it.isNotEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    IconButton(onClick = { onCancelSearch() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                },
                trailingIcon = {
                    // Use explicit AnimatedVisibility
                    androidx.compose.animation.AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Search",
                            modifier = Modifier.clickable {
                                onQueryChange("")
                                isSearching = false
                            },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchSubmit(query)
                        keyboardController?.hide()
                    }
                )
            )
        }

        // Use explicit AnimatedVisibility for search hint
        AnimatedVisibility(
            visible = searchHint != null && !isSearching,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = searchHint ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ContactScreenSearchBar(
    contactScreenModel: ContactScreenModel,
    contactState: ContactState
) {
    SearchBar(
        query = contactState.searchQuery,
        onQueryChange = { query ->
            contactScreenModel.updateSearchQuery(query)
        },
        onSearchSubmit = { query ->
            contactScreenModel.performSearch(query, contacts = contactState.contacts)
        },
        onCancelSearch = {
            contactScreenModel.toggleSearchBar(false)
        },
        searchHint = "Try searching by name, email, or tag",
        placeholder = "Search contacts, or notes..."
    )
}