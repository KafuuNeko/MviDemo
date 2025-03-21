package cc.kafuu.mvidemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cc.kafuu.mvidemo.core.CoreActivity

class MainActivity : CoreActivity() {
    private val mViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        mViewModel.emit(MainUiIntent.PageCreate)
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun ViewContent() {
        val uiState by mViewModel.uiStateFlow.collectAsState()
        MainLayout(
            uiState = uiState,
            onEmitUiIntent = { mViewModel.emit(it) }
        )
    }

}