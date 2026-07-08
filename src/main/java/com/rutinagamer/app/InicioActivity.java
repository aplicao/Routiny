package com.rutinagamer.app;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

import com.rutinagamer.app.persistencia.UsuarioRepository;

import rutinagamer.desafio.DesafioMental;
import rutinagamer.excepciones.AlarmaDuplicadaException;
import rutinagamer.excepciones.RegistroDuplicadoException;
import rutinagamer.modelo.Alarma;
import rutinagamer.modelo.RegistroCumplimiento;
import rutinagamer.modelo.Usuario;

/*
  muestra el estado de la mascota, la lista de alarmas,
  y permite agregar alarmas nuevas y registrar el cumplimiento del dia.
  Por ahora el "usuarioId" es un valor fijo (USUARIO_ID).
 */
public class InicioActivity extends AppCompatActivity {

    private static final String USUARIO_ID = "usuario_prueba";
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private UsuarioRepository repo;
    private Usuario usuario;

    private TextView textNombreMascota;
    private TextView textEstadoMascota;
    private TextView textKaomojiMascota;
    private TextView textMensaje;
    private LinearLayout contenedorAlarmas;
    private EditText inputHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        textNombreMascota = findViewById(R.id.textNombreMascota);
        textEstadoMascota = findViewById(R.id.textEstadoMascota);
        textKaomojiMascota = findViewById(R.id.textKaomojiMascota);
        textMensaje = findViewById(R.id.textMensaje);
        contenedorAlarmas = findViewById(R.id.contenedorAlarmas);
        inputHorario = findViewById(R.id.inputHorario);

        inputHorario.setOnClickListener(v -> mostrarTimePicker());

        Button botonAgregarAlarma = findViewById(R.id.botonAgregarAlarma);
        Button botonCumplido = findViewById(R.id.botonCumplido);
        Button botonNoCumplido = findViewById(R.id.botonNoCumplido);

        botonAgregarAlarma.setOnClickListener(v -> agregarAlarma());
        botonCumplido.setOnClickListener(v -> registrarCumplimiento(true));
        botonNoCumplido.setOnClickListener(v -> registrarCumplimiento(false));

        repo = new UsuarioRepository();
        cargarUsuario();
    }

    /*
      Intenta cargar el usuario desde Firestore. Si no existe todavia crea uno nuevo con datos por
      defecto.
     */
    private void cargarUsuario() {
        repo.cargarUsuario(USUARIO_ID,
                usuarioCargado -> {
                    usuario = usuarioCargado;
                    AlarmScheduler.reprogramarTodas(this, usuario.getAlarmas());
                    actualizarPantalla();
                },
                error -> {
                    usuario = new Usuario("Diego", "Rookidee");
                    repo.crearUsuario(USUARIO_ID, usuario,
                            this::actualizarPantalla,
                            e2 -> mostrarMensaje("Error creando usuario: " + e2.getMessage()));
                });
    }

    private void actualizarPantalla() {
        textNombreMascota.setText(usuario.getMascota().getNombre());
        String estado = usuario.getMascota().getEstadoActual();
        textEstadoMascota.setText("Estado: " + estado);

        switch (estado) {
            case "Saludable":
                textKaomojiMascota.setText("(˶ᵔ ᵕ ᵔ˶)");
                break;
            case "Cansado":
                textKaomojiMascota.setText("( -_ -)");
                break;
            case "Critico":
                textKaomojiMascota.setText("(╥﹏╥)");
                break;
            default:
                textKaomojiMascota.setText("(o_o)");
                break;
        }

        contenedorAlarmas.removeAllViews();
        for (Alarma alarma : usuario.getAlarmasActivas()) {
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, 8, 0, 8);

            TextView texto = new TextView(this);
            texto.setText(alarma.getHorario().format(FORMATO_HORA));
            texto.setTextSize(16);
            LinearLayout.LayoutParams paramsTexto = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            texto.setLayoutParams(paramsTexto);

            TextView botonEliminar = new TextView(this);
            botonEliminar.setText("Eliminar");
            botonEliminar.setTextColor(0xFFCC0000);
            botonEliminar.setPadding(24, 0, 0, 0);
            botonEliminar.setOnClickListener(v -> eliminarAlarma(alarma));

            fila.addView(texto);
            fila.addView(botonEliminar);
            contenedorAlarmas.addView(fila);
        }
    }

    private void eliminarAlarma(Alarma alarma) {
        usuario.eliminarAlarma(alarma);
        AlarmScheduler.cancelarAlarma(this, alarma);
        repo.eliminarAlarma(USUARIO_ID, alarma,
                () -> {
                    mostrarMensaje("Alarma eliminada");
                    actualizarPantalla();
                },
                error -> mostrarMensaje("Error eliminando alarma: " + error.getMessage()));
    }

    private void agregarAlarma() {
        String texto = inputHorario.getText().toString().trim();
        LocalTime horario;
        try {
            horario = LocalTime.parse(texto, FORMATO_HORA);
        } catch (DateTimeParseException e) {
            mostrarMensaje("Formato invalido, usa HH:mm (ejemplo: 08:00)");
            return;
        }

        Alarma nueva = new Alarma(horario, new DesafioMental());
        try {
            usuario.agregarAlarma(nueva);
            AlarmScheduler.programarAlarma(this, nueva);
        } catch (AlarmaDuplicadaException e) {
            mostrarMensaje(e.getMessage());
            return;
        }

        repo.guardarAlarma(USUARIO_ID, nueva,
                () -> {
                    mostrarMensaje("Alarma agregada");
                    inputHorario.setText("");
                    actualizarPantalla();
                },
                error -> mostrarMensaje("Error guardando alarma: " + error.getMessage()));
    }

    private void registrarCumplimiento(boolean cumplido) {
        RegistroCumplimiento registro = new RegistroCumplimiento(LocalDate.now(), cumplido);
        try {
            usuario.registrarCumplimiento(registro);
        } catch (RegistroDuplicadoException e) {
            mostrarMensaje("Ya registraste el cumplimiento de hoy");
            return;
        }

        repo.registrarCumplimiento(USUARIO_ID, usuario.getMascota(), registro,
                () -> {
                    mostrarMensaje("Registro guardado. Mascota ahora: " + usuario.getMascota().getEstadoActual());
                    actualizarPantalla();
                },
                error -> mostrarMensaje("Error guardando registro: " + error.getMessage()));
    }

    private void mostrarTimePicker() {
        Calendar c = Calendar.getInstance();
        int horaActual = c.get(Calendar.HOUR_OF_DAY);
        int minutoActual = c.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    inputHorario.setText(timeFormatted);
                }, horaActual, minutoActual, true); // true = 24h format

        timePicker.show();
    }

    private void mostrarMensaje(String texto) {
        textMensaje.setText(texto);
    }
}
