package rutinagamer.desafio;

import rutinagamer.excepciones.DesafioExpiradoException;

public abstract class DesafioAbstracto implements Desafio {

    private final int intentosMaximos;
    private int intentosUsados;
    protected boolean generado;
    private boolean expirado;

    protected DesafioAbstracto(int intentosMaximos) {
        if (intentosMaximos <= 0) {
            throw new IllegalArgumentException("intentosMaximos debe ser mayor a 0");
        }
        this.intentosMaximos = intentosMaximos;
    }

    @Override
    public final void generar() {
        prepararDesafio();
        generado = true;
        expirado = false;
        intentosUsados = 0;
    }


    protected abstract void prepararDesafio();


    public void marcarExpirado() {
        this.expirado = true;
    }

    @Override
    public final boolean evaluar(String entradaUsuario) throws DesafioExpiradoException {
        if (!generado) {
            throw new IllegalStateException("Debes llamar a generar() antes de evaluar()");
        }
        if (expirado) {
            throw new DesafioExpiradoException("El desafio ya expiro, no se aceptan mas respuestas");
        }
        if (!quedanIntentos()) {
            return false;
        }
        intentosUsados++;
        return verificarRespuesta(entradaUsuario);
    }

    public boolean quedanIntentos() {
        return intentosUsados < intentosMaximos;
    }

    public int getIntentosUsados() {
        return intentosUsados;
    }

    protected abstract boolean verificarRespuesta(String entradaUsuario);
}
