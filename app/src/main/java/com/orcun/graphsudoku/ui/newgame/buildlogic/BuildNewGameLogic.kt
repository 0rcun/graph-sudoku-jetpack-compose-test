package com.orcun.graphsudoku.ui.newgame.buildlogic

import android.content.Context
import com.orcun.graphsudoku.ui.newgame.NewGameContainer
import com.orcun.graphsudoku.ui.newgame.NewGameLogic
import com.orcun.graphsudoku.ui.newgame.NewGameViewModel
import com.orcun.graphsudoku.common.ProductionDispatcherProvider
import com.orcun.graphsudoku.persistence.*
import com.orcun.graphsudoku.persistence.settingsDataStore

internal fun buildNewGameLogic(
    container: NewGameContainer,
    viewModel: NewGameViewModel,
    context: Context
): NewGameLogic {
    return NewGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(
            context.statsDataStore
        ),
        ProductionDispatcherProvider
    )
}