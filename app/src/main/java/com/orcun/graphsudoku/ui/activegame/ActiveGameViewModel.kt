package com.orcun.graphsudoku.ui.activegame

import com.orcun.graphsudoku.domain.Difficulty
import com.orcun.graphsudoku.domain.SudokuPuzzle
import com.orcun.graphsudoku.domain.getHash


/**
 * Created by orcun on 2.07.2022
 */

class ActiveGameViewModel {
    internal var subBoardState: ((HashMap<Int, SudokuTile>)-> Unit)? = null
    internal var subContentState: ((ActiveGameScreenState)-> Unit)? = null
    internal var subTimerState: ((Long)-> Unit)? = null

    internal fun updateTimerState(){
        timerState++
        subTimerState?.invoke(1L)
    }

    internal var subIsComplateState: ((Boolean) -> Unit)? = null

    internal var timerState: Long = 0L

    internal var difficulty: Difficulty = Difficulty.MEDIUM
    internal var boundary: Int = 9
    internal var boardState: HashMap<Int, SudokuTile> = HashMap()
    internal var isComplateState: Boolean = false
    internal var isNewRecordState: Boolean = false


    fun initializeBoardState(
        puzzle: SudokuPuzzle,
        isComplete: Boolean
    ){
        puzzle.graph.forEach {
            val node = it.value[0]
            boardState[it.key] = SudokuTile(
                node.x,
                node.y,
                node.color,
                hasFocus = false,
                node.readOnly
            )
        }
        val contentState: ActiveGameScreenState

        if (isComplete){
            isComplateState = true
            contentState = ActiveGameScreenState.COMPLETE
        } else {

            contentState = ActiveGameScreenState.ACTIVE
        }

        boundary = puzzle.boundary
        difficulty = puzzle.difficulty
        timerState = puzzle.elapsedTime


        subIsComplateState?.invoke(isComplateState)
        subContentState?.invoke(contentState)
        subBoardState?.invoke(boardState)

    }

    internal fun updateBoardState(
        x: Int,
        y: Int,
        value: Int,
        hasFocus: Boolean
    ){
        boardState[getHash(x,y)]?.let {
            it.value = value
            it.hasFocus = hasFocus
        }
        subBoardState?.invoke(boardState)
    }

    internal fun showLoadingState(){
        subContentState?.invoke(ActiveGameScreenState.LOADING)
    }

    internal fun updateFocusState(x: Int, y: Int){
        boardState.values.forEach {
            it.hasFocus = (it.x == x && it.y == y)
        }

        subBoardState?.invoke(boardState)
    }

    internal fun updateCompleteState(){
        isComplateState = true
        subContentState?.invoke(ActiveGameScreenState.COMPLETE)
    }



}


class SudokuTile(
    val x: Int,
    val y: Int,
    var value: Int,
    var hasFocus: Boolean,
    val readOnly: Boolean,
)