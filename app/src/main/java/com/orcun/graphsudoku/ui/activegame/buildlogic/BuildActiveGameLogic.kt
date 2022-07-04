package com.orcun.graphsudoku.ui.activegame.buildlogic

import android.content.Context
import androidx.datastore.dataStoreFile
import com.orcun.graphsudoku.common.ProductionDispatcherProvider
import com.orcun.graphsudoku.domain.SettingsStorageResult
import com.orcun.graphsudoku.persistence.*
import com.orcun.graphsudoku.persistence.statsDataStore
import com.orcun.graphsudoku.ui.activegame.ActiveGameContainer
import com.orcun.graphsudoku.ui.activegame.ActiveGameLogic
import com.orcun.graphsudoku.ui.activegame.ActiveGameViewModel


/**
 * Created by orcun on 2.07.2022
 */

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic {

return ActiveGameLogic(
    container,
    viewModel,
    GameRepositoryImpl(
        LocalGameStorageImpl(context.filesDir.path),
        LocalSettingsStorageImpl(context.settingsDataStore)
    ),
    LocalStatisticsStorageImpl(context.statsDataStore),
    ProductionDispatcherProvider
    )
}