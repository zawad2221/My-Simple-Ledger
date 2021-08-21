package com.example.mysimpleledger.data.repository

import com.example.mysimpleledger.data.model.request.body.RegistrationBody
import com.example.mysimpleledger.data.model.request.response.RegistrationResponse
import com.example.mysimpleledger.network.api.AuthApi
import com.example.mysimpleledger.ui.TestUiState
import com.example.mysimpleledger.ui.UiState
import com.example.mysimpleledger.utils.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository(val authApi: AuthApi) {

    private val _registrationDataState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val registrationDataState: StateFlow<TestUiState> = _registrationDataState

    var job: CompletableJob? = null

    suspend fun register(registrationBody: RegistrationBody){
        job = Job()
        job?.let { theJob->
            CoroutineScope(IO + theJob).launch {
                kotlin.runCatching {
                    withContext(Main){
                        _registrationDataState.value = TestUiState.Loading
                    }
                    authApi.register(registrationBody)
                }.onSuccess {
                    if(it.isSuccessful && it.body()!=null){
                        if(it.body()!!.Status.equals("Success"))
                            _registrationDataState.value = TestUiState.Success(Event(it.body() as RegistrationResponse))
                        else
                            _registrationDataState.value = TestUiState.Error(Event(it.body()!!.Message!!))
                    }
                }.onFailure {
                    _registrationDataState.value = TestUiState.Error(Event(it.message!!))
                }
                theJob.complete()
            }
        }
    }
    fun cancelJob(){
        job?.cancel()
    }
}