package com.example.studienarbeit.presentation.addMarker.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studienarbeit.ui.theme.StudienarbeitTheme
import com.example.studienarbeit.utils.Icons

@Composable
fun TypeDropDown(
    modifier: Modifier = Modifier,
    type: String,
    onTypeChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Button(
            onClick = {
                expanded = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(6.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Row {
                Text(text = type.toTitleCase())
                Spacer(modifier = Modifier.width(8.dp))
                if (!expanded)
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowUp,
                        contentDescription = "Arrow Up"
                    )
                else
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                        contentDescription = "Arrow Down",
                    )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(10.dp)),
        ) {
            Icons.entries.forEach { icon ->
                if (icon.name != type && icon.name != Icons.PREVIEW.name)
                    DropdownMenuItem(
                        text = { Text(text = icon.name.toTitleCase()) },
                        onClick = {
                            onTypeChange(icon.name)
                            expanded = false
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = icon.resourceId),
                                contentDescription = icon.name
                            )
                        }
                    )

            }


        }

    }
}

fun String.toTitleCase(): String {
    if (this.isEmpty()) return this
    return this.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Preview
@Composable
fun TypeDropDownPreview() {
    StudienarbeitTheme {
        TypeDropDown(
            type = Icons.BURGER.name,
            onTypeChange = {}
        )
    }
}