package rutinagamer.modelo;

import org.junit.Test;

import java.time.LocalTime;

import rutinagamer.desafio.Desafio;
import rutinagamer.excepciones.DesafioExpiradoException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class AlarmaTest {

    /** Desafio de prueba cuyo resultado se puede fijar de antemano. */
    private static class DesafioFalso implements Desafio {
        private final boolean resultado;
        private final boolean lanzarExpirado;
        private boolean generado;

        DesafioFalso(boolean resultado) {
            this(resultado, false);
        }

        DesafioFalso(boolean resultado, boolean lanzarExpirado) {
            this.resultado = resultado;
            this.lanzarExpirado = lanzarExpirado;
        }

        @Override
        public void generar() {
            generado = true;
        }

        @Override
        public boolean evaluar(String entradaUsuario) throws DesafioExpiradoException {
            if (lanzarExpirado) {
                throw new DesafioExpiradoException("expirado");
            }
            return resultado;
        }

        @Override
        public String getDescripcion() {
            return "Desafio falso";
        }

        @Override
        public String getEnunciado() {
            return "Enunciado falso";
        }
    }

    @Test
    public void dispararGeneraElDesafio() {
        DesafioFalso desafio = new DesafioFalso(true);
        Alarma alarma = new Alarma(LocalTime.of(7, 0), desafio);

        alarma.disparar();

        assertTrue(desafio.generado);
    }

    @Test
    public void intentarSuperarConRespuestaCorrectaMarcaLaAlarmaComoSuperada() throws DesafioExpiradoException {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioFalso(true));
        alarma.disparar();

        boolean resultado = alarma.intentarSuperar("cualquier cosa");

        assertTrue(resultado);
        assertTrue(alarma.isSuperada());
    }

    @Test
    public void intentarSuperarConDesafioExpiradoLanzaExcepcion() {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioFalso(true, true));
        alarma.disparar();

        assertThrows(DesafioExpiradoException.class, () -> alarma.intentarSuperar("10"));
    }
}
