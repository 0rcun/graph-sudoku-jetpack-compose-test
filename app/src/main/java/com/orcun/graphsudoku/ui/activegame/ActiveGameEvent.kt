package com.orcun.graphsudoku.ui.activegame


/**
 * Created by orcun on 2.07.2022
 */

sealed class ActiveGameEvent{
    data class OnInput(val input: Int): ActiveGameEvent()
    data class OnTileFocused(val x: Int, val y: Int): ActiveGameEvent()
    object OnNewGameClicked: ActiveGameEvent()
    object OnStart: ActiveGameEvent()
    object OnStop: ActiveGameEvent()

}
