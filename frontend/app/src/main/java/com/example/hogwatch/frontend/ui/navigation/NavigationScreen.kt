
package com.example.hogwatch.frontend.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hogwatch.frontend.R


@Composable
fun HomeMenu(
   onInfoClick: () -> Unit,
   onAboutClick: () -> Unit,
   onSoundClick: () -> Unit,
   onMapClick: () -> Unit,
   onRescueClick: () -> Unit,
   onCsvClick: () -> Unit,
   onUserSubClick: () -> Unit
) {

   var isExpanded by remember { mutableStateOf(false) }

   Row(
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 12.dp, end = 0.dp),
      horizontalArrangement = Arrangement.End
   ) {
      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
         Box {
            IconButton(
               onClick = { isExpanded = true },
               modifier = Modifier.size(width = 64.dp, height = 64.dp)
            ) {
               Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
               ) {
                  Icon(
                     painter = painterResource(id = R.drawable.menu_icon_brown2),
                     contentDescription = "Menu",
                     modifier = Modifier.size(30.dp),
                     tint = Color.Unspecified
                  )
                  Text(
                     text = "Menu",
                     fontSize = 12.sp,
                     color = Color(0xFF8B4513)
                  )
               }
            }

            DropdownMenu(
               expanded = isExpanded,
               onDismissRequest = { isExpanded = false }
            ) {/* Map menu i dropdown - unused
               DropdownMenuItem(
                  text = { Text("Map") },
                  onClick = {
                     isExpanded = false
                     onMapClick()
                  }
               )*/
               DropdownMenuItem(
                  text = { Text("My Submissions") },
                  onClick = {
                     isExpanded = false
                     onUserSubClick()
                  }
               )
               DropdownMenuItem(
                  text = { Text("Info Hedgehogs") },
                  onClick = {
                     isExpanded = false
                     onInfoClick()
                  }
               )
               DropdownMenuItem(
                  text = { Text("Hedgehog Sounds") },
                  onClick = {
                     isExpanded = false
                     onSoundClick()
                  }
               )
               DropdownMenuItem(
                  text = { Text("Rescue Guide") },
                  onClick = {
                     isExpanded = false
                     onRescueClick()
                  }
               )
               DropdownMenuItem(
                  text = { Text("CSV Download") },
                  onClick = {
                     isExpanded = false
                     onCsvClick()
                  }
               )/* Unused about page
               DropdownMenuItem(
                  text = { Text("About HogWatch") },
                  onClick = {
                     isExpanded = false
                     onAboutClick()
                  }
               )*/
            }
         }
         IconButton(
            onClick = onMapClick,
            modifier = Modifier.size(width = 64.dp, height = 64.dp)
         ) {
            Column(
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.Center
            ) {
               Icon(
                  painter = painterResource(id = R.drawable.map_icon),
                  contentDescription = "Map",
                  modifier = Modifier.size(30.dp),
                  tint = Color.Unspecified
               )
               Text(
                  text = "Map",
                  fontSize = 12.sp,
                  color = Color(0xFF8B4513)
               )
            }
         }
      }
   }
}
