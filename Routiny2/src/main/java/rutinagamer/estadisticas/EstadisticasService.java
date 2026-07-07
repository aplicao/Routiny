package rutinagamer.estadisticas;

import rutinagamer.modelo.RegistroCumplimiento;

import java.time.DayOfWeek;
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
        return (int) historial.stream()
                .sorted(Comparator.comparing(RegistroCumplimiento::getFecha).reversed())
                .takeWhile(RegistroCumplimiento::isCumplido)
                .count();
    }

    public int calcularRachaMasLarga(List<RegistroCumplimiento> historial) {
        List<RegistroCumplimiento> ordenado = historial.stream()
                .sorted(Comparator.comparing(RegistroCumplimiento::getFecha))
                .toList();

        int rachaMasLarga = 0;
        int rachaActual = 0;
        for (RegistroCumplimiento registro : ordenado) {
            if (registro.isCumplido()) {
                rachaActual++;
                rachaMasLarga = Math.max(rachaMasLarga, rachaActual);
            } else {
                rachaActual = 0;
            }
        }
        return rachaMasLarga;
    }

    public Map<DayOfWeek, List<RegistroCumplimiento>> agruparPorDiaDeSemana(List<RegistroCumplimiento> historial) {
        return historial.stream()
                .collect(Collectors.groupingBy(r -> r.getFecha().getDayOfWeek()));
    }
}
