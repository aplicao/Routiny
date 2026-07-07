package rutinagamer.modelo;

import org.junit.jupiter.api.Test;
import rutinagamer.desafio.Desafio;
import rutinagamer.excepciones.DesafioExpiradoException;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AlarmaTest {

    private static class DesafioFalso implements Desafio {
        boolean generado = false;
        final boolean respuestaEsperadaCorrecta;

        DesafioFalso(boolean respuestaEsperadaCorrecta) {
            this.respuestaEsperadaCorrecta = respuestaEsperadaCorrecta;
        }

        @Override
        public void generar() {
            generado = true;
        }

        @Override
        public boolean evaluar(String entradaUsuario) {
            return respuestaEsperadaCorrecta;
        }

        @Override
        public String getDescripcion() {
            return "Desafio falso para pruebas";
        }

        @Override
        public String getEnunciado() {
            if (!generado) {
                throw new IllegalStateException("no generado");
            }
            return "enunciado de prueba";
        }
    }

    @Test
    void constructorRechazaHorarioNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Alarma(null, new DesafioFalso(true)));
    }

    @Test
    void constructorRechazaDesafioNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Alarma(LocalTime.of(7, 0), null));
    }

    @Test
    void unaAlarmaNuevaEstaActivaYNoSuperada() {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioFalso(true));
        assertTrue(alarma.isActiva());
        assertFalse(alarma.isSuperada());
    }

    @Test
    void dispararGeneraElDesafioAsociado() {
        DesafioFalso desafio = new DesafioFalso(true);
        Alarma alarma = new Alarma(LocalTime.of(7, 0), desafio);

        alarma.disparar();

        assertTrue(desafio.generado);
    }

    @Test
    void intentarSuperarConRespuestaCorrectaMarcaSuperada() throws DesafioExpiradoException {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioFalso(true));
        alarma.disparar();

        boolean resultado = alarma.intentarSuperar("cualquier cosa");

        assertTrue(resultado);
        assertTrue(alarma.isSuperada());
    }

    @Test
    void intentarSuperarConRespuestaIncorrectaNoMarcaSuperada() throws DesafioExpiradoException {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioFalso(false));
        alarma.disparar();

        boolean resultado = alarma.intentarSuperar("cualquier cosa");

        assertFalse(resultado);
        assertFalse(alarma.isSuperada());
    }

    @Test
    void activarYDesactivarCambianElEstado() {
        Alarma alarma = new Alarma(LocalTime.of(7, 0), new DesafioFalso(true));

        alarma.desactivar();
        assertFalse(alarma.isActiva());

        alarma.activar();
        assertTrue(alarma.isActiva());
    }

    @Test
    void getDesafioDevuelveLaMismaInstanciaInyectada() {
        DesafioFalso desafio = new DesafioFalso(true);
        Alarma alarma = new Alarma(LocalTime.of(7, 0), desafio);

        assertSame(desafio, alarma.getDesafio());
    }
}
