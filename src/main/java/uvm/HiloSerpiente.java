package uvm;
import javax.swing.*;
import java.awt.Color;

public class HiloSerpiente extends Thread {
    private final JLabel[][] matriz; 
    private final Object lock = new Object();
    private boolean pausado = false;
    private boolean terminado = false;

    public HiloSerpiente(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    @Override
    public void run() {
        int contador = 1;

        for (int fila = 0; fila < 15; fila++) {
            for (int i = 0; i < 15; i++) {
                // Filas pares: izquierda -> derecha. Filas impares: derecha -> izquierda
                int col = (fila % 2 == 0) ? i : 14 - i;

                // Si está en pausa se queda esperando; si lo terminaron, sale
                if (!esperarSiPausado()) {
                    return;
                }

                final int f = fila;
                final int c = col;
                final int numero = contador++;

                // Toda modificación de la interfaz se envía al EDT
                SwingUtilities.invokeLater(() -> {
                    matriz[f][c].setForeground(Color.BLUE);
                    matriz[f][c].setText(String.valueOf(numero));
                });

                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    // Bloquea mientras esté en pausa. 
    private boolean esperarSiPausado() {
        synchronized (lock) {
            while (pausado && !terminado) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    return false;
                }
            }
            return !terminado;
        }
    }

    public void pausar() {
        synchronized (lock) {
            pausado = true;
        }
    }

    public void reanudar() {
        synchronized (lock) {
            pausado = false;
            lock.notifyAll();
        }
    }

    public void terminar() {
        synchronized (lock) {
            terminado = true;
            lock.notifyAll();
        }
    }
}