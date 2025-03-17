package com.example.xiaomi16virtual

import com.example.xiaomi16virtual.R
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var etDisplay: EditText // show input box
    private var currentInput = StringBuilder() // save input

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 设置布局文件

        // 初始化
        etDisplay = findViewById(R.id.etDisplay)

        // 绑定所有数字按钮并统一处理点击事件
        val numberButtons = listOf(
            R.id.buttonZero, R.id.buttonOne, R.id.buttonTwo,
            R.id.buttonThree, R.id.buttonFour, R.id.buttonFive,
            R.id.buttonSix, R.id.buttonSeven, R.id.buttonEight, R.id.buttonNine
        )

        numberButtons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                appendToDisplay((it as Button).text.toString()) // show numbers
            }
        }

        // 绑定操作符按钮
        val operatorButtons = listOf(
            R.id.buttonAdd, R.id.buttonMinus, R.id.buttonTimes, R.id.buttonDivide, R.id.buttonDot
        )

        operatorButtons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                handleOperator((it as Button).text.toString()) // 处理运算符输入
            }
        }

        // 等号按钮
        findViewById<Button>(R.id.buttonEqual).setOnClickListener {
            calculateResult()
        }
    }

    private fun appendToDisplay(value: String) {
        currentInput.append(value)
        etDisplay.setText(currentInput.toString())
    }

    private fun handleOperator(operator: String) {
        if (currentInput.isEmpty()) return

        val lastChar = currentInput.last()
        if (lastChar.toString() in "+-×÷") {
            currentInput.deleteCharAt(currentInput.lastIndex)
        }
        currentInput.append(operator)
        etDisplay.setText(currentInput.toString())
    }

    private fun calculateResult() {
        try {
            val expression = currentInput.toString()
                .replace("×", "*")
                .replace("÷", "/")

            val result = evaluateExpression(expression)
            currentInput.clear()
            currentInput.append(result.toString())
            etDisplay.setText(result.toString())
        } catch (e: Exception) {
            etDisplay.setText("错误")
            currentInput.clear()
        }
    }

    private fun evaluateExpression(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("意外字符: ${expression[pos]}")
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when (ch.toChar()) {
                        '+' -> { nextChar(); x += parseTerm() }
                        '-' -> { nextChar(); x -= parseTerm() }
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when (ch.toChar()) {
                        '*' -> { nextChar(); x *= parseFactor() }
                        '/' -> { nextChar(); x /= parseFactor() }
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (ch.toChar() == '+') nextChar()
                if (ch.toChar() == '-') {
                    nextChar()
                    return -parseFactor()
                }
                var x = 0.0
                val startPos = pos
                while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                x = expression.substring(startPos, pos).toDouble()
                return x
            }
        }.parse()
    }
}