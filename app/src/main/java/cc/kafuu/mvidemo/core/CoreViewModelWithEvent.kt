package cc.kafuu.mvidemo.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class CoreViewModelWithEvent<I, S, E>(initStatus: S) : CoreViewModel<I, S>(initStatus) {
    // Single Event (Model -> View)
    private val mSingleEventFlow = MutableSharedFlow<E>()
    val singleEventFlow = mSingleEventFlow.asSharedFlow()

    protected fun dispatchingEvent(event: E) {
        viewModelScope.launch {
            mSingleEventFlow.emit(event)
        }
    }
}

inline fun <I, S, E> CoreActivity.attachEventListener(
    viewModel: CoreViewModelWithEvent<I, S, E>,
    crossinline onSingleEvent: (event: E) -> Unit
) = lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.singleEventFlow.collect { it?.run { onSingleEvent(this) } }
    }
}