package cc.kafuu.mvidemo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.kafuu.mvidemo.core.ActivityPreview

@Composable
fun MainLayout(
    uiState: MainUiState,
    onEmitUiIntent: (MainUiIntent) -> Unit
) {
    when (uiState) {
        MainUiState.None -> Unit
        is MainUiState.Master -> MasterLayout(uiState, onEmitUiIntent)
    }
}

@Composable
private fun MasterLayout(
    uiState: MainUiState.Master,
    onEmitUiIntent: (MainUiIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        // 列表视图区
        when (val listState = uiState.listState) {
            // 空状态，使用Spacer占位
            MainListState.None -> Spacer(modifier = Modifier.weight(1f))
            // 应用包名列表状态
            is MainListState.ApplicationPackages -> ApplicationPackagesLayout(
                modifier = Modifier.weight(1f),
                listState = listState,
                onEmitUiIntent = onEmitUiIntent
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            onClick = { onEmitUiIntent(MainUiIntent.LoadApplicationList(System.currentTimeMillis())) }
        ) {
            Text(text = stringResource(R.string.load_application_list))
        }
    }
}

/**
 * 应用列表
 */
@Composable
private fun ApplicationPackagesLayout(
    listState: MainListState.ApplicationPackages,
    onEmitUiIntent: (MainUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        items(listState.packages) {
            Spacer(modifier = Modifier.height(10.dp))
            ApplicationPackageItem(it)
        }
    }
}

/**
 * 应用列表表项
 */
@Composable
private fun ApplicationPackageItem(
    packageName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            text = packageName
        )
    }
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun MasterLayoutEmptyPreview() {
    val list = (0..100).map { "Item$it" }
    ActivityPreview(darkTheme = false) {
        MainLayout(
            uiState = MainUiState.Master(
                listState = MainListState.None
            ),
            onEmitUiIntent = {}
        )
    }
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun MasterLayoutPreview() {
    val list = (0..100).map { "Item$it" }
    ActivityPreview(darkTheme = false) {
        MainLayout(
            uiState = MainUiState.Master(
                listState = MainListState.ApplicationPackages(list)
            ),
            onEmitUiIntent = {}
        )
    }
}
