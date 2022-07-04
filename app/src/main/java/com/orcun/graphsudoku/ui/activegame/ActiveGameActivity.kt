package com.orcun.graphsudoku.ui.activegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import com.orcun.graphsudoku.R
import com.orcun.graphsudoku.common.makeToast
import com.orcun.graphsudoku.ui.GraphSudokuTheme
import com.orcun.graphsudoku.ui.activegame.buildlogic.buildActiveGameLogic
import com.orcun.graphsudoku.ui.newgame.NewGameActivity
import kotlin.math.log

class ActiveGameActivity : AppCompatActivity(), ActiveGameContainer {
    private lateinit var logic: ActiveGameLogic
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ActiveGameViewModel()

        setContent {
            GraphSudokuTheme {
                ActiveGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }
        logic = buildActiveGameLogic(this, viewModel, applicationContext)
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)
    }

    override fun showError() = makeToast(getString(R.string.generic_error))

    override fun onNewGameClick() {
        startActivity(
            Intent(
                this,
                NewGameActivity::class.java
            )
        )
    }
}