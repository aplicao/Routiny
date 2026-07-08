package rutinagamer.modelo;

import rutinagamer.desafio.Desafio;
import rutinagamer.excepciones.DesafioExpiradoException;

import java.time.LocalTime;

public class Alarma {

    private final LocalTime horario;
    private final Desafio desafio;
    private boolean activa;
    private boolean superada;

    public Alarma(LocalTime horario, Desafio desafio) {
        if (horario == null) {
            throw new IllegalArgumentException("horario no puede ser nulo");
        }
        if (desafio == null) {
            throw new IllegalArgumentException("desafio no puede ser nulo");
        }
        this.horario = horario;
        this.desafio = desafio;
        this.activa = true;
        this.superada = false;
    }

    /*
      Simula que la alarma suena, prepara una nueva instancia del
      desafio y limpia el estado de
      "superada" de la vez anterior.
     */
    public void disparar() {
        superada = false;
        desafio.generar();
    }

    /*
      Intenta apagar la alarma con la entrada del usuario.
     */
    public boolean intentarSuperar(String entradaUsuario) throws DesafioExpiradoException {
        boolean resultado = desafio.evaluar(entradaUsuario);
        if (resultado) {
            superada = true;
        }
        return resultado;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public boolean isActiva() {
        return activa;
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }

    public boolean isSuperada() {
        return superada;
    }

    public Desafio getDesafio() {
        return desafio;
    }
}
