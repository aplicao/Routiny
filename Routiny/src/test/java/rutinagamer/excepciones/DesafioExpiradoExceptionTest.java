package rutinagamer.excepciones;

import org.junit.jupiter.api.Test;
import rutinagamer.desafio.DesafioMental;

import static org.junit.jupiter.api.Assertions.*;

class DesafioExpiradoExceptionTest {

    @Test
    void mensajeDeLaExcepcionEsDescriptivo() {
        DesafioMental desafio = new DesafioMental();
        desafio.generar();
        desafio.marcarExpirado();

        DesafioExpiradoException excepcion = assertThrows(
                DesafioExpiradoException.class,
                () -> desafio.evaluar("1")
        );

        assertTrue(excepcion.getMessage().toLowerCase().contains("expiro"));
    }

    @Test
    void esUnaExcepcionCheckedNoRuntime() {
        assertFalse(RuntimeException.class.isAssignableFrom(DesafioExpiradoException.class));
    }
}
