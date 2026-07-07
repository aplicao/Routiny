package rutinagame.desafio;

import rutinagame.excepciones.DesafioExpiradoException;

/*
 * Contrato que debe cumplir cualquier desafio usado para verificar que el
 * usuario esta realmente despierto
 */
public interface Desafio {

    /*
     * Prepara una nueva instancia del desafio (nuevos numeros, reinicia
     * intentos, etc.). Debe llamarse antes de evaluar()
     */
    void generar();

    /*
     * Evalua la entrada del usuario contra el desafio ya generado.
     *
     * @param entradaUsuario para un desafio mental, la respuesta escrita;
     *                       para uno fisico, una representacion de lo que
     *                       detecto el sensor
     * @return true si el usuario supero el desafio en este intento
     * @throws DesafioExpiradoException si el tiempo limite ya se cumplio
     */
    boolean evaluar(String entradaUsuario) throws DesafioExpiradoException;

    /*
     * @return una descripcion corta del tipo de desafio
     */
    String getDescripcion();
}
