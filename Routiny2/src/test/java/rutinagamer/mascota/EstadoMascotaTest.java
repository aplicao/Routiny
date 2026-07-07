package rutinagamer.mascota;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoMascotaTest {

    @Test
    void saludableSigueSaludableSiCumplioLaRutina() {
        EstadoMascota estado = new EstadoSaludable();
        assertEquals("Saludable", estado.reaccionar(true).getNombre());
    }

    @Test
    void saludablePasaACansadoSiNoCumplioLaRutina() {
        EstadoMascota estado = new EstadoSaludable();
        assertEquals("Cansado", estado.reaccionar(false).getNombre());
    }

    @Test
    void cansadoVuelveASaludableSiCumplioLaRutina() {
        EstadoMascota estado = new EstadoCansado();
        assertEquals("Saludable", estado.reaccionar(true).getNombre());
    }

    @Test
    void cansadoPasaACriticoSiNoCumplioLaRutina() {
        EstadoMascota estado = new EstadoCansado();
        assertEquals("Critico", estado.reaccionar(false).getNombre());
    }

    @Test
    void criticoVuelveACansadoSiCumplioLaRutina() {
        EstadoMascota estado = new EstadoCritico();
        assertEquals("Cansado", estado.reaccionar(true).getNombre());
    }

    @Test
    void criticoSigueCriticoSiNoCumplioLaRutina() {
        EstadoMascota estado = new EstadoCritico();
        assertEquals("Critico", estado.reaccionar(false).getNombre());
    }
}
