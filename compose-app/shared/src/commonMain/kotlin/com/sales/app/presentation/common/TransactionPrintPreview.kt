
package com.sales.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PrintItem(
    val name: String,
    val qty: String,
    val price: String,
    val total: String
)

@Composable
fun TransactionPrintPreview(
    title: String,
    details: Map<String, String>,
    items: List<PrintItem>,
    totalAmount: String
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    val extraWidth = (scale - 1) * size.width
                    val extraHeight = (scale - 1) * size.height
                    val maxX = extraWidth / 2
                    val maxY = extraHeight / 2
                    offsetX = (offsetX + pan.x * scale).coerceIn(-maxX, maxX)
                    offsetY = (offsetY + pan.y * scale).coerceIn(-maxY, maxY)
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
                .background(Color.White) // Print usually on white paper
                .padding(24.dp)
        ) {
            // Header
            Text(
                text = "Sales App", // TODO: Get company name from settings
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Details Grid
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                details.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "$key:",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black),
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Items Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Item",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
                Text(
                    text = "Qty",
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
                Text(
                    text = "Price",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
                Text(
                    text = "Total",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
            }
            
            // Items List
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = Color.Black.copy(alpha = 0.1f)) // Light border for rows
                            .padding(8.dp)
                    ) {
                        Text(
                            text = item.name,
                            modifier = Modifier.weight(2f),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                        )
                        Text(
                            text = item.qty,
                            modifier = Modifier.weight(0.5f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                        )
                        Text(
                            text = item.price,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                        )
                        Text(
                            text = item.total,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grand Total
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Grand Total:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black),
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = totalAmount,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Footer
            Text(
                text = "Thank you for your business!",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
