@file:OptIn(ExperimentalMaterial3Api::class)

package com.taptag.project.ui.composables.nfc.write

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.taptag.project.data.nfcManager.getNFCManager
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.ui.composables.nfc.NFCWriteScreenContent
import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel
import com.taptag.project.ui.screens.contact.ContactScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ContactInputForm(
    contact: ContactDomain,
    onContactChange: (ContactDomain) -> Unit,
    nfcScreenModel: NFCScreenModel,
    contactScreenModel: ContactScreenModel,
    navigator: Navigator,
    scope: CoroutineScope
) {
    // Track validation state for each field
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var roleError by remember { mutableStateOf(false) }
    var companyError by remember { mutableStateOf(false) }
    var tagsError by remember { mutableStateOf(false) }
    var notesError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val nfcManager = getNFCManager()

    // Field label style
    val labelStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = Color.DarkGray
    )

    // Error text style
    val errorStyle = TextStyle(
        color = Color.Red,
        fontSize = 12.sp
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Name field
        Text(
            text = "Full Name *",
            style = labelStyle,
            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp, top = 8.dp)
        )
        OutlinedTextField(
            value = contact.name,
            onValueChange = {
                nameError = !contactScreenModel.validateRequiredField(it)
                onContactChange(contact.copy(name = it))
            },
            label = { Text("Name") },
            isError = nameError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {

                    nameError = !contactScreenModel.validateRequiredField(contact.name)

                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        if (nameError) {
            Text(
                text = "Name is required",
                style = errorStyle,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Email field
        Text(
            text = "Email Address *",
            style = labelStyle,
            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp, top = 8.dp)
        )
        OutlinedTextField(
            value = contact.email,
            onValueChange = {
                emailError = !contactScreenModel.validateEmailFormat(it)
                onContactChange(contact.copy(email = it))
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    emailError = !contactScreenModel.validateEmailFormat(contact.email)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        if (emailError) {
            Text(
                text = "Invalid Email",
                style = errorStyle,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Role and Company fields
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text(
                    text = "Role *",
                    style = labelStyle,
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp, top = 8.dp)
                )
                OutlinedTextField(
                    value = contact.role,
                    onValueChange = {
                        roleError = !contactScreenModel.validateRequiredField(it)
                        onContactChange(contact.copy(role = it))
                    },
                    label = { Text("Role") },
                    isError = roleError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            roleError = !contactScreenModel.validateRequiredField(contact.role)

                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )
                if (roleError) {
                    Text(
                        text = "Role is required",
                        style = errorStyle,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = "Company *",
                    style = labelStyle,
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp, top = 8.dp)
                )
                OutlinedTextField(
                    value = contact.company,
                    onValueChange = {
                        companyError = !contactScreenModel.validateRequiredField(it)
                        onContactChange(contact.copy(company = it))
                    },
                    label = { Text("Company") },
                    isError = companyError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            companyError = !contactScreenModel.validateRequiredField(contact.company)

                            focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                if (companyError) {
                    Text(
                        text = "Company is required",
                        style = errorStyle,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        // Tags input
        var tagsInput by remember { mutableStateOf(contact.tags.joinToString(", ")) }
        Text(
            text = "Tags *",
            style = labelStyle,
            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp, top = 8.dp)
        )
        OutlinedTextField(
            value = tagsInput,
            onValueChange = {
                tagsInput = it
                val newTags =
                    it.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotEmpty() }
                tagsError = newTags.isEmpty()
                onContactChange(contact.copy(tags = newTags))
            },
            label = { Text("Tags (comma separated)") },
            isError = tagsError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    tagsError = !contactScreenModel.validateTags(contact.tags)

                    focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        if (tagsError) {
            Text(
                text = "At least one tag is required",
                style = errorStyle,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Notes field
        Text(
            text = "Notes *",
            style = labelStyle,
            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp, top = 8.dp)
        )
        OutlinedTextField(
            value = contact.notes,
            onValueChange = {
                notesError = !contactScreenModel.validateRequiredField(it)
                onContactChange(contact.copy(notes = it))
            },
            label = { Text("Notes") },
            isError = notesError,
            minLines = 3,
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
        if (notesError) {
            Text(
                text = "Notes are required",
                style = errorStyle,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {

                    nameError = !contactScreenModel.validateRequiredField(contact.name)
                    emailError = !contactScreenModel.validateRequiredField(contact.email)
                    roleError = !contactScreenModel.validateRequiredField(contact.role)
                    companyError = !contactScreenModel.validateRequiredField(contact.company)
                    tagsError = !contactScreenModel.validateTags(contact.tags)
                    notesError = !contactScreenModel.validateRequiredField(contact.notes)

                    if(!nameError && !emailError && !roleError && !companyError && !tagsError && !notesError) {

                        nfcScreenModel.observeCurrentContact(contact)

                        navigator.push(NFCWriteScreenContent())
                    }

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Write to NFC Tag")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    nfcScreenModel.observeCurrentContact(ContactDomain())
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Clear Form")
            }
        }
    }
}