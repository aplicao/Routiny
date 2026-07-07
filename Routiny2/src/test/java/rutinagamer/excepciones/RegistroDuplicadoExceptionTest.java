package rutinagamer.excepciones;

import org.junit.jupiter.api.Test;
import rutinagamer.modelo.RegistroCumplimiento;
import rutinagamer.modelo.Usuario;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegistroDuplicadoExceptionTest {

    @Test
    void mensajeIncluyeLaFechaEnConflicto() throws RegistroDuplicadoException {
        Usuario usuario = new Usuario("Carlos");
        LocalDate hoy = LocalDate.now();
        usuario.registrarCumplimiento(new RegistroCumplimiento(hoy, true));

        RegistroDuplicadoException excepcion = assertThrows(RegistroDuplicadoException.class, () ->
                usuario.registrarCumplimiento(new RegistroCumplimiento(hoy, false)));

        assertTrue(excepcion.getMessage().contains(hoy.toString()));
    }

    @Test
    void esUnaExcepcionUncheckedNoChecked() {
        assertTrue(RuntimeException.class.isAssignableFrom(RegistroDuplicadoException.class));
    }
}
