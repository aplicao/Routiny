package rutinagame.desafio;

import java.util.function.Supplier;

/*
 * En el desafio fisico el usuario debe agitar el telefono una cantidad
 * minima de veces. La lectura real vendria de un SensorEventListener de
 * Android (acelerometro), pero esta clase no sabe nada de Android, solo
 * conoce un Supplier<Integer>
 *
 * Se le pasa una lambda que lee el sensor real
 * en las pruebas, una lambda que devuelve un numero fijo
 */
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
        // No hay estado propio que preparar, el conteo de movimientos
        // lo entrega lectorSensor cada vez que se le consulta
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

    public String getEnunciado() {
        return "Agita el telefono al menos " + movimientosRequeridos + " veces";
    }
}
