package cc.kafuu.mvidemo

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import cc.kafuu.mvidemo.core.CoreViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MainViewModel : CoreViewModel<MainUiIntent, MainUiState>(initStatus = MainUiState.None),
    KoinComponent {
    override fun onReceivedUiIntent(uiIntent: MainUiIntent) {
        when (uiIntent) {
            MainUiIntent.PageCreate -> onPageCreate()
            MainUiIntent.LoadApplicationList -> onLoadApplicationList()
        }
    }

    private fun onPageCreate() {
        // 将uiState变更为Master状态
        MainUiState.Master().setup()
//        //上面的代码等价于下面的代码
//        _uiStateFlow.value = MainUiState.Master()
    }

    private fun onLoadApplicationList() = viewModelScope.launch {
        val applications = get<Context>().packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA
        )
        // awaitUiStateOfType等待uiState变成MainUiState.Master类型后才会返回
        // 如果当前uiState状态不是MainUiState.Master则会阻塞这个协程，直到状态变更为MainUiState.Master状态
        // 当uiState当前状态为Master状态则更新当前Master状态中的列表状态
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
    data object LoadApplicationList : MainUiIntent()
}