package uvm;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
public class HiloSerpiente extends Thread {
    /* Variables compartidas: static synchronized para que cualquier hilo las lea sin choques */
    private static int posSerpiente = 0;
    private static int pasosSerpiente = 0;

    public static synchronized void setPosSerpiente(int pos) {
        posSerpiente = pos;
        pasosSerpiente++;
    }
    public static synchronized int getPosSerpiente() {
        return posSerpiente;
    }
    public static synchronized int getPasosSerpiente() {
        return pasosSerpiente;
    }
    private JLabel[][] matriz;
    private boolean pausado = false;
    private boolean terminado = false;
    
    public HiloSerpiente(JLabel[][] matriz) {
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

    /* Cambia una celda con invokeLater: pos de 0 a 224, fila = pos / 15, columna = pos % 15 */
    private void pintar(int pos, String texto, Color color) {
        SwingUtilities.invokeLater(() -> {
            matriz[pos / 15][pos % 15].setText(texto);
            matriz[pos / 15][pos % 15].setBackground(color);
        });
    }

    @Override
    public void run() {
        int cabeza = 0;
        try {
            while (!debeTerminar()) {
                pintar(cabeza, "S", Color.GREEN);                    // dibuja la cabeza
                pintar((cabeza - 3 + 225) % 225, "-", Color.WHITE);  // borra la cola
                setPosSerpiente(cabeza);                             // variable compartida
                cabeza = (cabeza + 1) % 225;
                sleep(200);
            }
        } catch (InterruptedException e) {
            System.out.println("Serpiente interrumpida");
        }

        /* Al terminar, borra las 3 celdas que quedaron pintadas */
        for (int k = 1; k <= 3; k++) {
            pintar((cabeza - k + 225) % 225, "-", Color.WHITE);
        }
    }
}