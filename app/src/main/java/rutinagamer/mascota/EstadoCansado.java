package rutinagamer.mascota;

public class EstadoCansado implements EstadoMascota {

    @Override
    public EstadoMascota reaccionar(boolean rutinaCumplida) {
        return rutinaCumplida ? new EstadoSaludable() : new EstadoCritico();
    }

    @Override
    public String getNombre() {
        return "Cansado";
    }
}
