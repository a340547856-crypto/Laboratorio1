package uvm;
 
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
 
public class HiloRebotador extends Thread {
 
    /* Variable compartida: cuántos rebotes ha dado en los bordes */
    private static int rebotes = 0;
 
    public static synchronized void sumarRebote() {
        rebotes++;
    }
 
    public static synchronized int getRebotes() {
        return rebotes;
    }
 
    private JLabel[][] matriz;
    private boolean pausado = false;
    private boolean terminado = false;
 
    public HiloRebotador(JLabel[][] matriz) {
        this.matriz = matriz;
    }
 
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
 
    /* Dibuja la "R" o la borra con invokeLater.
       Solo borra si la celda todavía tiene la "R", para no quitar lo de otro hilo */
    private void dibujar(int fila, int col, String texto, Color color) {
        SwingUtilities.invokeLater(() -> {
            if (texto.equals("R") || "R".equals(matriz[fila][col].getText())) {
                matriz[fila][col].setText(texto);
                matriz[fila][col].setBackground(color);
            }
        });
    }
 
    @Override
    public void run() {
        int fila = 7;   // empieza en el centro
        int col = 7;
        int dirFila = 1;  // dirección: 1 baja/derecha, -1 sube/izquierda
        int dirCol = 1;
        try {
            while (!debeTerminar()) {
                dibujar(fila, col, "-", Color.WHITE);
 
                /* Si el siguiente paso se sale de la matriz, invierte la dirección */
                if (fila + dirFila < 0 || fila + dirFila > 14) {
                    dirFila = -dirFila;
                    sumarRebote();
                }
                if (col + dirCol < 0 || col + dirCol > 14) {
                    dirCol = -dirCol;
                    sumarRebote();
                }
                fila += dirFila;
                col += dirCol;
 
                dibujar(fila, col, "R", Color.CYAN);
 
                /* Lee la variable compartida del clima: con lluvia va más lento */
                if (HiloClima.getLluvia()) {
                    sleep(300);
                } else {
                    sleep(100);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Rebotador interrumpido");
        }
 
        dibujar(fila, col, "-", Color.WHITE);  // borra su última posición
    }
}
