package rutinagamer.desafio;

import java.util.Random;

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

    @Override
    public String getEnunciado() {
        if (!generado) {
            throw new IllegalStateException("Aun no se ha generado el desafio");
        }
        return "Cuanto es " + numeroA + " " + operador + " " + numeroB + "?";
    }

    static int calcular(int a, int b, char operador) {
        switch (operador) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            default: throw new IllegalArgumentException("Operador no soportado: " + operador);
        }
    }

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
