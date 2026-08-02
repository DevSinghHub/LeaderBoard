package com.example.leaderboard.ui.theme.compose

object AppTheme {
    val colors: AppColor = AppColor()
    val typography: AppTypography = AppTypography()
    val dimensions: AppDimensions = AppDimensions()
    val textDimensions: AppTextDimensions = AppTextDimensions()
}

data class AppThemeData(
    val colors: AppColor = AppColor(),
    val typography: AppTypography = AppTypography(),
    val dimensions: AppDimensions = AppDimensions(),
    val textDimensions: AppTextDimensions = AppTextDimensions()
)