package rutinagamer.mascota;

public class EstadoSaludable implements EstadoMascota {

    @Override
    public EstadoMascota reaccionar(boolean rutinaCumplida) {
        return rutinaCumplida ? this : new EstadoCansado();
    }

    @Override
    public String getNombre() {
        return "Saludable";
    }
}
