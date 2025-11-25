package pl.bartpos24.shopmobile.utilities

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun Lifecycle.states() = callbackFlow<Lifecycle.State> {
    val listener = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreated() {
            trySend(Lifecycle.State.CREATED)
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroyed() {
            trySend(Lifecycle.State.DESTROYED)
        }
    }
    addObserver(listener)
    awaitClose { removeObserver(listener) }
}