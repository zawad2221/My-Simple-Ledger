package com.example.mysimpleledger.ui

import com.example.mysimpleledger.utils.Event

open class TestUiState {
    data class Success(val data: Event<Any>?) : TestUiState()
    data class Error(val message: Event<String>?): TestUiState()
    object Loading: TestUiState()
    object Empty: TestUiState()
}