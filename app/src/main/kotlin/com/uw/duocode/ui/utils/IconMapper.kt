package com.uw.duocode.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.ui.graphics.vector.ImageVector

fun getTopicIcon(iconKey: String): ImageVector {
    return when (iconKey.lowercase()) {
        "viewlist" -> Icons.Filled.ViewList
        "link" -> Icons.Filled.Link
        "search" -> Icons.Filled.Search
        "nature" -> Icons.Filled.Nature
        "sort" -> Icons.Filled.Sort
        "share" -> Icons.Filled.Share
        "dynamicfeed" -> Icons.Filled.DynamicFeed
        else -> Icons.Filled.Code  // default icon
    }
}
