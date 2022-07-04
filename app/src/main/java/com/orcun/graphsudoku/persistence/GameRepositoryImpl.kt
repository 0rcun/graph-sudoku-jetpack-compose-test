package com.orcun.graphsudoku.persistence

import com.orcun.graphsudoku.domain.*
import java.lang.Exception

class GameRepositoryImpl (
    private val gameStorage: IGameDataStorage,
    private val settingsStorage: ISettingsStorage
): IGameRepository {

    override suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when(val getCurrentGameResult = gameStorage.getCurrentGame()){
            is GameStorageResult.OnSuccess ->{
                gameStorage.updateGame(
                    getCurrentGameResult.currentGame.copy(
                        elapsedTime = elapsedTime
                    )
                )
                onSuccess(Unit)
            }

            is GameStorageResult.OnError ->{
                onError(getCurrentGameResult.exception)
            }
        }
    }

    override suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when(val updateGameResult = gameStorage.updateGame(game)){
            is GameStorageResult.OnSuccess -> onSuccess(Unit)
            is GameStorageResult.OnError -> onError(updateGameResult.exception)
        }
    }

    override suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long,
        onSuccess: (isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when(val result = gameStorage.updateNode(x, y, color, elapsedTime)){
            is GameStorageResult.OnSuccess -> onSuccess(puzzleIsComplate(result.currentGame))
            is GameStorageResult.OnError -> onError(result.exception)
        }
    }

    override suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when(val getCurrentGameResult = gameStorage.getCurrentGame()){
            is GameStorageResult.OnSuccess -> onSuccess(
                getCurrentGameResult.currentGame,
                puzzleIsComplated(getCurrentGameResult.currentGame)
            )
            is GameStorageResult.OnError -> {
                when(val getSettingsResult = settingsStorage.getSettings()){
                    is SettingsStorageResult.OnSuccess -> {
                        when(val updateGameResult = createAndWriteNewGame(getSettingsResult.settings)){
                            is SettingsStorageResult.OnSuccess -> onSuccess(
                                updateGameResult.currentGame,
                                puzzleIsComplated(updateGameResult.currentGame)
                            )
                            is SettingsStorageResult.OnError-> onError(updateGameResult.exception)
                        }
                    }
                    is SettingsStorageResult.OnError -> onError(getSettingsResult.exception)
                }
            }
        }
    }

    override suspend fun createNewGame(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when(val updateSettingsResult = settingsStorage.updateSettings(settings)){
            SettingsStorageResult.OnComplete -> {
                when(val updateGameResult = createAndWriteNewGame(settings)){
                    is GameStorageResult.OnSuccess -> onSuccess(Unit)
                    is GameStorageResult.OnError -> onError(updateGameResult.exception)
                }
            }
            is SettingsStorageResult.OnError -> onError(updateSettingsResult.exception)
        }
    }

    private suspend fun createAndWriteNewGame(settings: Settings): GameStorageResult {
        return gameStorage.updateGame(
            SudokuPuzzle(
                settings.boundary,
                settings.difficulty
            )
        )
    }

    override suspend fun getSettings(onSuccess: (Settings) -> Unit, onError: (Exception) -> Unit) {
        when(val getSettingsResult = settingsStorage.getSettings()){
            is SettingsStorageResult.OnSuccess -> onSuccess(getSettingsResult.settings)
            is SettingsStorageResult.OnError -> onError(getSettingsResult.exception)
        }
    }

    override suspend fun updateSettings(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when(val updateSettingsResult = settingsStorage.updateSettings(settings)){
            is SettingsStorageResult.OnSuccess -> onSuccess(Unit)
            is SettingsStorageResult.OnError -> onError(updateSettingsResult.exception)
        }
    }
}