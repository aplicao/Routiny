package rutinagamer.desafio;

import org.junit.jupiter.api.Test;
import rutinagamer.excepciones.DesafioExpiradoException;

import static org.junit.jupiter.api.Assertions.*;

class DesafioFisicoTest {

    @Test
    void evaluarConSuficientesMovimientosDevuelveTrue() throws DesafioExpiradoException {
        DesafioFisico desafio = new DesafioFisico(5, () -> 7);
        desafio.generar();

        assertTrue(desafio.evaluar(""));
    }

    @Test
    void evaluarConPocosMovimientosDevuelveFalse() throws DesafioExpiradoException {
        DesafioFisico desafio = new DesafioFisico(5, () -> 2);
        desafio.generar();

        assertFalse(desafio.evaluar(""));
    }

    @Test
    void cadaLlamadaAEvaluarConsultaElSensorDeNuevo() throws DesafioExpiradoException {
        int[] lecturas = {1, 6};
        int[] indice = {0};
        DesafioFisico desafio = new DesafioFisico(5, () -> lecturas[indice[0]++]);
        desafio.generar();

        assertFalse(desafio.evaluar("")); // primera lectura: 1, insuficiente
        assertTrue(desafio.evaluar(""));  // segunda lectura: 6, ya cumple
    }

    @Test
    void constructorRechazaMovimientosRequeridosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new DesafioFisico(0, () -> 5));
    }

    @Test
    void evaluarDespuesDeMarcarExpiradoLanzaDesafioExpiradoException() {
        DesafioFisico desafio = new DesafioFisico(5, () -> 10);
        desafio.generar();
        desafio.marcarExpirado();

        assertThrows(DesafioExpiradoException.class, () -> desafio.evaluar(""));
    }
}
