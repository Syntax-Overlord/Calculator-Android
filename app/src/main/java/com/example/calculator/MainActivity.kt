package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color(0xFF222222).toArgb()
            )
        )
        setContent {
            CalculatorTheme {
                Calculator()
            }
        }
    }
}

val inputs = mutableStateListOf<String>()

fun addInput(input: String) {
    // protect against empty list on first press
    val ops = setOf("+", "x", "÷", "-")
    if (inputs.isEmpty()) {
        if (input in ops) {
            // Prevent starting with an operator: prefix with 0 then operator
            inputs.add("0")
            inputs.add(input)
        } else {
            inputs.add(input)
        }
        return
    }

    when (input) {
        "+", "x", "÷", "-" -> {
            // if last token is an operator, replace it with the new one
            val last = inputs[inputs.lastIndex]
            if (last in ops) {
                inputs[inputs.lastIndex] = input
            } else {
                inputs.add(input)
            }
        }
        else -> {
            when (val last = inputs[inputs.lastIndex]) {
                in ops -> {
                    // if user presses '.', start a new decimal number 0.
                    if (input == ".") inputs.add("0.") else inputs.add(input)
                }
                "0" -> {
                    // replace leading zero unless adding decimal
                    inputs[inputs.lastIndex] = if (input == ".") "0." else input
                }
                else -> {
                    // prevent multiple decimals in same number
                    if (input == "." && last.contains('.')) {
                        // ignore
                    } else {
                        inputs[inputs.lastIndex] = last + input
                    }
                }
            }
        }
    }
}

fun parserInput(inputList: SnapshotStateList<String>) : Double {
    val items = inputList.toMutableList()
    if (items.isEmpty()) return 0.0

    val ops = setOf("÷", "x", "+", "-")
    if (items.lastOrNull() in ops) {
        items.removeAt(items.lastIndex)
    }

    if (items.isEmpty()) return 0.0

    // first resolve multiplication and division using Double
    val collapsed = mutableListOf<String>()
    var i = 0
    while (i < items.size) {
        val token = items[i]
        if ((token == "x" || token == "÷") && collapsed.isNotEmpty() && i + 1 < items.size) {
            val left = collapsed.removeAt(collapsed.lastIndex).toDouble()
            val right = items[i + 1].toDouble()
            val value = when (token) {
                "x" -> multiplication(left, right)
                "÷" -> division(left, right)
                else -> 0.0
            }
            collapsed.add(value.toString())
            i += 2
        } else {
            collapsed.add(token)
            i++
        }
    }

    // then resolve addition and subtraction
    var answer = collapsed[0].toDouble()
    i = 1
    while (i < collapsed.size - 1) {
        val op = collapsed[i]
        val right = collapsed[i + 1].toDouble()
        answer = when (op) {
            "+" -> addition(answer, right)
            "-" -> subtraction(answer, right)
            else -> answer
        }
        i += 2
    }

    return answer
}

@Composable
fun CalculatorButtons(
    text: String,
    modifier: Modifier = Modifier,
    color: Long = 0xFFFF6200,
    function: () -> Unit = {}
)
{
    Button(
        onClick = function,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(color),
            contentColor = Color(0xFFEAEFEF)
        ),
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}


@Composable
fun Calculator() {

    var input by rememberSaveable { mutableStateOf("0") } // Input string
    var result by rememberSaveable { mutableDoubleStateOf(0.0) } // Result (Double)



    Column(modifier = Modifier.fillMaxSize())
    {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Screen(input)
        }
        Spacer(
            modifier = Modifier
                .weight(0.06f)
                .fillMaxWidth()
                .background(Color(0xFF222222))
        )
        Column(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) // 7 8 9 =
            {
                CalculatorButtons(text = "7", modifier = Modifier.weight(1f)) {
                    addInput("7")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "8", modifier = Modifier.weight(1f)) {
                    addInput("8")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "9", modifier = Modifier.weight(1f)) {
                    addInput("9")
                    input = inputs.joinToString ( " " )
                }
                Column(modifier = Modifier.weight(1f)) {
                    CalculatorButtons(text = "C", modifier = Modifier.weight(2f), color = 0xFF262626) {
                        inputs.clear()
                        input = "0"
                        result = 0.0
                    }
                    CalculatorButtons(text = ".", modifier = Modifier.weight(1f), color = 0xFF262626) {
                        addInput(".")
                        input = inputs.joinToString(" ")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) // 4 5 6 x
            {
                CalculatorButtons(text = "4", modifier = Modifier.weight(1f)) {
                    addInput("4")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "5", modifier = Modifier.weight(1f)) {
                    addInput("5")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "6", modifier = Modifier.weight(1f)) {
                    addInput("6")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "x", modifier = Modifier.weight(1f), color = 0xFF262626) {
                    addInput("x")
                    input = inputs.joinToString ( " " )
                }
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) // 1 2 3 ÷
            {
                CalculatorButtons(text = "1", modifier = Modifier.weight(1f)) {
                    addInput("1")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "2", modifier = Modifier.weight(1f)) {
                    addInput("2")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "3", modifier = Modifier.weight(1f)) {
                    addInput("3")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "÷", modifier = Modifier.weight(1f), color = 0xFF262626) {
                    addInput("÷")
                    input = inputs.joinToString ( " " )
                }
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) // 0 + - C
            {
                CalculatorButtons(text = "0", modifier = Modifier.weight(1f)) {
                    addInput("0")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "+", modifier = Modifier.weight(1f), color = 0xFF262626) {
                    addInput("+")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "-", modifier = Modifier.weight(1f), color = 0xFF262626) {
                    addInput("-")
                    input = inputs.joinToString ( " " )
                }
                CalculatorButtons(text = "=", modifier = Modifier.weight(1f), color = 0xFF262626) {
                    try {
                        result  = parserInput(inputs)
                        // format display: drop trailing .0 when integer
                        input = if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
                    } catch (_: IllegalArgumentException) {
                        input = "Zero Division"
                    }
                    inputs.clear()
                    inputs.add(input)
                }
            }
        }
    }
}

@Composable
fun Screen(context: String = "0") {
    val scrollState = rememberScrollState()

    // When text changes, animate scroll to the end so the latest characters are visible
    LaunchedEffect(context) {
        // small delay is not required; animateScrollTo will handle zero-length safely
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFF262626))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            Text(
                text = context,
                color = Color(0xFFBFC9D1),
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

fun addition(a: Double, b: Double): Double {
    return a + b
}

fun subtraction(a: Double, b: Double): Double {
    return a - b
}

fun multiplication(a: Double, b: Double): Double {
    return a * b
}

fun division(a: Double, b: Double): Double {
    if (b == 0.0) {
        throw IllegalArgumentException("Cannot divide by zero")
    }
    return a / b
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CalculatorPreview() {
    CalculatorTheme {
        Calculator()
    }
}