package ru.ifmo.android_2016.calc;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.math.*;

public final class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    View[] DIGITS, OPERATIONS;
    TextView resultText;
    Boolean isDotted = false;
    StringBuilder currentText = new StringBuilder();
    BigDecimal a = new BigDecimal(0);
    BigDecimal b = new BigDecimal(0);
    char op = ' ';
    StringBuilder s = new StringBuilder();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        DIGITS = new View[]{
                findViewById(R.id.d0),
                findViewById(R.id.d1),
                findViewById(R.id.d2),
                findViewById(R.id.d3),
                findViewById(R.id.d4),
                findViewById(R.id.d5),
                findViewById(R.id.d6),
                findViewById(R.id.d7),
                findViewById(R.id.d8),
                findViewById(R.id.d9)
        };
        OPERATIONS = new View[]{
                findViewById(R.id.add),
                findViewById(R.id.sub),
                findViewById(R.id.mul),
                findViewById(R.id.div),
                findViewById(R.id.dot),
                findViewById(R.id.eqv),
                findViewById(R.id.clear)
        };
        resultText = (TextView) findViewById(R.id.result);
        for (int i = 0; i < DIGITS.length; i++) {
            DIGITS[i].setOnClickListener(this);
        }
        for (int i = 0; i < OPERATIONS.length; i++) {
            OPERATIONS[i].setOnClickListener(this);
        }
        if (savedInstanceState != null) {
            a = new BigDecimal(savedInstanceState.getString("A_VALUE"));
            b = new BigDecimal(savedInstanceState.getString("B_VALUE"));
            resultText.setText(savedInstanceState.getString("RESULT_TEXT"));
            currentText = new StringBuilder(savedInstanceState.getString("CURRENT_TEXT"));
            isDotted = savedInstanceState.getBoolean("IS_DOTTED");
        }

    }

    void changeText(char oper) {
        currentText.delete(0, currentText.length());
        if (oper != ' ') {
            isDotted = false;
            currentText.append(a).append(" " + oper + " ");
        } else {
            if (a.toString().contains(".")) {
                isDotted = true;
                currentText.append(a);
            } else {
                isDotted = false;
                currentText.append(a);
            }
        }
        op = oper;
        b = new BigDecimal(0);
    }

    void addDot() {
        currentText.append('.');
        isDotted = true;
    }

    void clearThis() {
        a = new BigDecimal(0);
        b = new BigDecimal(0);
        op = ' ';
        isDotted = false;
    }

    void cutZeroes() {
        while (a.toString().charAt(a.toString().length() - 1) == '0') {
            a = new BigDecimal(a.toString().substring(0,a.toString().length() - 1));
        }
    }

    char[] operations = new char[]{
            '+',
            '-',
            '*',
            '/',
            '.',
            ' '
    };

    @Override
    public void onClick(View v) {
        for (int i = 0; i < DIGITS.length; i++) {
            if (v == DIGITS[i]) {
                currentText.append(Integer.toString(i));
                s.delete(0, s.length());
                if (op == ' ') {
                    if (isDotted && !a.toString().contains(".")) {
                        s.append(a.toString()).append(".").append(i);
                    } else {
                        s.append(a.toString()).append(i);
                    }
                    a = new BigDecimal(s.toString());
                } else {
                    if (isDotted && !b.toString().contains(".")) {
                        s.append(b.toString()).append(".").append(i);
                    } else {
                        s.append(b.toString()).append(i);
                    }
                    b = new BigDecimal(s.toString());
                }
                break;
            }
        }

        for (int i = 0; i < OPERATIONS.length; i++) {
            if (v == OPERATIONS[i] && i == 4) {
                if (!isDotted) {
                    if (op == ' ' && a.equals(BigDecimal.ZERO)) {
                        currentText.delete(0, currentText.length());
                        currentText.append(0);
                    } else if (op != ' ' && b.equals(BigDecimal.ZERO)) {
                        currentText.delete(a.toString().length() + 3, currentText.length());
                        currentText.append(0);
                    }
                    addDot();
                }
                continue;
            }
            if (v == OPERATIONS[i]) {
                if (i == OPERATIONS.length - 1) {
                    currentText.delete(0, currentText.length());
                    clearThis();
                    break;
                } else if (i == OPERATIONS.length - 2) {
                    switch (op) {
                        case '+': {
                            a = a.add(b);
                            changeText(' ');
                            break;
                        }
                        case '-': {
                            a = a.subtract(b);
                            changeText(' ');
                            break;
                        }
                        case '*': {
                            a = a.multiply(b);
                            changeText(' ');
                            break;
                        }
                        case '/': {
                            if (b.equals(new BigDecimal(0))) {
                                clearThis();
                                currentText.delete(0, currentText.length());
                                currentText.append("Ошибка.");
                            } else {
                                a = a.divide(b, 8, BigDecimal.ROUND_HALF_UP);
                                cutZeroes();
                                changeText(' ');
                            }
                        }
                        case ' ': {
                            break;
                        }
                    }
                } else {
                    if (op == ' ') {
                        op = operations[i];
                        changeText(op);
                        break;
                    }
                    op = operations[i];
                    switch (op) {
                        case '+': {
                            a = a.add(b);
                            changeText('+');
                            break;
                        }
                        case '-': {
                            a = a.subtract(b);
                            changeText('-');
                            break;
                        }
                        case '*': {
                            a = a.multiply(b);
                            changeText('*');
                            break;
                        }
                        case '/': {
                            if (b.equals(new BigDecimal(0))) {
                                clearThis();
                                currentText.delete(0, currentText.length());
                                currentText.append("Ошибка.");
                            } else {
                                a = a.divide(b, 8, BigDecimal.ROUND_HALF_UP);
                                cutZeroes();
                                changeText('/');
                            }
                        }
                    }
                }
            }
        }

        resultText.setText(currentText);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("A_VALUE", a.toString());
        outState.putString("B_VALUE", b.toString());
        outState.putString("RESULT_TEXT", resultText.getText().toString());
        outState.putString("CURRENT_TEXT", currentText.toString());
        outState.putBoolean("IS_DOTTED", isDotted);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        a = new BigDecimal(savedInstanceState.getString("A_VALUE"));
        b = new BigDecimal(savedInstanceState.getString("B_VALUE"));
        resultText.setText(savedInstanceState.getString("RESULT_TEXT"));
        currentText = new StringBuilder(savedInstanceState.getString("CURRENT_TEXT"));
        isDotted = savedInstanceState.getBoolean("IS_DOTTED");
    }
}
