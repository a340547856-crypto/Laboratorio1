package uvm;

import javax.swing.*;
import java.awt.Color;

public class HiloRebotador extends Thread {
    private final JLabel[][] matriz;
    private final Object lock = new Object();
    private boolean pausado = false;
    private boolean terminado = false;

    public HiloRebotador(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    @Override
    public void run() {
        int fila = 0;
        int col = 2;      // empieza en col 2 para no recorrer solo la diagonal principal
        int dirFila = 1;  // 1 = baja, -1 = sube
        int dirCol = 1;   // 1 = derecha, -1 = izquierda

        while (true) {
            // Si está en pausa espera; si lo terminaron, sale
            if (!esperarSiPausado()) {
                return;
            }

            final int f = fila;
            final int c = col;

            // Toda modificación de la interfaz se envía al EDT
            SwingUtilities.invokeLater(() -> {
                matriz[f][c].setForeground(Color.BLACK);
                matriz[f][c].setText("@");
            });

            // Calcular la siguiente posición
            int nuevaFila = fila + dirFila;
            int nuevaCol = col + dirCol;

            // Rebote: si se sale por arriba/abajo, invierte la dirección vertical
            if (nuevaFila < 0 || nuevaFila >= 15) {
                dirFila = -dirFila;
                nuevaFila = fila + dirFila;
            }
            // Rebote: si se sale por izquierda/derecha, invierte la dirección horizontal
            if (nuevaCol < 0 || nuevaCol >= 15) {
                dirCol = -dirCol;
                nuevaCol = col + dirCol;
            }

            fila = nuevaFila;
            col = nuevaCol;

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    // Bloquea mientras esté en pausa. Devuelve false si el hilo debe terminar.
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