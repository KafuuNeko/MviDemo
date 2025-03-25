package cc.kafuu.mvidemo

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.viewModelScope
import cc.kafuu.mvidemo.core.CoreViewModel
import cc.kafuu.mvidemo.core.UiIntentObserver
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MainViewModel : CoreViewModel<MainUiIntent, MainUiState>(initStatus = MainUiState.None),
    KoinComponent {
    companion object {
        private const val TAG = "MainViewModel"
    }

    @UiIntentObserver(MainUiIntent.PageCreate::class)
    private fun onPageCreate() {
        // 将uiState变更为Master状态
        MainUiState.Master().setup()
//        //上面的代码等价于下面的代码
//        _uiStateFlow.value = MainUiState.Master()
    }

    @UiIntentObserver(MainUiIntent.LoadApplicationList::class)
    private fun onLoadApplicationList(
        uiIntent: MainUiIntent.LoadApplicationList
    ) = viewModelScope.launch {
        Log.d(TAG, "onLoadApplicationList: ${uiIntent.time}")
        val applications = get<Context>().packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA
        )
        awaitUiStateOfType<MainUiState.Master>().copy(
            listState = MainListState.ApplicationPackages(applications.map { it.packageName })
        ).setup()
    }
}

/**
 * 主页Ui状态密封类
 */
sealed class MainUiState {
    /**
     * 空视图状态
     */
    data object None : MainUiState()

    /**
     * 页面主要状态
     */
    data class Master(
        val listState: MainListState = MainListState.None
    ) : MainUiState()
}

/**
 * 主页列表状态
 */
sealed class MainListState {
    /**
     * 空列表状态
     */
    data object None : MainListState()

    /**
     * 应用包名列表
     */
    data class ApplicationPackages(val packages: List<String>) : MainListState()
}

/**
 * 主页Ui意图密封类
 */
sealed class MainUiIntent {
    /**
     * 页面创建
     */
    data object PageCreate : MainUiIntent()

    /**
     * 刷新应用列表
     */
    data class LoadApplicationList(val time: Long) : MainUiIntent()
}