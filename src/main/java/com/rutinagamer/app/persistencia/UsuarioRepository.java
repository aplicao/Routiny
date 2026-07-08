package com.rutinagamer.app.persistencia;

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

/**
 * Capa de persistencia con Firebase Firestore.
 * <p>
 * Esta clase vive SOLO en el modulo Android (depende de FirebaseFirestore),
 * a diferencia del paquete rutinagamer.* que es Java puro y testeable con
 * JUnit sin necesidad de un dispositivo/emulador.
 * <p>
 * Estructura en Firestore:
 * <pre>
 * usuarios/{usuarioId}
 *     nombre: String
 *     nombreMascota: String
 *     estadoMascota: String   ("Saludable" | "Cansado" | "Critico")
 *
 *     usuarios/{usuarioId}/alarmas/{alarmaId}
 *         horario: String       ("HH:mm")
 *         tipoDesafio: String   ("MENTAL" | "FISICO")
 *         activa: boolean
 *
 *     usuarios/{usuarioId}/historial/{fecha}   (doc id = "yyyy-MM-dd")
 *         fecha: String
 *         cumplido: boolean
 * </pre>
 * Como todas las llamadas a Firestore son asincronas, cada metodo recibe
 * callbacks (interfaces definidas al final de esta clase) en vez de
 * retornar el valor directamente.
 */
public class UsuarioRepository {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final FirebaseFirestore db;

    public UsuarioRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // ---------------------------------------------------------------
    // Usuario / Mascota
    // ---------------------------------------------------------------

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

    /**
     * Reconstruye un Usuario completo (con sus alarmas e historial) desde
     * Firestore. Se hacen 3 lecturas encadenadas: el documento del usuario,
     * la subcoleccion de alarmas y la subcoleccion de historial.
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
            // Nota: Mascota siempre nace "Saludable"; si en Firestore quedo
            // guardado otro estado, se podria avanzar el estado aqui mismo
            // llamando reaccionarA() las veces necesarias. Para la primera
            // version basta con partir saludable en cada carga.

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
                        } catch (RegistroDuplicadoException e) {
                            onError.onError(e);
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
            // TODO: reemplazar este Supplier placeholder por la lectura real del
            // acelerometro/sensor cuando la Activity conecte el DesafioFisico a la
            // pantalla. El repositorio no tiene acceso al sensor, solo persiste datos.
            desafio = new DesafioFisico(movimientosRequeridos, () -> 0);
        } else {
            desafio = new DesafioMental();
        }
        return new Alarma(horario, desafio);
    }

    // ---------------------------------------------------------------
    // Alarmas
    // ---------------------------------------------------------------

    public void guardarAlarma(String usuarioId, Alarma alarma, OnExito onExito, OnError onError) {
        boolean esFisico = alarma.getDesafio() instanceof DesafioFisico;
        String tipoDesafio = esFisico ? "FISICO" : "MENTAL";

        Map<String, Object> datos = new HashMap<>();
        datos.put("horario", alarma.getHorario().format(FORMATO_HORA));
        datos.put("tipoDesafio", tipoDesafio);
        datos.put("activa", alarma.isActiva());
        // El DesafioFisico no expone movimientosRequeridos como getter publico en
        // la version actual de la clase; si mas adelante se agrega ese getter,
        // reemplazar el valor fijo de abajo por el real. Por ahora se guarda un
        // valor por defecto para no romper la reconstruccion desde Firestore.
        if (esFisico) {
            datos.put("movimientosRequeridos", 10);
        }

        // El id del documento es el horario: es simple y evita colisiones,
        // ya que Usuario ya impide dos alarmas activas a la misma hora.
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

    // ---------------------------------------------------------------
    // Historial de cumplimiento + estado de la mascota
    // ---------------------------------------------------------------

    /**
     * Guarda el registro del dia y, en el mismo paso, actualiza el estado
     * de la mascota reflejando la reaccion (esto replica lo que hace
     * Usuario.registrarCumplimiento en memoria, pero persistiendolo).
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

    // ---------------------------------------------------------------
    // Callbacks (Firestore es 100% asincrono, no se puede "retornar" el dato)
    // ---------------------------------------------------------------

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
