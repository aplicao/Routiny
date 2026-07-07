package rutinagamer.mascota;

public class Mascota {

    private final String nombre;
    private EstadoMascota estado;

    public Mascota(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("nombre no puede estar vacio");
        }
        this.nombre = nombre;
        this.estado = new EstadoSaludable();
    }

    public void reaccionarA(boolean rutinaCumplida) {
        estado = estado.reaccionar(rutinaCumplida);
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstadoActual() {
        return estado.getNombre();
    }
}
