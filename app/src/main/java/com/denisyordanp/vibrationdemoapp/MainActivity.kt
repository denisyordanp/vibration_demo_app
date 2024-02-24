package com.denisyordanp.vibrationdemoapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.denisyordanp.vibrationdemoapp.ui.theme.VibrationDemoAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VibrationDemoAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (getVibrator().hasVibrator()) {
                            MainContent()
                        } else {
                            Text(
                                text = "This device doesn't support vibration.",
                                style = TextStyle.Default.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    @Composable
    private fun MainContent() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = DestinationsScreen.MAIN
        ) {
            composable(DestinationsScreen.MAIN) {
                MainScreen(
                    onCustom = {
                        navController.navigate(DestinationsScreen.CUSTOM)
                    },
                    onDefault = {
                        navController.navigate(DestinationsScreen.DEFAULT)
                    }
                )
            }

            composable(DestinationsScreen.DEFAULT) {
                DefaultVibrationScreen(
                    onClickVibration = {
                        handleDefaultEffect(VibrationEffect.EFFECT_CLICK)
                    },
                    onDoubleClickVibration = {
                        handleDefaultEffect(VibrationEffect.EFFECT_DOUBLE_CLICK)
                    },
                    onHeavyClickVibration = {
                        handleDefaultEffect(VibrationEffect.EFFECT_CLICK)
                    },
                    onTickVibration = {
                        handleDefaultEffect(VibrationEffect.EFFECT_TICK)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(DestinationsScreen.CUSTOM) {
                CustomVibrationScreen(
                    onVibrate = { duration, strength, defaultStrength ->
                        val vibrationEffect = VibrationEffect.createOneShot(
                            duration.toLong(),
                            if (defaultStrength) VibrationEffect.DEFAULT_AMPLITUDE else strength
                        )
                        getVibrator().vibrate(vibrationEffect)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    private fun getVibrator() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun handleDefaultEffect(effect: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val vibrationEffect = VibrationEffect.createPredefined(effect)
            getVibrator().vibrate(vibrationEffect)
        } else {
            Toast.makeText(this, "This device api is below 29.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onCustom: () -> Unit,
    onDefault: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        CustomButton(text = "Custom Vibration", onClick = onCustom)
        CustomButton(text = "Default Effect", onClick = onDefault)
    }
}

@Composable
fun CustomVibrationScreen(
    modifier: Modifier = Modifier,
    onVibrate: (duration: Int, strength: Int, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        var durationInput by remember { mutableIntStateOf(1000) }
        var strengthInput by remember { mutableIntStateOf(1) }
        var defaultStrength by remember { mutableStateOf(true) }

        Text(text = "Vibrate duration")
        Spacer(modifier = Modifier.width(4.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = durationInput.toString(),
            onValueChange = {
                durationInput = it.toIntOrNull() ?: 0
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            suffix = {
                Text(text = "ms", style = TextStyle.Default.copy(fontWeight = FontWeight.Bold))
            },
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Vibrate strength (1-255)")
        Spacer(modifier = Modifier.width(4.dp))
        Row {
            Slider(
                modifier = Modifier.weight(1f),
                enabled = defaultStrength.not(),
                value = strengthInput.toFloat(),
                onValueChange = { strengthInput = it.toInt() },
                valueRange = 1f..255f
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = strengthInput.toString(),
                style = TextStyle.Default.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Row {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically), text = "Default strength"
            )
            Switch(checked = defaultStrength, onCheckedChange = { defaultStrength = it })
        }
        Spacer(modifier = Modifier.height(16.dp))
        CustomButton(
            text = "Vibrate",
            onClick = { onVibrate(durationInput, strengthInput, defaultStrength) })
        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(text = "Back", onClick = onBack)
    }
}

@Composable
fun DefaultVibrationScreen(
    modifier: Modifier = Modifier,
    onClickVibration: () -> Unit,
    onDoubleClickVibration: () -> Unit,
    onHeavyClickVibration: () -> Unit,
    onTickVibration: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "This options is only support for Android API 29 or above.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(text = "Effect Click", onClick = onClickVibration)
        CustomButton(text = "Effect Double Click", onClick = onDoubleClickVibration)
        CustomButton(text = "Effect Heavy Click", onClick = onHeavyClickVibration)
        CustomButton(text = "Effect Tick Click", onClick = onTickVibration)
        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(text = "Back", onClick = onBack)
    }
}

@Composable
private fun CustomButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    VibrationDemoAppTheme {
        MainScreen(
            onCustom = {},
            onDefault = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomVibrationScreenPreview() {
    VibrationDemoAppTheme {
        CustomVibrationScreen(onVibrate = { _, _, _ -> }, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultVibrationScreenPreview() {
    VibrationDemoAppTheme {
        DefaultVibrationScreen(
            onClickVibration = {},
            onDoubleClickVibration = {},
            onHeavyClickVibration = {},
            onTickVibration = {},
            onBack = {}
        )
    }
}