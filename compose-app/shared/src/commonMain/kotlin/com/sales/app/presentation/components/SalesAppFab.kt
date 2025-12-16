package com.sales.app.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sales.app.presentation.theme.Gold
import com.sales.app.presentation.theme.NavyBlue

/**
 * Standard FAB component for the Sales App following VTC design system.
 * Uses Gold background with Navy icon for brand consistency.
 */
@Composable
fun SalesAppFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = "Add"
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Gold,
        contentColor = NavyBlue,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Extended FAB component for the Sales App following VTC design system.
 * Uses Gold background with Navy icon and text for brand consistency.
 */
@Composable
fun SalesAppExtendedFab(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Gold,
        contentColor = NavyBlue,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            modifier = Modifier
        )
    }
}
