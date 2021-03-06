package com.orcun.graphsudoku.domain

import java.lang.Exception

interface ISettingsStorage {
    suspend fun getSettings():SettingsStorageResult
    suspend fun updateSettings(settings: Settings):SettingsStorageResult

}

sealed class SettingsStorageResult{
    object OnComplete : SettingsStorageResult()
    data class OnSuccess(val settings: Settings): SettingsStorageResult()
    data class OnError(val exception: Exception): SettingsStorageResult()
}