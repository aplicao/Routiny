package rutinagamer.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rutinagamer.desafio.DesafioMental;
import rutinagamer.excepciones.AlarmaDuplicadaException;
import rutinagamer.excepciones.RegistroDuplicadoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Carlos");
    }

    @Test
    void constructorRechazaNombreEnBlanco() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario("   "));
    }

    @Test
    void agregarAlarmaSinConflictoFunciona() throws AlarmaDuplicadaException {
        usuario.agregarAlarma(new Alarma(LocalTime.of(7, 0), new DesafioMental()));

        assertEquals(1, usuario.getAlarmas().size());
    }

    @Test
    void agregarAlarmaEnHorarioYaOcupadoLanzaExcepcion() throws AlarmaDuplicadaException {
        usuario.agregarAlarma(new Alarma(LocalTime.of(7, 0), new DesafioMental()));

        assertThrows(AlarmaDuplicadaException.class, () ->
                usuario.agregarAlarma(new Alarma(LocalTime.of(7, 0), new DesafioMental())));
    }

    @Test
    void agregarAlarmaEnHorarioDeUnaAlarmaInactivaNoLanzaExcepcion() throws AlarmaDuplicadaException {
        Alarma inactiva = new Alarma(LocalTime.of(7, 0), new DesafioMental());
        inactiva.desactivar();
        usuario.agregarAlarma(inactiva);

        assertDoesNotThrow(() ->
                usuario.agregarAlarma(new Alarma(LocalTime.of(7, 0), new DesafioMental())));
    }

    @Test
    void getAlarmasActivasFiltraSoloLasActivas() throws AlarmaDuplicadaException {
        Alarma activa = new Alarma(LocalTime.of(7, 0), new DesafioMental());
        Alarma inactiva = new Alarma(LocalTime.of(8, 0), new DesafioMental());
        inactiva.desactivar();

        usuario.agregarAlarma(activa);
        usuario.agregarAlarma(inactiva);

        List<Alarma> activas = usuario.getAlarmasActivas();

        assertEquals(1, activas.size());
        assertTrue(activas.contains(activa));
    }

    @Test
    void eliminarAlarmaLaQuitaDeLaLista() throws AlarmaDuplicadaException {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioMental());
        usuario.agregarAlarma(alarma);

        boolean eliminada = usuario.eliminarAlarma(alarma);

        assertTrue(eliminada);
        assertTrue(usuario.getAlarmas().isEmpty());
    }

    @Test
    void eliminarAlarmaQueNoExisteDevuelveFalse() {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioMental());
        assertFalse(usuario.eliminarAlarma(alarma));
    }

    @Test
    void registrarCumplimientoSinConflictoFunciona() throws RegistroDuplicadoException {
        usuario.registrarCumplimiento(new RegistroCumplimiento(LocalDate.now(), true));

        assertEquals(1, usuario.getHistorial().size());
    }

    @Test
    void registrarCumplimientoEnFechaYaRegistradaLanzaExcepcion() throws RegistroDuplicadoException {
        LocalDate hoy = LocalDate.now();
        usuario.registrarCumplimiento(new RegistroCumplimiento(hoy, true));

        assertThrows(RegistroDuplicadoException.class, () ->
                usuario.registrarCumplimiento(new RegistroCumplimiento(hoy, false)));
    }

    @Test
    void getHistorialDevuelveUnaCopiaNoLaListaInterna() throws RegistroDuplicadoException {
        usuario.registrarCumplimiento(new RegistroCumplimiento(LocalDate.now(), true));

        List<RegistroCumplimiento> historial = usuario.getHistorial();

        assertThrows(UnsupportedOperationException.class, () ->
                historial.add(new RegistroCumplimiento(LocalDate.now().minusDays(1), false)));
    }

    @Test
    void unUsuarioNuevoTieneUnaMascotaSaludable() {
        assertEquals("Saludable", usuario.getMascota().getEstadoActual());
    }

    @Test
    void registrarCumplimientoActualizaElEstadoDeLaMascota() throws RegistroDuplicadoException {
        usuario.registrarCumplimiento(new RegistroCumplimiento(LocalDate.now(), false));

        assertEquals("Cansado", usuario.getMascota().getEstadoActual());
    }

    @Test
    void constructorConNombreDeMascotaPersonalizadoFunciona() {
        Usuario u = new Usuario("Carlos", "Firulais");
        assertEquals("Firulais", u.getMascota().getNombre());
    }
}
