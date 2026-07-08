package rutinagamer.mascota;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
  verifica que el estado cambia correctamente segun el cumplimiento del usuario.
 */
public class MascotaTest {

    private Mascota mascota;

    @Before
    public void setUp() {
        mascota = new Mascota("Rookidee");
    }

    @Test
    public void estadoInicialEsSaludable() {
        assertEquals("Saludable", mascota.getEstadoActual());
    }

    @Test
    public void pasaDeSaludableACansadoSiNoCumple() {
        mascota.reaccionarA(false);
        assertEquals("Cansado", mascota.getEstadoActual());
    }
}
