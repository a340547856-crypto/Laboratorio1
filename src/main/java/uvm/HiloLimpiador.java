package uvm;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HiloLimpiador extends Thread {

    /* Variable compartida: cuántas celdas ha limpiado en total.
       static synchronized para que cualquier hilo la lea sin choques */
    private static int limpiadas = 0;

    public static synchronized void sumarLimpiadas(int n) {
        limpiadas += n;
    }

    public static synchronized int getLimpiadas() {
        return limpiadas;
    }

    private JLabel[][] matriz;
    private boolean pausado = false;
    private boolean terminado = false;

    public HiloLimpiador(JLabel[][] matriz) {
        this.matriz = matriz;
    }

    /* Pausa o reanuda; notifyAll() despierta al hilo si estaba en wait() */
    public synchronized void alternarPausa() {
        pausado = !pausado;
        notifyAll();
    }

    public synchronized void terminar() {
        terminado = true;
        notifyAll();
    }

    /* Si está en pausa se duerme con wait(); devuelve true si debe terminar */
    private synchronized boolean debeTerminar() throws InterruptedException {
        while (pausado && !terminado) {
            wait();
        }
        return terminado;
    }

    /* Limpia una fila con invokeLater: cada celda que no sea "-" queda vacía */
    private void limpiarFila(int fila) {
        SwingUtilities.invokeLater(() -> {
            int cuenta = 0;
            for (int c = 0; c < 15; c++) {
                if (!"-".equals(matriz[fila][c].getText())) {
                    matriz[fila][c].setText("-");
                    matriz[fila][c].setBackground(Color.WHITE);
                    cuenta++;
                }
            }
            sumarLimpiadas(cuenta);  // variable compartida
        });
    }

    @Override
    public void run() {
        int fila = 0;
        try {
            while (!debeTerminar()) {
                limpiarFila(fila);
                System.out.println("Limpiando fila " + fila);
                fila = (fila + 1) % 15;  // al llegar a la última fila vuelve a la primera
                sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("Limpiador interrumpido");
        }
    }
}