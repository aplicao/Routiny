package rutinagamer.estadisticas;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import rutinagamer.modelo.RegistroCumplimiento;

import static org.junit.Assert.assertEquals;

public class EstadisticasServiceTest {

    private EstadisticasService service;

    @Before
    public void setUp() {
        service = new EstadisticasService();
    }

    @Test
    public void porcentajeCumplimientoConDatosMixtos() {
        LocalDate hoy = LocalDate.now();
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(3), true),
                new RegistroCumplimiento(hoy.minusDays(2), false),
                new RegistroCumplimiento(hoy.minusDays(1), true),
                new RegistroCumplimiento(hoy, true)
        );

        // 3 de 4 dias cumplidos = 75%
        assertEquals(75.0, service.calcularPorcentajeCumplimiento(historial), 0.001);
    }

    @Test
    public void rachaActualCuentaSoloLosDiasSeguidosMasRecientes() {
        LocalDate hoy = LocalDate.now();
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(3), false),
                new RegistroCumplimiento(hoy.minusDays(2), true),
                new RegistroCumplimiento(hoy.minusDays(1), true),
                new RegistroCumplimiento(hoy, true)
        );

        // los ultimos 3 dias cumplidos, la racha se corta en el dia -3 (no cumplido)
        assertEquals(3, service.calcularRachaActual(historial));
    }

    @Test
    public void rachaMasLargaEncuentraElMejorTramoAunqueNoSeaElUltimo() {
        LocalDate hoy = LocalDate.now();
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(4), true),
                new RegistroCumplimiento(hoy.minusDays(3), true),
                new RegistroCumplimiento(hoy.minusDays(2), true),
                new RegistroCumplimiento(hoy.minusDays(1), false),
                new RegistroCumplimiento(hoy, true)
        );

        // la racha mas larga es la de 3 dias del principio, no la de 1 dia del final
        assertEquals(3, service.calcularRachaMasLarga(historial));
    }

    @Test
    public void rachaSeRompeSiFaltanDias() {
        LocalDate hoy = LocalDate.now();
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(5), true),
                new RegistroCumplimiento(hoy.minusDays(4), true),
                // falta dia -3
                new RegistroCumplimiento(hoy.minusDays(2), true),
                new RegistroCumplimiento(hoy.minusDays(1), true),
                new RegistroCumplimiento(hoy, true)
        );

        // La racha actual es 3 (hoy, ayer, anteayer), no 5
        assertEquals(3, service.calcularRachaActual(historial));
        // La racha mas larga es 3
        assertEquals(3, service.calcularRachaMasLarga(historial));
    }
}
