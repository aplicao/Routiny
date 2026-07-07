package rutinagamer.estadisticas;

import org.junit.jupiter.api.Test;
import rutinagamer.modelo.RegistroCumplimiento;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EstadisticasServiceTest {

    private final EstadisticasService estadisticas = new EstadisticasService();
    private final LocalDate hoy = LocalDate.now();

    @Test
    void porcentajeCumplimientoConHistorialVacioEsCero() {
        assertEquals(0.0, estadisticas.calcularPorcentajeCumplimiento(List.of()));
    }

    @Test
    void porcentajeCumplimientoSeCalculaCorrectamente() {
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(3), true),
                new RegistroCumplimiento(hoy.minusDays(2), true),
                new RegistroCumplimiento(hoy.minusDays(1), false),
                new RegistroCumplimiento(hoy, true)
        );
        assertEquals(75.0, estadisticas.calcularPorcentajeCumplimiento(historial));
    }

    @Test
    void rachaActualCuentaDesdeElDiaMasRecienteHaciaAtras() {
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(3), false),
                new RegistroCumplimiento(hoy.minusDays(2), true),
                new RegistroCumplimiento(hoy.minusDays(1), true),
                new RegistroCumplimiento(hoy, true)
        );
        assertEquals(3, estadisticas.calcularRachaActual(historial));
    }

    @Test
    void rachaActualEsCeroSiHoyNoSeCumplio() {
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(1), true),
                new RegistroCumplimiento(hoy, false)
        );
        assertEquals(0, estadisticas.calcularRachaActual(historial));
    }

    @Test
    void rachaMasLargaEncuentraElMejorTramoAunqueNoSeaElActual() {
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(5), true),
                new RegistroCumplimiento(hoy.minusDays(4), true),
                new RegistroCumplimiento(hoy.minusDays(3), true),
                new RegistroCumplimiento(hoy.minusDays(2), false),
                new RegistroCumplimiento(hoy.minusDays(1), true),
                new RegistroCumplimiento(hoy, false)
        );
        assertEquals(3, estadisticas.calcularRachaMasLarga(historial));
    }

    @Test
    void agruparPorDiaDeSemanaJuntaFechasDelMismoDia() {
        List<RegistroCumplimiento> historial = List.of(
                new RegistroCumplimiento(hoy.minusDays(7), true),  // mismo dia de semana que hoy
                new RegistroCumplimiento(hoy.minusDays(14), false), // idem
                new RegistroCumplimiento(hoy.minusDays(1), true)   // un dia distinto
        );

        Map<DayOfWeek, List<RegistroCumplimiento>> agrupado = estadisticas.agruparPorDiaDeSemana(historial);

        assertEquals(2, agrupado.get(hoy.getDayOfWeek()).size());
    }
}
