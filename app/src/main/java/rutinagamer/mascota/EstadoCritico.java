package rutinagamer.mascota;

public class EstadoCritico implements EstadoMascota {

    @Override
    public EstadoMascota reaccionar(boolean rutinaCumplida) {
        return rutinaCumplida ? new EstadoCansado() : this;
    }

    @Override
    public String getNombre() {
        return "Critico";
    }
}
