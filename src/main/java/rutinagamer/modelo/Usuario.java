package rutinagamer.modelo;

import rutinagamer.excepciones.AlarmaDuplicadaException;
import rutinagamer.excepciones.RegistroDuplicadoException;
import rutinagamer.mascota.Mascota;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private final String nombre;
    private final List<Alarma> alarmas;
    private final List<RegistroCumplimiento> historial;
    private final Mascota mascota;

    public Usuario(String nombre) {
        this(nombre, "Compañero");
    }

    public Usuario(String nombre, String nombreMascota) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("nombre no puede estar vacio");
        }
        this.nombre = nombre;
        this.alarmas = new ArrayList<>();
        this.historial = new ArrayList<>();
        this.mascota = new Mascota(nombreMascota);
    }

    public void agregarAlarma(Alarma nueva) throws AlarmaDuplicadaException {
        boolean yaExisteEnEseHorario = alarmas.stream()
                .anyMatch(a -> a.isActiva() && a.getHorario().equals(nueva.getHorario()));

        if (yaExisteEnEseHorario) {
            throw new AlarmaDuplicadaException(
                    "Ya existe una alarma activa a las " + nueva.getHorario());
        }
        alarmas.add(nueva);
    }

    public boolean eliminarAlarma(Alarma alarma) {
        return alarmas.remove(alarma);
    }

    public List<Alarma> getAlarmasActivas() {
        return alarmas.stream()
                .filter(Alarma::isActiva)
                .toList();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Alarma> getAlarmas() {
        return List.copyOf(alarmas);
    }


    public void registrarCumplimiento(RegistroCumplimiento registro) throws RegistroDuplicadoException {
        boolean yaExisteEnEsaFecha = historial.stream()
                .anyMatch(r -> r.getFecha().equals(registro.getFecha()));

        if (yaExisteEnEsaFecha) {
            throw new RegistroDuplicadoException(
                    "Ya existe un registro de cumplimiento para " + registro.getFecha());
        }
        historial.add(registro);
        mascota.reaccionarA(registro.isCumplido());
    }

    public List<RegistroCumplimiento> getHistorial() {
        return List.copyOf(historial);
    }

    public Mascota getMascota() {
        return mascota;
    }
}
