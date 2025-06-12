package com.example.courtgate.authentication.presentation.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courtgate.authentication.domain.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    var state by mutableStateOf(SignUpState())
        private set

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EmailChange -> state = state.copy(email = event.email)
            is SignUpEvent.PasswordChange -> state = state.copy(password = event.password)
            SignUpEvent.SignIn -> state = state.copy(signIn = true)
            SignUpEvent.SignUp -> signUp()
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            authenticationRepository.signUp(state.email, state.password).onSuccess {
                println()
            }.onFailure {
                val error = it.message
                println(error)
            }
        }
    }
}