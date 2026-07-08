package rutinagamer.desafio;

import org.junit.Test;

import java.util.Random;

import rutinagamer.excepciones.DesafioExpiradoException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DesafioMentalTest {

    @Test
    public void calcularSuma() {
        assertEquals(7, DesafioMental.calcular(3, 4, '+'));
    }

    @Test
    public void evaluarConRespuestaCorrectaRetornaTrue() throws DesafioExpiradoException {
        // Random con semilla fija para que el desafio generado sea predecible
        DesafioMental desafio = new DesafioMental(new Random(42));
        desafio.generar();

        int respuestaCorrecta = DesafioMental.calcular(
                desafio.getNumeroA(), desafio.getNumeroB(), desafio.getOperador());

        assertTrue(desafio.evaluar(String.valueOf(respuestaCorrecta)));
    }
}
