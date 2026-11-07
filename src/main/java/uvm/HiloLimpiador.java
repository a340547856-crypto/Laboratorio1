package uvm;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HiloLimpiador extends Thread {

    /* Logger para mostrar mensajes en consola */
    private static final Logger LOGGER = Logger.getLogger(HiloLimpiador.class.getName());

    /* Variable compartida: cuántas celdas ha limpiado en total.
       Es static y synchronized para que cualquier hilo la lea sin choques */
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

    /* Pausa o reanuda el hilo y despierta a los hilos que esperan */
    public synchronized void alternarPausa() {
        pausado = !pausado;
        notifyAll();
    }

    public synchronized void terminar() {
        terminado = true;
        notifyAll();
    }

    /* Si está en pausa, el hilo se duerme hasta que lo despierten.
       Devuelve true si debe terminar */
    private synchronized boolean debeTerminar() throws InterruptedException {
        while (pausado && !terminado) {
            wait();
        }
        return terminado;
    }

    /* Limpia una fila usando invokeLater: cada celda que no sea "-" queda vacía */
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
            sumarLimpiadas(cuenta);
        });
    }

    @Override
    public void run() {
        int fila = 0;
        try {
            while (!debeTerminar()) {
                limpiarFila(fila);
                LOGGER.log(Level.INFO, "Limpiando fila {0}", fila);
                /* Al llegar a la última fila vuelve a la primera */
                fila = (fila + 1) % 15;
                sleep(500);
            }
        } catch (InterruptedException e) {
            /* Vuelve a marcar la interrupción, como pide Sonar */
            Thread.currentThread().interrupt();
            LOGGER.info("Limpiador interrumpido");
        }
    }
}