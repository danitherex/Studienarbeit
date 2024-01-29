package com.example.studienarbeit.presentation.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studienarbeit.domain.model.MarkerModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkerBottomSheet(
    onDismissRequest: () -> Unit,
    bottomSheetState: MarkerModel,
    currentUser: String,
    sheetState: SheetState,
    onDeleteRequest: () -> Unit
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog)
        ConfirmDeleteDialog(
            onConfirm = onDeleteRequest,
            title = bottomSheetState.title,
            onDismissRequest = { showDeleteDialog = false }
        )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    text = bottomSheetState.title
                )
                if (bottomSheetState.userID == currentUser)
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically)
                            .weight(1f, false)
                            .size(40.dp),
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "remove marker",
                            tint = Color.Red
                        )
                    }
            }

            Text(
                text = bottomSheetState.description,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

