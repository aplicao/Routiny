package com.rutinagamer.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalTime;

import rutinagamer.desafio.DesafioMental;
import rutinagamer.excepciones.DesafioExpiradoException;
import rutinagamer.modelo.Alarma;

/*
  Pantalla de prueba: una alarma fija con un desafio mental. El objetivo de
  esta pantalla es probar que la logica de rutinagamer funciona igual
  dentro de Android que en las pruebas JUnit. La persistencia con Firebase
  y el flujo real de la app viven en InicioActivity.
 */
public class MainActivity extends AppCompatActivity {

    private Alarma alarma;

    private TextView textEnunciado;
    private TextView textResultado;
    private EditText inputRespuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarma = new Alarma(LocalTime.now(), new DesafioMental());

        textEnunciado = findViewById(R.id.textEnunciado);
        textResultado = findViewById(R.id.textResultado);
        inputRespuesta = findViewById(R.id.inputRespuesta);

        Button botonSonar = findViewById(R.id.botonSonar);
        Button botonResponder = findViewById(R.id.botonResponder);

        botonSonar.setOnClickListener(v -> sonarAlarma());
        botonResponder.setOnClickListener(v -> responder());
    }

    private void sonarAlarma() {
        alarma.disparar();
        textEnunciado.setText(alarma.getDesafio().getEnunciado());
        textResultado.setText("");
        inputRespuesta.setText("");
    }

    private void responder() {
        String respuesta = inputRespuesta.getText().toString();
        try {
            boolean correcto = alarma.intentarSuperar(respuesta);
            textResultado.setText(correcto ? getString(R.string.correcto) : getString(R.string.incorrecto));
        } catch (DesafioExpiradoException e) {
            textResultado.setText(getString(R.string.tiempo_agotado));
        }
    }
}
