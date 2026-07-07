package rutinagame.desafio;

import java.util.Random;

/**
 * Desafio mental: una operacion aritmetica simple que el usuario debe
 * resolver. Hereda de DesafioAbstracto, que se encarga de intentos y expiracion;
 */
public class DesafioMental extends DesafioAbstracto {

    private static final int INTENTOS_MAXIMOS = 3;
    private static final char[] OPERADORES = {'+', '-', '*'};

    private final Random random;

    private int numeroA;
    private int numeroB;
    private char operador;

    public DesafioMental() {
        this(new Random());
    }

    /*
     * Constructor de paquete: permite inyectar un Random en pruebas si
     * en algun momento se necesita reproducibilidad exacta. Con el
     * constructor publico basta para uso normal
     */
    DesafioMental(Random random) {
        super(INTENTOS_MAXIMOS);
        this.random = random;
    }

    @Override
    protected void prepararDesafio() {
        numeroA = random.nextInt(50) + 1;
        numeroB = random.nextInt(50) + 1;
        operador = OPERADORES[random.nextInt(OPERADORES.length)];
    }

    @Override
    protected boolean verificarRespuesta(String entradaUsuario) {
        try {
            int respuestaNumerica = Integer.parseInt(entradaUsuario.trim());
            return respuestaNumerica == calcular(numeroA, numeroB, operador);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getDescripcion() {
        return "Desafio mental: resolver una operacion aritmetica";
    }

    public String getEnunciado() {
        if (!generado) {
            throw new IllegalStateException("Aun no se ha generado el desafio");
        }
        return "Cuanto es " + numeroA + " " + operador + " " + numeroB + "?";
    }

    /*
     * Funcion pura (sin estado, sin efectos secundarios): dado los
     * mismos argumentos siempre devuelve el mismo resultado. Se deja
     * package-private (sin "private") a proposito para poder probarla
     * directamente desde las pruebas unitarias, ademas de probarla
     * indirectamente a traves de evaluar().
     */
    static int calcular(int a, int b, char operador) {
        switch (operador) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            default: throw new IllegalArgumentException("Operador no soportado: " + operador);
        }
    }

    /* Visibles solo para el paquete: las usan las pruebas para calcular
    // de antemano cual es la respuesta correcta, sin exponerlas como
    parte publica de la clase.
     */
    int getNumeroA() {
        return numeroA;
    }

    int getNumeroB() {
        return numeroB;
    }

    char getOperador() {
        return operador;
    }
}
