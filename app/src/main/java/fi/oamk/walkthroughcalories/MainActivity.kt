package fi.oamk.walkthroughcalories

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import fi.oamk.walkthroughcalories.ui.theme.WalkthroughCaloriesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalkthroughCaloriesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WalkthroughCalories(
                        modifier = Modifier.padding(innerPadding),

                    )

                }
            }
        }
    }
}


@Composable
fun WalkthroughCalories( modifier: Modifier = Modifier) {
    var weightInput by remember { mutableStateOf("") }
    val weight = weightInput.toIntOrNull() ?: 0
    var male by remember { mutableStateOf(true) }
    var intensity by remember { mutableFloatStateOf(1.3f) }
    var result by remember{mutableIntStateOf(0)}
    Column (
        modifier = modifier.padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Heading("Calories")
        WeightField(weightInput = weightInput,onValueChange={weightInput = it})
        GenderChoices(male, setGenderMale = {male = it})
        IntensityList (onClick={ intensity = it })
        Text(
            text = result.toString(),
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
        Calculation(
            male = male,
            weight = weight,
            intensity=intensity,
            setResult = {result = it}
        )
    }

}

@Composable
fun Calculation(male: Boolean, weight: Int, intensity: Float, setResult: (Int) -> Unit) {
    Button(
        onClick = {
            val baseCalories = if (male) 879 + 10.2 * weight else 795 + 7.18 * weight
            setResult((baseCalories * intensity).toInt())
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "CALCULATE")
    }
}



@Composable
fun GenderChoices(male: Boolean, setGenderMale:(Boolean)->Unit) {
    Column {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = male,
                onClick = {setGenderMale(true)}
            )
            Text(text = "Male")
        }
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !male,
                onClick = {setGenderMale(false)}
            )
            Text(text = "Female")
        }
    }
}

@Composable
fun WeightField(weightInput: String,onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = weightInput,
        onValueChange=onValueChange,
        label={Text(text="Enter weight")},
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun Heading(title: String) {
    Text (
        text = title,
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp,bottom = 16.dp)
    )
}

@Composable
fun IntensityList(onClick: (Float) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Light") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val items = listOf("Light","Usual","Moderate","Hard","Very hard")
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    Column {
        OutlinedTextField(
            readOnly = true,
            value=selectedText,
            onValueChange = { selectedText = it},
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            label = {Text("Select intensity")},
            trailingIcon = {
                Icon(icon,"Select intensity",
                    Modifier.clickable { expanded =!expanded })
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded=false},
            modifier = Modifier
                .width(with(LocalDensity.current){textFieldSize.width.toDp()})
        ) {
            items.forEach{ item ->
                val intensity: Float = when(item) {
                    "Light" -> 1.3f
                    "Usual" -> 1.5f
                    "Moderate" -> 1.7f
                    "Hard" -> 2f
                    "Very hard" -> 2.2f
                    else -> 0.0f
                }
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onClick(intensity)
                        selectedText = item
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalkthroughCaloriesPreview() {
    WalkthroughCaloriesTheme {
        WalkthroughCalories()
    }
}