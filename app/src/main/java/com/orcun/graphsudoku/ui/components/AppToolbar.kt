package com.orcun.graphsudoku.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.orcun.graphsudoku.ui.textColorDark
import com.orcun.graphsudoku.ui.textColorLight


/**
 * Created by orcun on 2.07.2022
 */

@Composable
fun AppToolbar(
    modifier: Modifier,
    title: String,
    icon: @Composable () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                color = if(MaterialTheme.colors.isLight) textColorLight else textColorDark,
                textAlign = TextAlign.Start,
                maxLines = 1
            )
        },
        actions = {
            icon()
        }
    )

}