package com.taptag.project.ui.composables.contact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactStatus
import com.taptag.project.domain.models.RelationshipStrength
import com.taptag.project.ui.screens.NFCScreen.NFCScreen
import com.taptag.project.ui.theme.NFCScannerTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ContactItem(
    contact: ContactDomain,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Contact header section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar and basic info
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar image
                Box(modifier = Modifier.wrapContentSize()) {
//                    Image(
//                        painter = rememberImagePainter(
//                            data = contact.avatarUrl
//                        ),
//                        contentDescription = "Avatar for ${contact.name}",
//                        modifier = Modifier
//                            .size(48.dp)
//                            .clip(CircleShape),
//                        contentScale = ContentScale.Crop
//                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Contact name and role
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = getRelationshipIcon(contact.relationshipStrength),
                            contentDescription = "Relationship Strength",
                            modifier = Modifier.size(16.dp),
                            tint = getRelationshipColor(contact.relationshipStrength)
                        )
                    }
                    Text(
                        text = "${contact.role} • ${contact.company}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    // Tags row - match React implementation
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        contact.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Tag,
                                        contentDescription = "Tag",
                                        modifier = Modifier.size(8.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp
                                        ),
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(getStatusBackgroundColor(contact.status))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getStatusIcon(contact.status),
                        contentDescription = "Status",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = getStatusLabel(contact.status),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Contact metadata section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Last contact and follow-up info
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = "Last Contact",
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Last contact: ${contact.lastContact}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (contact.scheduledDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = "Meeting",
                            modifier = Modifier.size(12.dp),
                            tint = Color.Magenta
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Meeting: ${contact.scheduledDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Magenta
                        )
                    }
                } else if (contact.nextFollowUp != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Follow-up",
                            modifier = Modifier.size(12.dp),
                            tint = Color.Yellow
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Follow up by: ${formatDate(contact.nextFollowUp)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Yellow
                        )
                    }
                }
            }

            // Action buttons
            Row {
                OutlinedButton(
                    onClick = { /* Phone action */ },
                    modifier = Modifier.size(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Green
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Green
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { /* Message action */ },
                    modifier = Modifier.size(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Green
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Message",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Green
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onExpandToggle,
                    modifier = Modifier.size(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }
        }

        // Expanded content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(12.dp)
            ) {
                // Notes section
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contact.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Contact history
                if (contact.history.isNotEmpty()) {
                    Text(
                        text = "Relationship Timeline",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Display contact history
                    contact.history.forEach { event ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            // Timeline dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .padding(top = 6.dp)
                                    .background(Color.Green, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = event.type.toString().lowercase()
                                            .replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Text(
                                        text = event.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = event.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { /* Snooze action */ },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        ),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            "Snooze Follow-up",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { /* Schedule action */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green
                        ),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = if (contact.scheduledDate != null) "Add Meeting Notes" else "Schedule Meeting",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Add divider between contacts
        if (!isExpanded) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun EmptyContactsView(navigator: Navigator, startScanning: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.LightGray.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.size(32.dp),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No contacts found",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try adjusting your search or filters",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navigator.push(NFCScreen())

//                startScanning()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = NFCScannerTheme.PrimaryGreen
            )
        ) {
            Text("Scan New Contact")
        }
    }
}

@Composable
private fun getRelationshipIcon(strength: RelationshipStrength): androidx.compose.ui.graphics.vector.ImageVector {
    return when (strength) {
        RelationshipStrength.NEW -> Icons.Default.Star
        RelationshipStrength.DEVELOPING -> Icons.AutoMirrored.Filled.StarHalf
        RelationshipStrength.STRONG -> Icons.Default.Star
    }
}

private fun getRelationshipColor(strength: RelationshipStrength): Color {
    return when (strength) {
        RelationshipStrength.NEW -> Color.Gray
        RelationshipStrength.DEVELOPING -> Color.Cyan
        RelationshipStrength.STRONG -> Color.Cyan
    }
}

private fun getStatusBackgroundColor(status: ContactStatus): Color {
    return when (status) {
        ContactStatus.PENDING -> Color.Red
        ContactStatus.FOLLOWED_UP -> Color.Cyan
        ContactStatus.SCHEDULED -> Color.Cyan
    }
}

@Composable
private fun getStatusIcon(status: ContactStatus): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        ContactStatus.PENDING -> Icons.Default.Circle
        ContactStatus.FOLLOWED_UP -> Icons.Default.CheckCircle
        ContactStatus.SCHEDULED -> Icons.Default.CalendarToday
    }
}

private fun getStatusLabel(status: ContactStatus): String {
    return when (status) {
        ContactStatus.PENDING -> "Need Follow-up"
        ContactStatus.FOLLOWED_UP -> "Followed Up"
        ContactStatus.SCHEDULED -> "Meeting Set"
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString == null) return ""
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date!!)
    } catch (e: Exception) {
        return dateString
    }
}