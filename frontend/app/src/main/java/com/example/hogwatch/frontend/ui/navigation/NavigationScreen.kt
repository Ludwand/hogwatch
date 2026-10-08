package com.example.hogwatch.frontend.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
/*This menu is AI-generated for a fast placeholder menu and will be replaced*/


@Composable
fun HomeMenu(
    onInfoClick: () -> Unit,
    onAboutClick: () -> Unit,
    onMapClick: () -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box {

            IconButton(onClick = { isExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Map") },
                    onClick = {
                        isExpanded = false
                        onMapClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("About HogWatch") },
                    onClick = {
                        isExpanded = false
                        onAboutClick()
                    }
                )
                DropdownMenuItem(
                    text ={ Text("Info Hedgehogs")},
                    onClick = {
                        isExpanded = false
                        onInfoClick()
                    }
                )
            }
        }
    }
}