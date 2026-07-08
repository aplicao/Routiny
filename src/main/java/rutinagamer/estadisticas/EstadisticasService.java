package rutinagamer.estadisticas;

import rutinagamer.modelo.RegistroCumplimiento;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EstadisticasService {

    public double calcularPorcentajeCumplimiento(List<RegistroCumplimiento> historial) {
        if (historial.isEmpty()) {
            return 0.0;
        }
        long cumplidos = historial.stream()
                .filter(RegistroCumplimiento::isCumplido)
                .count();
        return (cumplidos * 100.0) / historial.size();
    }

    public int calcularRachaActual(List<RegistroCumplimiento> historial) {
        if (historial.isEmpty()) return 0;

        List<RegistroCumplimiento> ordenado = historial.stream()
                .sorted(Comparator.comparing(RegistroCumplimiento::getFecha).reversed())
                .toList();

        int racha = 0;
        LocalDate fechaEsperada = null;

        for (RegistroCumplimiento registro : ordenado) {
            if (!registro.isCumplido()) break;

            if (fechaEsperada != null && !registro.getFecha().equals(fechaEsperada)) {
                break;
            }

            racha++;
            fechaEsperada = registro.getFecha().minusDays(1);
        }
        return racha;
    }

    public int calcularRachaMasLarga(List<RegistroCumplimiento> historial) {
        if (historial.isEmpty()) return 0;

        List<RegistroCumplimiento> ordenado = historial.stream()
                .sorted(Comparator.comparing(RegistroCumplimiento::getFecha))
                .toList();

        int rachaMasLarga = 0;
        int rachaActual = 0;
        LocalDate fechaAnterior = null;

        for (RegistroCumplimiento registro : ordenado) {
            if (registro.isCumplido()) {
                if (fechaAnterior != null && !registro.getFecha().equals(fechaAnterior.plusDays(1))) {
                    rachaActual = 1;
                } else {
                    rachaActual++;
                }
                rachaMasLarga = Math.max(rachaMasLarga, rachaActual);
                fechaAnterior = registro.getFecha();
            } else {
                rachaActual = 0;
                fechaAnterior = null;
            }
        }
        return rachaMasLarga;
    }

    public Map<DayOfWeek, List<RegistroCumplimiento>> agruparPorDiaDeSemana(List<RegistroCumplimiento> historial) {
        return historial.stream()
                .collect(Collectors.groupingBy(r -> r.getFecha().getDayOfWeek()));
    }
}
