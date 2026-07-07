package rutinagamer.excepciones;

import org.junit.jupiter.api.Test;
import rutinagamer.desafio.DesafioMental;
import rutinagamer.modelo.Alarma;
import rutinagamer.modelo.Usuario;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AlarmaDuplicadaExceptionTest {

    @Test
    void mensajeIncluyeElHorarioEnConflicto() throws AlarmaDuplicadaException {
        Usuario usuario = new Usuario("Carlos");
        usuario.agregarAlarma(new Alarma(LocalTime.of(7, 0), new DesafioMental()));

        AlarmaDuplicadaException excepcion = assertThrows(AlarmaDuplicadaException.class, () ->
                usuario.agregarAlarma(new Alarma(LocalTime.of(7, 0), new DesafioMental())));

        assertTrue(excepcion.getMessage().contains("07:00"));
    }

    @Test
    void esUnaExcepcionCheckedNoRuntime() {
        assertFalse(RuntimeException.class.isAssignableFrom(AlarmaDuplicadaException.class));
    }
}
