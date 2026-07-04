package com.chojikun.logit.feature.auth.presentation

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chojikun.logit.core.network.util.ApiResult
import com.chojikun.logit.feature.auth.domain.usecase.LoginUseCase
import com.chojikun.logit.feature.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase : LoginUseCase,
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var emailError by mutableStateOf<String?>(null)
        private set

    var password by mutableStateOf("")
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set

    var confirmPassword by mutableStateOf("")
        private set
    var confirmPasswordError by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set
    var authError by mutableStateOf<String?>(null)
        private set

    private val _navigateToHome = Channel<Unit>(Channel.BUFFERED)
    val navigateToHome = _navigateToHome.receiveAsFlow()

    fun onEmailChange(value: String) {
        email = value
        emailError = validateEmailOrNull(value)
    }

    fun onPasswordChange(value: String) {
        password = value
        passwordError = validatePasswordOrNull(value)
        if (confirmPassword.isNotEmpty()) {
            confirmPasswordError = validateConfirmOrNull(confirmPassword, value)
        }
    }

    fun onConfirmPasswordChange(value: String) {
        confirmPassword = value
        confirmPasswordError = validateConfirmOrNull(value, password)
    }

    fun onRegisterClick() {
        if (!validateAll()) return
        viewModelScope.launch {
            isLoading = true
            authError = null
            when (val result = registerUseCase(email, password)) {
                is ApiResult.Success -> _navigateToHome.send(Unit)
                is ApiResult.Error   -> authError = result.message
            }
            isLoading = false
        }
    }

    fun onLoginClick(){
        if(!validateLoginScreen()) return
        viewModelScope.launch {
            isLoading = true
            authError = null
            when (val result = loginUseCase(email, password)){
                is ApiResult.Success -> _navigateToHome.send(Unit)
                is ApiResult.Error   -> authError = result.message
            }
            isLoading = false
        }
    }

    private fun validateLoginScreen() : Boolean{
        emailError           = validateEmailOrNull(email)
        passwordError        = validatePasswordOrNull(password)
        return emailError == null && passwordError == null
    }

    fun clearAuthError() { authError = null }

    private fun validateAll(): Boolean {
        emailError           = validateEmailOrNull(email)
        passwordError        = validatePasswordOrNull(password)
        confirmPasswordError = validateConfirmOrNull(confirmPassword, password)
        return emailError == null && passwordError == null && confirmPasswordError == null
    }

    private fun validateEmailOrNull(value: String): String? = when {
        value.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Enter a valid email address"
        else -> null
    }

    private fun validatePasswordOrNull(value: String): String? = when {
        value.isBlank() -> "Password is required"
        value.length <= 5 -> "Password must be at least 6 characters"
        !value.matches(Regex("^[a-zA-Z0-9]+\$")) -> "Password must be alphanumeric (letters and numbers only)"
        else -> null
    }

    private fun validateConfirmOrNull(value: String, against: String): String? = when {
        value.isBlank() -> "Please confirm your password"
        value != against -> "Passwords do not match"
        else -> null
    }
}
