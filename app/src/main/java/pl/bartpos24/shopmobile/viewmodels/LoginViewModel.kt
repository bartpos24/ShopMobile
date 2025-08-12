package pl.bartpos24.shopmobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import pl.bartpos24.shopmobile.repositories.TokenRepository
import pl.bartpos24.shopmobile.utilities.LoginStatus
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val tokenRepository: TokenRepository) : ShopMobileViewModel() {
    private val _loginError = MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val loginError: SharedFlow<String> = _loginError

    private val _authenticationState = MutableLiveData<LoginStatus>()
    val authenticationState: LiveData<LoginStatus> = _authenticationState
    val loginInProgress = authenticationState.map { it == LoginStatus.AUTHENTICATING }
}