package com.example.mysimpleledger.ui

import com.example.mysimpleledger.data.model.Transaction

public sealed class UiState{
    data class Success(val data: List<Transaction>) : UiState()
    data class Error(val message: String): UiState()
    object Loading: UiState()
    object Empty: UiState()
}