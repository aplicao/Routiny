package rutinagamer.modelo;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import rutinagamer.desafio.DesafioMental;
import rutinagamer.excepciones.AlarmaDuplicadaException;
import rutinagamer.excepciones.RegistroDuplicadoException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UsuarioTest {

    private Usuario usuario;

    @Before
    public void setUp() {
        usuario = new Usuario("Diego", "Rookidee");
    }

    @Test
    public void agregarAlarmaLaDejaComoActiva() throws AlarmaDuplicadaException {
        Alarma alarma = new Alarma(LocalTime.of(8, 0), new DesafioMental());
        usuario.agregarAlarma(alarma);

        assertEquals(1, usuario.getAlarmasActivas().size());
    }

    @Test
    public void noPermiteDosAlarmasActivasEnElMismoHorario() throws AlarmaDuplicadaException {
        usuario.agregarAlarma(new Alarma(LocalTime.of(8, 0), new DesafioMental()));

        assertThrows(AlarmaDuplicadaException.class,
                () -> usuario.agregarAlarma(new Alarma(LocalTime.of(8, 0), new DesafioMental())));
    }

    @Test
    public void noPermiteDosRegistrosParaLaMismaFecha() {
        LocalDate hoy = LocalDate.now();
        usuario.registrarCumplimiento(new RegistroCumplimiento(hoy, true));

        assertThrows(RegistroDuplicadoException.class,
                () -> usuario.registrarCumplimiento(new RegistroCumplimiento(hoy, false)));
    }
}
