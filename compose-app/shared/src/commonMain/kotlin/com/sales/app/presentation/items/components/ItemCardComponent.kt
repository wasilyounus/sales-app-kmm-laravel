package com.sales.app.presentation.items.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sales.app.presentation.items.model.ItemUiModel

/**
 * Item card component matching the reference app design
 * Background color: #F0F0D9 (cream/yellow)
 */
@Composable
fun ItemCardComponent(
    modifier: Modifier = Modifier,
    itemUiModel: ItemUiModel,
    onClick: () -> Unit
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF0F0D9)
    
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .wrapContentHeight()
        ) {
            // Item name
            Text(
                text = itemUiModel.itemName,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
            
            // HSN code (if available)
            if (itemUiModel.itemHsn.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = itemUiModel.itemHsn,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Size and UQC row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (itemUiModel.itemSize.isNotBlank()) {
                    Text(
                        text = itemUiModel.itemSize,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                Text(
                    text = itemUiModel.itemUqc,
                    fontSize = 10.sp,
                )
            }
        }
    }
}
