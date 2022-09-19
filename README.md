# test_android_performance

# https://github.com/ColdTea-Projects/ThouShaltCompose - drag and drop example

package com.example.myapplicationtestcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplicationtestcompose.ui.theme.MyApplicationTestComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            getView()
        }
    }
}

@Composable
fun getView() {
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp.value
    val screenHeight = configuration.screenHeightDp.dp.value

    var randomWidth = remember {
        mutableStateOf(0)
    }
    var randomHeight = remember {
        mutableStateOf(0)
    }

    for (i in 0..1000) {
        randomWidth.value = (0..screenWidth.toInt()).random()
        randomHeight.value = (0..screenHeight.toInt()).random()
        Image(
            painter = painterResource(id = R.drawable.cat),
            contentDescription = "cat",
            modifier = Modifier
                .width(Dp(randomWidth.value.toFloat()))
                .height(Dp(randomHeight.value.toFloat()))
                .fillMaxSize()
        )
    }

}
