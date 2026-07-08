package rutinagamer.modelo;

import java.time.LocalDate;

public class RegistroCumplimiento {

    private final LocalDate fecha;
    private final boolean cumplido;

    public RegistroCumplimiento(LocalDate fecha, boolean cumplido) {
        if (fecha == null) {
            throw new IllegalArgumentException("fecha no puede ser nula");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("no se puede registrar una fecha futura: " + fecha);
        }
        this.fecha = fecha;
        this.cumplido = cumplido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public boolean isCumplido() {
        return cumplido;
    }
}
