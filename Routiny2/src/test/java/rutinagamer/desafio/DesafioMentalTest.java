package rutinagamer.desafio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rutinagamer.excepciones.DesafioExpiradoException;

import static org.junit.jupiter.api.Assertions.*;

class DesafioMentalTest {

    private DesafioMental desafio;

    @BeforeEach
    void setUp() {
        desafio = new DesafioMental();
        desafio.generar();
    }

    @Test
    void generarProduceUnEnunciadoConNumeros() {
        String enunciado = desafio.getEnunciado();
        assertNotNull(enunciado);
        assertTrue(enunciado.contains("Cuanto es"));
    }

    @Test
    void evaluarConRespuestaCorrectaDevuelveTrue() throws DesafioExpiradoException {
        int respuestaCorrecta = DesafioMental.calcular(
                desafio.getNumeroA(), desafio.getNumeroB(), desafio.getOperador());

        assertTrue(desafio.evaluar(String.valueOf(respuestaCorrecta)));
    }

    @Test
    void evaluarConRespuestaIncorrectaDevuelveFalse() throws DesafioExpiradoException {
        int respuestaCorrecta = DesafioMental.calcular(
                desafio.getNumeroA(), desafio.getNumeroB(), desafio.getOperador());

        assertFalse(desafio.evaluar(String.valueOf(respuestaCorrecta + 1000)));
    }

    @Test
    void evaluarConTextoNoNumericoDevuelveFalseSinLanzarExcepcion() throws DesafioExpiradoException {
        assertFalse(desafio.evaluar("no-soy-un-numero"));
    }

    @Test
    void evaluarSinGenerarLanzaIllegalStateException() {
        DesafioMental desafioNuevo = new DesafioMental();
        assertThrows(IllegalStateException.class, () -> desafioNuevo.evaluar("5"));
    }

    @Test
    void evaluarDespuesDeMarcarExpiradoLanzaDesafioExpiradoException() {
        desafio.marcarExpirado();
        assertThrows(DesafioExpiradoException.class, () -> desafio.evaluar("5"));
    }

    @Test
    void seAgotanLosIntentosMaximosTrasVariasRespuestasIncorrectas() throws DesafioExpiradoException {
        int respuestaCorrecta = DesafioMental.calcular(
                desafio.getNumeroA(), desafio.getNumeroB(), desafio.getOperador());
        String respuestaMala = String.valueOf(respuestaCorrecta + 1000);

        assertTrue(desafio.quedanIntentos());
        desafio.evaluar(respuestaMala);
        desafio.evaluar(respuestaMala);
        desafio.evaluar(respuestaMala);

        assertFalse(desafio.quedanIntentos());
        assertEquals(3, desafio.getIntentosUsados());
    }

    @Test
    void calcularSuma() {
        assertEquals(5, DesafioMental.calcular(2, 3, '+'));
    }

    @Test
    void calcularResta() {
        assertEquals(-1, DesafioMental.calcular(2, 3, '-'));
    }

    @Test
    void calcularMultiplicacion() {
        assertEquals(6, DesafioMental.calcular(2, 3, '*'));
    }
}
