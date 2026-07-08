package rutinagamer.desafio;

import java.util.function.Supplier;

public class DesafioFisico extends DesafioAbstracto {

    private static final int INTENTOS_MAXIMOS = 2;

    private final int movimientosRequeridos;
    private final Supplier<Integer> lectorSensor;

    public DesafioFisico(int movimientosRequeridos, Supplier<Integer> lectorSensor) {
        super(INTENTOS_MAXIMOS);
        if (movimientosRequeridos <= 0) {
            throw new IllegalArgumentException("movimientosRequeridos debe ser mayor a 0");
        }
        this.movimientosRequeridos = movimientosRequeridos;
        this.lectorSensor = lectorSensor;
    }

    @Override
    protected void prepararDesafio() {
    }

    @Override
    protected boolean verificarRespuesta(String entradaUsuario) {
        int movimientosDetectados = lectorSensor.get();
        return movimientosDetectados >= movimientosRequeridos;
    }

    @Override
    public String getDescripcion() {
        return "Desafio fisico: agitar el telefono";
    }

    @Override
    public String getEnunciado() {
        return "Agita el telefono al menos " + movimientosRequeridos + " veces";
    }
}
