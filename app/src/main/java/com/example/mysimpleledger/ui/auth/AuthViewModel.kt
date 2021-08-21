package com.example.mysimpleledger.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleledger.data.model.request.body.RegistrationBody
import com.example.mysimpleledger.data.repository.AuthRepository
import com.example.mysimpleledger.ui.TestUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {
    var job: Job? = null

    var registrationBody: RegistrationBody? = null


    private val _registrationDataState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val registrationDataState: StateFlow<TestUiState> = _registrationDataState

    suspend fun registration(registrationBody: RegistrationBody){
        job = viewModelScope.launch {
            authRepository.register(registrationBody)
            authRepository.registrationDataState.collect{
                _registrationDataState.value = it
            }

        }
    }

    fun cancelJob(){
        job?.cancel()
        authRepository.cancelJob()
    }
}