package rutinagame.desafio;

import rutinagame.excepciones.DesafioExpiradoException;

/*
 * Implementa control de intentos
 */
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

    /*
     * Hook que cada subclase implementa para preparar su propio estado
     * (ejemplo, sortear numeros para una suma)
     */
    protected abstract void prepararDesafio();

    /*
     * Se llama desde fuera cuando se acaba el tiempo. No se calcula el
     * tiempo aqui dentro con System.currentTimeMillis()
     */
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

    /*
     * Hook que cada subclase implementa con su propia logica de
     * verificacion (comparar un numero, consultar un sensor, etc.)
     */
    protected abstract boolean verificarRespuesta(String entradaUsuario);
}
