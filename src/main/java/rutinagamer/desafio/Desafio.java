package rutinagamer.desafio;

import rutinagamer.excepciones.DesafioExpiradoException;

public interface Desafio {

    void generar();

    boolean evaluar(String entradaUsuario) throws DesafioExpiradoException;

    String getDescripcion();

    String getEnunciado();
}
