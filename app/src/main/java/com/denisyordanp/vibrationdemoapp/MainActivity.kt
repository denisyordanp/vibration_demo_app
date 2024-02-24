package com.denisyordanp.vibrationdemoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denisyordanp.vibrationdemoapp.ui.theme.VibrationDemoAppTheme

@SuppressLint("NewApi")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        setContent {
            VibrationDemoAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MainScreen(
                            modifier = Modifier.align(Alignment.Center),
                            onDefaultVibration = {
                                val vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
                                vibrator.vibrate(vibrationEffect)
                            },
                            onClickVibration = {
                                val vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                                vibrator.vibrate(vibrationEffect)
                            },
                            onDoubleClickVibration = {
                                val vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                                vibrator.vibrate(vibrationEffect)
                            },
                            onHeavyClickVibration = {
                                val vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                                vibrator.vibrate(vibrationEffect)
                            },
                            onTickVibration = {
                                val vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                                vibrator.vibrate(vibrationEffect)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onDefaultVibration: () -> Unit,
    onClickVibration: () -> Unit,
    onDoubleClickVibration: () -> Unit,
    onHeavyClickVibration: () -> Unit,
    onTickVibration: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        CustomButton(text = "Default Amplitude", onClick = onDefaultVibration)
        CustomButton(text = "Effect Click", onClick = onClickVibration)
        CustomButton(text = "Effect Double Click", onClick = onDoubleClickVibration)
        CustomButton(text = "Effect Heavy Click", onClick = onHeavyClickVibration)
        CustomButton(text = "Effect Tick Click", onClick = onTickVibration)
    }
}

@Composable
private fun CustomButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VibrationDemoAppTheme {
        MainScreen(
            onDefaultVibration = {},
            onClickVibration = {},
            onDoubleClickVibration = {},
            onHeavyClickVibration = {},
            onTickVibration = {},
        )
    }
}