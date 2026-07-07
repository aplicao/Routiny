package rutinagamer.modelo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegistroCumplimientoTest {

    @Test
    void constructorRechazaFechaNula() {
        assertThrows(IllegalArgumentException.class, () -> new RegistroCumplimiento(null, true));
    }

    @Test
    void constructorRechazaFechaFutura() {
        LocalDate manana = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () -> new RegistroCumplimiento(manana, true));
    }

    @Test
    void constructorAceptaFechaDeHoy() {
        LocalDate hoy = LocalDate.now();
        RegistroCumplimiento registro = new RegistroCumplimiento(hoy, true);

        assertEquals(hoy, registro.getFecha());
        assertTrue(registro.isCumplido());
    }
}
