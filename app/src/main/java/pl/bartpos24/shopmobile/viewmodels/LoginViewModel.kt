package pl.bartpos24.shopmobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.repositories.TokenRepository
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.utilities.toShopApiMessage
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val tokenRepository: TokenRepository) : ShopMobileViewModel() {
    private val _loginError = MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val loginError: SharedFlow<String> = _loginError

    private val _authenticationState = MutableLiveData<LoginStatus>()
    val authenticationState: LiveData<LoginStatus> = _authenticationState
    val loginInProgress = authenticationState.map { it == LoginStatus.AUTHENTICATING }

    fun login(login: String?, password: String?, ssaid: String) {
        viewModelScope.launch {
            tokenRepository.login(login ?: "", password ?: "", ssaid)
                .onStart { _authenticationState.postValue(LoginStatus.AUTHENTICATING) }
                .onCompletion {
                    if (it == null) {
                        _authenticationState.postValue(LoginStatus.AUTHENTICATED)
                        //tokenRepository.createRefreshTokenWorker()
                        MainActivity.loginAuth.setStatus(LoginStatus.AUTHENTICATED)
                    }
                }.catch {
                    Timber.d(it, "Login Failed")
                    _authenticationState.postValue(LoginStatus.INVALID_AUTHENTICATION)
                    _loginError.tryEmit(it.toShopApiMessage())
                }.launchIn(this)
        }
    }
}