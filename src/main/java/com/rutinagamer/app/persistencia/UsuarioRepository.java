package com.rutinagamer.app.persistencia;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rutinagamer.desafio.Desafio;
import rutinagamer.desafio.DesafioFisico;
import rutinagamer.desafio.DesafioMental;
import rutinagamer.excepciones.AlarmaDuplicadaException;
import rutinagamer.excepciones.RegistroDuplicadoException;
import rutinagamer.mascota.Mascota;
import rutinagamer.modelo.Alarma;
import rutinagamer.modelo.RegistroCumplimiento;
import rutinagamer.modelo.Usuario;

/*
  persistencia con Firebase Firestore.
 */
public class UsuarioRepository {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final FirebaseFirestore db;

    public UsuarioRepository() {
        this.db = FirebaseFirestore.getInstance();
    }
    public void crearUsuario(String usuarioId, Usuario usuario,
                              OnExito onExito, OnError onError) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", usuario.getNombre());
        datos.put("nombreMascota", usuario.getMascota().getNombre());
        datos.put("estadoMascota", usuario.getMascota().getEstadoActual());

        db.collection("usuarios").document(usuarioId)
                .set(datos)
                .addOnSuccessListener(unused -> onExito.onExito())
                .addOnFailureListener(onError::onError);
    }

    /*
      Reconstruye un Usuario completo (con sus alarmas e historial) desde Firestore.
     */
    public void cargarUsuario(String usuarioId, OnUsuarioCargado callback, OnError onError) {
        DocumentReference docUsuario = db.collection("usuarios").document(usuarioId);

        docUsuario.get().addOnSuccessListener(snapshotUsuario -> {
            if (!snapshotUsuario.exists()) {
                onError.onError(new IllegalStateException("Usuario no encontrado: " + usuarioId));
                return;
            }

            String nombre = snapshotUsuario.getString("nombre");
            String nombreMascota = snapshotUsuario.getString("nombreMascota");
            Usuario usuario = new Usuario(nombre, nombreMascota);

            cargarAlarmas(usuarioId, usuario, () ->
                    cargarHistorial(usuarioId, usuario, () ->
                            callback.onCargado(usuario), onError), onError);
        }).addOnFailureListener(onError::onError);
    }

    private void cargarAlarmas(String usuarioId, Usuario usuario, Runnable siguiente, OnError onError) {
        db.collection("usuarios").document(usuarioId).collection("alarmas")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        Alarma alarma = mapearAlarma(doc);
                        try {
                            usuario.agregarAlarma(alarma);
                        } catch (AlarmaDuplicadaException e) {
                            // No deberia pasar viniendo de Firestore, pero si pasa
                            // preferimos avisar que romper la carga completa.
                            onError.onError(e);
                        }
                    }
                    siguiente.run();
                })
                .addOnFailureListener(onError::onError);
    }

    private void cargarHistorial(String usuarioId, Usuario usuario, Runnable siguiente, OnError onError) {
        db.collection("usuarios").document(usuarioId).collection("historial")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        LocalDate fecha = LocalDate.parse(doc.getId());
                        boolean cumplido = Boolean.TRUE.equals(doc.getBoolean("cumplido"));
                        try {
                            usuario.registrarCumplimiento(new RegistroCumplimiento(fecha, cumplido));
                        } catch (RegistroDuplicadoException | IllegalArgumentException e) {
                            Log.w("UsuarioRepository", "Saltando registro invalido o duplicado: " + fecha, e);
                        }
                    }
                    siguiente.run();
                })
                .addOnFailureListener(onError::onError);
    }

    private Alarma mapearAlarma(DocumentSnapshot doc) {
        LocalTime horario = LocalTime.parse(doc.getString("horario"), FORMATO_HORA);
        String tipoDesafio = doc.getString("tipoDesafio");

        Desafio desafio;
        if ("FISICO".equals(tipoDesafio)) {
            Long movimientos = doc.getLong("movimientosRequeridos");
            int movimientosRequeridos = movimientos != null ? movimientos.intValue() : 10;

            desafio = new DesafioFisico(movimientosRequeridos, () -> 0);
        } else {
            desafio = new DesafioMental();
        }
        return new Alarma(horario, desafio);
    }

    public void guardarAlarma(String usuarioId, Alarma alarma, OnExito onExito, OnError onError) {
        boolean esFisico = alarma.getDesafio() instanceof DesafioFisico;
        String tipoDesafio = esFisico ? "FISICO" : "MENTAL";

        Map<String, Object> datos = new HashMap<>();
        datos.put("horario", alarma.getHorario().format(FORMATO_HORA));
        datos.put("tipoDesafio", tipoDesafio);
        datos.put("activa", alarma.isActiva());

        if (esFisico) {
            datos.put("movimientosRequeridos", 10);
        }

        String alarmaId = alarma.getHorario().format(FORMATO_HORA);

        db.collection("usuarios").document(usuarioId)
                .collection("alarmas").document(alarmaId)
                .set(datos)
                .addOnSuccessListener(unused -> onExito.onExito())
                .addOnFailureListener(onError::onError);
    }

    public void eliminarAlarma(String usuarioId, Alarma alarma, OnExito onExito, OnError onError) {
        String alarmaId = alarma.getHorario().format(FORMATO_HORA);
        db.collection("usuarios").document(usuarioId)
                .collection("alarmas").document(alarmaId)
                .delete()
                .addOnSuccessListener(unused -> onExito.onExito())
                .addOnFailureListener(onError::onError);
    }

    /*
      Guarda el registro del dia y, en el mismo paso, actualiza el estado
      de la mascota.
     */
    public void registrarCumplimiento(String usuarioId, Mascota mascota,
                                       RegistroCumplimiento registro,
                                       OnExito onExito, OnError onError) {
        String docId = registro.getFecha().toString(); // yyyy-MM-dd

        Map<String, Object> datosHistorial = new HashMap<>();
        datosHistorial.put("fecha", docId);
        datosHistorial.put("cumplido", registro.isCumplido());

        DocumentReference docHistorial = db.collection("usuarios").document(usuarioId)
                .collection("historial").document(docId);

        DocumentReference docUsuario = db.collection("usuarios").document(usuarioId);

        db.runBatch(batch -> {
                    batch.set(docHistorial, datosHistorial);
                    batch.update(docUsuario, "estadoMascota", mascota.getEstadoActual());
                })
                .addOnSuccessListener(unused -> onExito.onExito())
                .addOnFailureListener(onError::onError);
    }

    public interface OnExito {
        void onExito();
    }

    public interface OnError {
        void onError(Exception e);
    }

    public interface OnUsuarioCargado {
        void onCargado(Usuario usuario);
    }
}
