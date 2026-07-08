package rutinagamer.desafio;

import org.junit.Test;

import rutinagamer.excepciones.DesafioExpiradoException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DesafioFisicoTest {

    @Test
    public void evaluarConMovimientosSuficientesRetornaTrue() throws DesafioExpiradoException {
        // lectorSensor simulado, siempre reporta 5 movimientos
        DesafioFisico desafio = new DesafioFisico(3, () -> 5);
        desafio.generar();

        assertTrue(desafio.evaluar("ignorado"));
    }

    @Test
    public void evaluarConMovimientosInsuficientesRetornaFalse() throws DesafioExpiradoException {
        DesafioFisico desafio = new DesafioFisico(10, () -> 2);
        desafio.generar();

        assertFalse(desafio.evaluar("ignorado"));
    }
}
