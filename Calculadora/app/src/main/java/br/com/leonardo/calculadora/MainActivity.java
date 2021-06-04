package br.com.leonardo.calculadora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText result, newNunber;
    private TextView displayOperation;

    //Variáveis para as operações
    private Double operandOne = null;
    private String pendingOperation = "=";

    private static final String STATE_PENDING_OPERATION = "PendingOperation";
    private static final String STATE_OPERATION_ONE = "OperationOne";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        newNunber = findViewById(R.id.newNumber);
        displayOperation = findViewById(R.id.operation);

        Button buttonZero = findViewById(R.id.buttonZero);
        Button buttonUm = findViewById(R.id.buttonUM);
        Button buttonDois = findViewById(R.id.buttonDois);
        Button buttonTres = findViewById(R.id.buttonTres);
        Button buttonQuatro = findViewById(R.id.buttonQuatro);
        Button buttonCinco = findViewById(R.id.buttonCinco);
        Button buttonSeis = findViewById(R.id.buttonSeis);
        Button buttonSete = findViewById(R.id.buttonSete);
        Button buttonOito = findViewById(R.id.buttonOito);
        Button buttonNove = findViewById(R.id.buttonNome);
        Button buttonPonto = findViewById(R.id.buttonPonto);

        Button buttonIgual = findViewById(R.id.buttonEquals);
        Button buttonDiv = findViewById(R.id.buttonDiv);
        Button buttonSomar = findViewById(R.id.buttonSomar);
        Button buttonSubtrair = findViewById(R.id.buttonSubtrair);
        Button buttonMult = findViewById(R.id.buttonMult);

        View.OnClickListener listener = view -> {
            Button button = (Button) view;
            newNunber.append(button.getText().toString());
        };

        buttonZero.setOnClickListener(listener);
        buttonUm.setOnClickListener(listener);
        buttonDois.setOnClickListener(listener);
        buttonTres.setOnClickListener(listener);
        buttonQuatro.setOnClickListener(listener);
        buttonCinco.setOnClickListener(listener);
        buttonSeis.setOnClickListener(listener);
        buttonSete.setOnClickListener(listener);
        buttonOito.setOnClickListener(listener);
        buttonNove.setOnClickListener(listener);
        buttonPonto.setOnClickListener(listener);

        Button buttonNegative = findViewById(R.id.buttonNegative);
        buttonNegative.setOnClickListener(v -> {
            String value = newNunber.getText().toString();
            if (value.length() == 0){
                newNunber.setText("-");
            }else {
                try {
                    Double doubleValue = Double.valueOf(value);
                    doubleValue *= 1;
                    newNunber.setText(doubleValue.toString());
                }catch (NumberFormatException e){
                    //newNumber era "-" ou "."
                    newNunber.setText("");
                }
            }
        });

        View.OnClickListener opListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String op = button.getText().toString();
                String value = newNunber.getText().toString();

                try {

                    Double doubleValue = Double.valueOf(value);
                    performOperation(doubleValue, op);

                }catch (NumberFormatException e){

                    newNunber.setText("");
                }

                pendingOperation = op;
                displayOperation.setText(pendingOperation);
            }
        };

        buttonIgual.setOnClickListener(opListener);
        buttonSomar.setOnClickListener(opListener);
        buttonDiv.setOnClickListener(opListener);
        buttonMult.setOnClickListener(opListener);
        buttonSubtrair.setOnClickListener(opListener);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_PENDING_OPERATION, pendingOperation);
        if (operandOne != null){
            outState.putDouble(STATE_OPERATION_ONE, operandOne);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION);
        operandOne = savedInstanceState.getDouble(STATE_OPERATION_ONE);
        displayOperation.setText(pendingOperation);
    }

    private void performOperation(Double value, String operation){

        displayOperation.setText(operation);

        if (operandOne == null){
            operandOne = value;
        }else {
            if (pendingOperation.equals("=")){
                pendingOperation = operation;
            }
            switch (pendingOperation){
                case "=" :
                    operandOne = value;
                    break;
                case "/" :
                    if (value == 0){
                        operandOne = 0.0;
                    }else {
                        operandOne /= value;
                    }
                    break;
                case "*" :
                    operandOne *= value;
                    break;
                case "-" :
                    operandOne -= value;
                    break;
                case "+" :
                    operandOne += value;
                    break;
            }
        }

        result.setText(operandOne.toString());
        newNunber.setText("");
    }

}