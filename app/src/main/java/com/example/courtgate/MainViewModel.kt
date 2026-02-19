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

    /*Esto se calcula una vez al iniciar y nunca cambia. No hay Firebase AuthStateListener ni flujo
    de FirebaseAuth. Si el usuario inicia sesión y se recrea la actividad, se volverá a leer desde
    Firebase correctamente. Sin embargo:
    - Si un usuario cierra sesión (aún no implementado), isLoggedIn se mantiene verdadero hasta que el
proceso finaliza.
- No hay conexión reactiva al estado de autenticación de Firebase: la máquina virtual es una
instantánea única.*/
    var isLoggedIn by mutableStateOf(getUserIdUseCase() != null)
        private set

}
