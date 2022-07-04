package com.orcun.graphsudoku.ui.activegame

import com.orcun.graphsudoku.common.BaseLogic
import com.orcun.graphsudoku.common.DispatcherProvider
import com.orcun.graphsudoku.domain.IGameRepository
import com.orcun.graphsudoku.domain.IStatisticsRepository
import com.orcun.graphsudoku.domain.SudokuPuzzle
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * Created by orcun on 2.07.2022
 */

class ActiveGameLogic(
    private val container: ActiveGameContainer?,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    private val dispatcher: DispatcherProvider
    ): BaseLogic<ActiveGameEvent>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker


    inline fun startCoroutineTimer(
        crossinline action: () -> Unit
    ) = launch {
        while (true) {
            action()
            delay(1000)
        }
    }

    //Time offset makes the UI timer look more consistent
    private val Long.timeOffset: Long
        get() {
            return if (this <= 0) 0
            else this - 1
        }

    private var timerTracker: Job? = null

    override fun onEvent(event: ActiveGameEvent) {
        when(event){
            ActiveGameEvent.OnNewGameClicked -> onNewGameClicked()
            is ActiveGameEvent.OnInput -> onInput(
                event.input,
                viewModel.timerState
            )
            ActiveGameEvent.OnStart -> onStart()
            ActiveGameEvent.OnStop -> onStop()
            is ActiveGameEvent.OnTileFocused -> onTileFocused(event.x, event.y)
        }
    }

    private fun onTileFocused(x: Int, y: Int) {
        viewModel.updateFocusState(x, y)
    }

    private fun onStop() {
        if (!viewModel.isComplateState){
            launch {
                    gameRepo.saveGame(
                        viewModel.timerState.timeOffset,
                        { cancelStuff() },
                        {
                            cancelStuff()
                            container?.showError()
                        }
                    )
            }
        } else {
            cancelStuff()
        }
    }

    private fun onStart() = launch {
        gameRepo.getCurrentGame(
            {currentGame, isComplete ->
                viewModel.initializeBoardState(
                    currentGame,isComplete
                )
                if (!isComplete) timerTracker = startCoroutineTimer { viewModel.updateTimerState() }
            },
            {
                container?.onNewGameClick()
            }
        )
    }

    private fun onNewGameClicked() = launch {
        viewModel.showLoadingState()

        if (!viewModel.isComplateState){
            gameRepo.getCurrentGame(
                { currentGame: SudokuPuzzle, _: Boolean ->
                    updateWithTime(currentGame)
                },
                {
                    container?.showError()
                }

            )
        } else {
            navigateToNewGame()
        }

    }

    private fun updateWithTime(currentGame: SudokuPuzzle) = launch {
        gameRepo.updateGame(
            currentGame.copy(elapsedTime = viewModel.timerState.timeOffset),
            { navigateToNewGame() },
            {
                container?.showError()
                navigateToNewGame()
            }
        )
    }

    private fun navigateToNewGame() {
        cancelStuff()
        container?.onNewGameClick()
    }

    private fun cancelStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }

    private fun onInput(input: Int, elapsedTime: Long) = launch {
        var focusedTile: SudokuTile? = null
        viewModel.boardState.values.forEach {
            if (it.hasFocus) focusedTile = it
        }

        if (focusedTile != null){
            gameRepo.updateNode(
                focusedTile!!.x,
                focusedTile!!.y,
                input,
                elapsedTime,
                {isComplete ->
                    focusedTile?.let {
                        viewModel.updateBoardState(
                            it.x,
                            it.y,
                            input,
                            false
                        )
                    }
                    if (isComplete){
                        timerTracker?.cancel()
                        checkIfNewRecord()
                    }
                },
                { container?.showError() }

            )
        }
    }

    private fun checkIfNewRecord() = launch {
        statsRepo.updateStatistic(
            viewModel.timerState,
            viewModel.difficulty,
            viewModel.boundary,
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompleteState()
            },
            {
                container?.showError()
                viewModel.updateCompleteState()
            }
        )
    }


}