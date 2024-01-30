package com.example.studienarbeit.presentation.profile.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.presentation.components.ConfirmDeleteDialog
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Composable
fun MarkersTable(
    modifier: Modifier = Modifier,
    items: List<MarkerModel>,
    search: (String) -> Unit,
    userID: String,
    deleteMarker: (String) -> Flow<Response<String>>
) {

    Column {
        SearchBar(onSearch = { query -> search(query) })
        Spacer(modifier = Modifier.height(16.dp))
        ItemList(items = items, userID = userID, deleteMarker = deleteMarker)
    }

}

@Composable
fun ItemList(
    items: List<MarkerModel>,
    userID: String,
    deleteMarker: (String) -> Flow<Response<String>>
) {
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteMarker by remember { mutableStateOf(MarkerModel()) }

    if (showDeleteDialog)
        ConfirmDeleteDialog(
            onConfirm = {
                scope.launch {
                    deleteMarker(deleteMarker.id).collect {
                        if (it is Response.Success) {
                            Log.d("Map", "Successfully deleted marker")
                            showDeleteDialog = false
                        } else if (it is Response.Error) {
                            Log.d("Map", "Error deleting marker: ${it.message}")
                        }
                    }

                }
            },
            title = deleteMarker.title,
            onDismissRequest = { showDeleteDialog = false }
        )

    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "Title",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Type",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Divider(thickness = 1.dp, color = Color.Gray)
        }
        items(items.size) { index ->
            if (items[index].userID == userID)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 3.dp, vertical = 1.5.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            items[index].title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            items[index].type,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Normal
                        )
                        IconButton(
                            onClick = {
                                deleteMarker = items[index]
                                showDeleteDialog = true
                            },
                            modifier = Modifier
                                .size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                }
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = {
            text = it
            onSearch(it)
        },
        label = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true
    )
}

@Preview
@Composable
fun MarkersColumnPreview() {
    fun mockDeleteMarker(markerID: String) = flow {
        emit(Response.Success("Marker $markerID deleted successfully"))
    }
    MarkersTable(
        items = listOf(
            MarkerModel(
                position = GeoPoint(0.0, 0.0),
                title = "Test",
                description = "Test",
                type = "PIZZA",
                userID = "Test"
            ),
            MarkerModel(
                position = GeoPoint(0.0, 0.0),
                title = "Test2",
                description = "Test2",
                type = "PIZZA",
                userID = "Test"
            ),
            MarkerModel(
                position = GeoPoint(0.0, 0.0),
                title = "Test3",
                description = "Test3",
                type = "KEBAB",
                userID = "Test"
            ),
        ),
        search = {},
        userID = "Test",
        deleteMarker = { mockDeleteMarker(it) }
    )
}