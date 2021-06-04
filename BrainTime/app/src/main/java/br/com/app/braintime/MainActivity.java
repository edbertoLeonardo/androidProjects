package br.com.app.braintime;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

   Button buttonInicio;
   TextView contaTextView, resultadoTextView, pontosTextView, textViewTimer;
   ArrayList<Integer> respostas = new ArrayList<>();
   int respostaCorreta;
   int pontos = 0;
   int numeroDePerguntas = 0;
   Button button0, button1, button2, button3, buttonJogarDeNovo;


   public void jogarDeNovo(View view){

       pontos = 0;
       numeroDePerguntas = 0;
       textViewTimer.setText("30s");
       pontosTextView.setText("0/0");
       resultadoTextView.setText("");
       buttonJogarDeNovo.setVisibility(View.INVISIBLE);

       geradorDePerguntas();


       new CountDownTimer(30100, 1000){

           @Override
           public void onTick(long millisUntilFinished) {

               textViewTimer.setText(String.valueOf(millisUntilFinished / 1000) + "s");
           }

           @Override
           public void onFinish() {

               buttonJogarDeNovo.setVisibility(View.VISIBLE);
               textViewTimer.setText("0s");
               resultadoTextView.setText("Seus pontos: " + Integer.toString(pontos) + "/" + Integer.toString(numeroDePerguntas));
           }
       }.start();


   }

    public void geradorDePerguntas(){

        Random randon = new Random();

        int a = randon.nextInt(21);
        int b = randon.nextInt(21);

        contaTextView.setText(Integer.toString(a) + " + " + Integer.toString(b));
        respostaCorreta = randon.nextInt(4);

        respostas.clear();

        int respostaErrada;

        for (int i = 0; i < 4; i++){
            if (i == respostaCorreta){
                respostas.add(a + b);

            }else {
                respostaErrada = randon.nextInt(41);
                while (respostaErrada == a + b){

                    respostaErrada = randon.nextInt(41);
                }

                respostas.add(respostaErrada);
            }
        }

        button0.setText(Integer.toString(respostas.get(0)));
        button1.setText(Integer.toString(respostas.get(1)));
        button2.setText(Integer.toString(respostas.get(2)));
        button3.setText(Integer.toString(respostas.get(3)));

    }

    public void escolhaResposta(View view){

        if (view.getTag().toString().equals(Integer.toString(respostaCorreta))){

            pontos++;
            resultadoTextView.setText("Correto!!!");
        }else {
            resultadoTextView.setText("Errado!!!");
        }

        numeroDePerguntas++;
        pontosTextView.setText(Integer.toString(pontos) + "/" + Integer.toString(numeroDePerguntas));
        geradorDePerguntas();

    }

    public void inicio(View view){

        buttonInicio.setVisibility(View.INVISIBLE);
        jogarDeNovo(findViewById(R.id.buttonJogarDeNovo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonInicio = findViewById(R.id.buttonInicio);
        contaTextView = findViewById(R.id.contaTextView);
        resultadoTextView = findViewById(R.id.resultadoTextView);
        pontosTextView= findViewById(R.id.pointTextView);
        textViewTimer = findViewById(R.id.textViewTimer);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        buttonJogarDeNovo = findViewById(R.id.buttonJogarDeNovo);


        jogarDeNovo(findViewById(R.id.buttonJogarDeNovo));

    }


}