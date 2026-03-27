@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanager.ui.theme.TaskManagerTheme
import java.time.LocalTime
import java.util.Locale
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskEditorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TaskEditorScreen(modifier: Modifier = Modifier) {
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

    Column(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimeWheelPicker(
            currentTime = selectedTime, onTimeChange = { newTime -> selectedTime = newTime })

        Text(
            text = "Selecionado: ${
                String.format(
                    Locale.getDefault(), "%02d:%02d", selectedTime.hour, selectedTime.minute
                )
            }",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@Composable
fun TimeWheelPicker(
    currentTime: LocalTime, onTimeChange: (LocalTime) -> Unit
) {
    Row {
        // Coluna de Horas
        WheelColumn(
            items = (0..23).toList(), initialIndex = currentTime.hour, label = "", onSelect = { h ->
                if (h != currentTime.hour) {
                    onTimeChange(currentTime.withHour(h))
                }
            })
//Adicionar ":" entre horas e minutos
        // Coluna de Minutos
        WheelColumn(
            items = (0..59).toList(),
            initialIndex = currentTime.minute,
            label = "",
            onSelect = { m ->
                if (m != currentTime.minute) {
                    onTimeChange(currentTime.withMinute(m))
                }
            })
    }
}

@Composable
fun WheelColumn(
    items: List<Int>, initialIndex: Int, label: String, onSelect: (Int) -> Unit
) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = state)

    // Escuta mudanças na rolagem para disparar o onSelect
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex }.collect { index ->
            if (index < items.size) {
                onSelect(items[index])
            }
        }
    }

    LazyColumn(
        state = state,
        flingBehavior = snapBehavior,
        modifier = Modifier
            .height(150.dp)
            .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 50.dp) // Centraliza melhor o item
    ) {
        itemsIndexed(items) { _, item ->
            Text(
                text = "${item.toString().padStart(2, '0')}$label",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskManagerTheme {
        TaskEditorScreen()
    }
}