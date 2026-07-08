package rutinagamer.mascota;


public interface EstadoMascota {

    EstadoMascota reaccionar(boolean rutinaCumplida);

    String getNombre();
}
