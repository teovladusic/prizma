package com.prizma_distribucija.prizma.feature_login.presentation.login

import androidx.lifecycle.*
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_login.domain.use_case.LogInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    state: SavedStateHandle,
    private val logInUseCase: LogInUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val code = state.getLiveData("code", "")

    fun onDigitAdded(digit: Int) = viewModelScope.launch(dispatchers.default) {
        var newCode = code.value ?: ""

        if (canAddNewDigitToCode(newCode)) {
            newCode = "$newCode$digit"
            code.postValue(newCode)
            if (isCodeMaxLength(newCode)) {
                logIn(newCode)
            }
        }
    }

    private fun canAddNewDigitToCode(code: String): Boolean {
        return code.length < 4
    }

    private fun isCodeMaxLength(code: String): Boolean {
        return code.length == 4
    }

    fun onDigitDeleted() {
        var newCode = this.code.value ?: ""
        if (newCode.isEmpty()) {
            //nothing to delete
            return
        }
        newCode = newCode.dropLast(1)
        code.postValue(newCode)
    }

    private val _signInStatus = MutableSharedFlow<Resource<User>>()
    val signInStatus = _signInStatus.asSharedFlow()

    private fun logIn(code: String) = viewModelScope.launch(dispatchers.io) {
        logInUseCase(code).collect {
            _signInStatus.emit(it)
        }
    }
}