package com.chojikun.logit.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoreContent(
    onLogoutTapped : () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        Text(
            "More",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
        )
        Spacer(
            Modifier.height(20.dp)
        )
        Box(
            Modifier.background(Color.White).fillMaxWidth()
        ){
            Row(
                Modifier.padding(10.dp).clickable(
                    onClick = onLogoutTapped,
                )
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Logout")
                Spacer(
                    Modifier.width(10.dp)
                )
                Text(
                    "Logout"
                )
            }
        }
    }
}