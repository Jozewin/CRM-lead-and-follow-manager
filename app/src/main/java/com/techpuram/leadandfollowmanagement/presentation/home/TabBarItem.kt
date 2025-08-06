package com.techpuram.leadandfollowmanagement.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TabBarItem(
    val title: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val badgeAmount: Int? = null
)
