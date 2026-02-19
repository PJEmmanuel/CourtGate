package com.example.courtgate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.courtgate.usecases.authentication.GetUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getUserIdUseCase: GetUserIdUseCase,
) : ViewModel() {

    var isLoggedIn by mutableStateOf(getUserIdUseCase() != null)
        private set

}
