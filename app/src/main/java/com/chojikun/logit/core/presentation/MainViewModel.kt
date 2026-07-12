package com.chojikun.logit.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chojikun.logit.core.util.FirstLaunchManager
import com.chojikun.logit.core.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firstLaunchManager: FirstLaunchManager,
    sessionManager: SessionManager
) : ViewModel() {

    val isFirstLaunch: StateFlow<Boolean?> = firstLaunchManager.isFirstLaunch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLoggedIn: StateFlow<Boolean?> = sessionManager.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sessionExpired: SharedFlow<Unit> = sessionManager.sessionExpired

    fun onEntryScreenDone() {
        viewModelScope.launch {
            firstLaunchManager.markLaunched()
        }
    }

}
