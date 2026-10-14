package com.example.hanziapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun WritingCanvas(modifier: Modifier = Modifier) {
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var current by remember { mutableStateOf(listOf<Offset>()) }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { current = listOf(it) },
                        onDragEnd = { paths.add(current); current = emptyList() },
                        onDrag = { change, _ -> current = current + change.position }
                    )
                }
        ) {
            val step = size.width / 2
            drawLine(Color.LightGray, Offset(step, 0f), Offset(step, size.height), 2f)
            drawLine(Color.LightGray, Offset(0f, step), Offset(size.width, step), 2f)
            (paths + listOf(current)).forEach { stroke ->
                for (i in 0 until stroke.size - 1) {
                    drawLine(
                        color = Color.Black,
                        start = stroke[i], end = stroke[i + 1],
                        strokeWidth = 8f, cap = StrokeCap.Round
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { paths.clear(); current = emptyList() }) { Text("Clear") }
    }
}