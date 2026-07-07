package rutinagamer.mascota;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MascotaTest {

    @Test
    void constructorRechazaNombreEnBlanco() {
        assertThrows(IllegalArgumentException.class, () -> new Mascota("  "));
    }

    @Test
    void unaMascotaNuevaEmpiezaSaludable() {
        Mascota mascota = new Mascota("Rex");
        assertEquals("Saludable", mascota.getEstadoActual());
    }

    @Test
    void variosDiasSeguidosSinCumplirEmpeoranElEstadoHastaElPiso() {
        Mascota mascota = new Mascota("Rex");

        mascota.reaccionarA(false);
        assertEquals("Cansado", mascota.getEstadoActual());

        mascota.reaccionarA(false);
        assertEquals("Critico", mascota.getEstadoActual());

        mascota.reaccionarA(false);
        assertEquals("Critico", mascota.getEstadoActual()); // no empeora mas alla de Critico
    }

    @Test
    void cumplirLaRutinaMejoraElEstadoGradualmente() {
        Mascota mascota = new Mascota("Rex");
        mascota.reaccionarA(false);
        mascota.reaccionarA(false); // ahora esta Critico

        mascota.reaccionarA(true);
        assertEquals("Cansado", mascota.getEstadoActual());

        mascota.reaccionarA(true);
        assertEquals("Saludable", mascota.getEstadoActual());
    }
}
