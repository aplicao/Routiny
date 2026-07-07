package rutinagame.excepciones;

/*
 * Se usa cuando se intenta evaluar una respuesta después de que el tiempo limite del desafio ya se cumplio
 */

public class DesafioExpiradoException extends Exception {

    public DesafioExpiradoException(String mensaje) {
        super(mensaje);
    }
}
