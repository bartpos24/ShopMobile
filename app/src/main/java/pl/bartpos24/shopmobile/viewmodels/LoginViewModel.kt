package pl.bartpos24.shopmobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.repositories.ProductRepository
import pl.bartpos24.shopmobile.repositories.TokenRepository
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.utilities.toShopApiMessage
import timber.log.Timber
import javax.inject.Inject

//@ExperimentalCoroutinesApi
class LoginViewModel @Inject constructor(private val tokenRepository: TokenRepository, private val productRepository: ProductRepository) : ShopMobileViewModel() {
    private val _loginError = MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val loginError: SharedFlow<String> = _loginError

    private val _authenticationState = MutableLiveData<LoginStatus>()
    val authenticationState: LiveData<LoginStatus> = _authenticationState
    val loginInProgress = authenticationState.map { it == LoginStatus.AUTHENTICATING }

    init {
        tokenRepository.logout()
        _authenticationState.value = LoginStatus.UNAUTHENTICATED
    }


    fun login(login: String?, password: String?, ssaid: String) {
        viewModelScope.launch {
            tokenRepository.login(login ?: "", password ?: "", ssaid)
                .onStart { _authenticationState.postValue(LoginStatus.AUTHENTICATING) }
                .onCompletion {
                    if (it == null) {
                        _authenticationState.postValue(LoginStatus.AUTHENTICATED)
                        tokenRepository.createRefreshTokenWorker()
                        MainActivity.loginAuth.setStatus(LoginStatus.AUTHENTICATED)
                    }
                }.catch {
                    Timber.d(it, "Login Failed")
                    _authenticationState.postValue(LoginStatus.INVALID_AUTHENTICATION)
                    _loginError.tryEmit(it.toShopApiMessage())
                }.launchIn(this)
        }
    }

    fun logout() {
        tokenRepository.logout()
        _authenticationState.value = LoginStatus.UNAUTHENTICATED
    }
    override fun onCleared() {
        super.onCleared()
        logout()
    }

    suspend fun refreshToken() = tokenRepository.refreshToken()
        .onEach {
            tokenRepository.setNewAccessToken(it)
        }
        .catch {
            offerError(it.toShopApiMessage())
        }
        .singleOrNull()
}