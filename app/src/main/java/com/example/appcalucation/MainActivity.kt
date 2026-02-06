package com.example.appcalucation

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private var currentInput = ""
    private val decimalFormat = DecimalFormat("###.##########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sử dụng layout nút tròn đã tạo
        setContentView(R.layout.activity_main_round)

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)

        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val button = it as Button
                if (currentInput == "0" && button.text != ".") {
                    currentInput = button.text.toString()
                } else {
                    currentInput += button.text.toString()
                }
                tvDisplay.text = currentInput
            }
        }

        val operatorButtons = mapOf(
            R.id.btnPlus to "+",
            R.id.btnMinus to "-",
            R.id.btnMultiply to "×",
            R.id.btnDivide to "÷"
        )

        operatorButtons.forEach { (id, symbol) ->
            findViewById<Button>(id).setOnClickListener {
                if (currentInput.isNotEmpty()) {
                    val lastChar = currentInput.last().toString()
                    // Nếu ký tự cuối là một phép tính khác, thì thay thế nó
                    if (lastChar in listOf("+", "-", "×", "÷")) {
                        currentInput = currentInput.dropLast(1) + symbol
                    } else {
                        currentInput += symbol
                    }
                    tvDisplay.text = currentInput
                }
            }
        }

        findViewById<Button>(R.id.btnAC).setOnClickListener {
            currentInput = "0"
            tvDisplay.text = currentInput
        }

        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            if (currentInput.isNotEmpty()) {
                try {
                    val result = calculateResult(currentInput)
                    currentInput = decimalFormat.format(result)
                    tvDisplay.text = currentInput
                } catch (e: Exception) {
                    tvDisplay.text = "Error"
                    currentInput = ""
                }
            }
        }
        
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener {
            if (currentInput.isNotEmpty() && currentInput != "0") {
                if (currentInput.startsWith("-")) {
                    currentInput = currentInput.substring(1)
                } else {
                    currentInput = "-$currentInput"
                }
                tvDisplay.text = currentInput
            }
        }

        findViewById<Button>(R.id.btnPercent).setOnClickListener {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                val result = value / 100
                currentInput = decimalFormat.format(result)
                tvDisplay.text = currentInput
            }
        }
    }

    private fun calculateResult(input: String): Double {
        // Thay thế ký tự hiển thị sang ký tự toán học
        var formula = input.replace("×", "*").replace("÷", "/")
        
        // Cách xử lý đơn giản: tìm vị trí phép tính đầu tiên
        val operators = listOf("+", "-", "*", "/")
        var operatorFound = ""
        var operatorIndex = -1
        
        for (op in operators) {
            val index = formula.indexOf(op)
            if (index != -1) {
                // Ưu tiên các phép tính không phải dấu âm ở đầu số
                if (index == 0 && op == "-") {
                    val nextIndex = formula.indexOf(op, 1)
                    if (nextIndex != -1) {
                        operatorFound = op
                        operatorIndex = nextIndex
                        break
                    }
                } else {
                    operatorFound = op
                    operatorIndex = index
                    break
                }
            }
        }

        if (operatorIndex == -1) return formula.toDoubleOrNull() ?: 0.0

        val leftPart = formula.substring(0, operatorIndex).toDoubleOrNull() ?: 0.0
        val rightPart = formula.substring(operatorIndex + 1).toDoubleOrNull() ?: 0.0

        return when (operatorFound) {
            "+" -> leftPart + rightPart
            "-" -> leftPart - rightPart
            "*" -> leftPart * rightPart
            "/" -> if (rightPart != 0.0) leftPart / rightPart else 0.0
            else -> leftPart
        }
    }
}
