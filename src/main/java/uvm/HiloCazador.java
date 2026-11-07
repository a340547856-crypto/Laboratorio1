package uvm;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
public class HiloCazador extends Thread {
    /* Logger: reemplaza al System.out para mostrar mensajes */
    private static final Logger LOGGER = Logger.getLogger(HiloCazador.class.getName());

    /* Variable compartida: cuántas veces ha atrapado a la serpiente */
    private static int capturas = 0;
    public static synchronized void sumarCaptura() {
        capturas++;
    }
    public static synchronized int getCapturas() {
        return capturas;
    }

    private JLabel[][] matriz;
    private boolean pausado = false;
    private boolean terminado = false;

    public HiloCazador(JLabel[][] matriz) {
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
    /* Dibuja la "C" o la borra con invokeLater.
       Solo borra si la celda todavía tiene la "C", para no quitar lo de otro hilo */
    private void dibujar(int fila, int col, String texto, Color color) {
        SwingUtilities.invokeLater(() -> {
            if (texto.equals("C") || "C".equals(matriz[fila][col].getText())) {
                matriz[fila][col].setText(texto);
                matriz[fila][col].setBackground(color);
            }
        });
    }

    @Override
    public void run() {
        int fila = 14;  // empieza en la esquina inferior derecha
        int col = 14;
        try {
            while (!debeTerminar()) {
                /* Espera a que la serpiente haya dado su primer paso */
                if (HiloSerpiente.getPasosSerpiente() > 0) {
                    int pos = HiloSerpiente.getPosSerpiente();  // variable compartida
                    int filaS = pos / 15;
                    int colS = pos % 15;

                    dibujar(fila, col, "-", Color.WHITE);
                    /* compare devuelve -1, 0 o 1: da un paso hacia la serpiente */
                    fila += Integer.compare(filaS, fila);
                    col += Integer.compare(colS, col);

                    /* Si la alcanzó, cuenta la captura y reaparece en la esquina */
                    if (fila == filaS && col == colS) {
                        sumarCaptura();
                        LOGGER.log(Level.INFO, "Capturas: {0}", getCapturas());
                        fila = 14;
                        col = 14;
                    }
                    dibujar(fila, col, "C", Color.RED);
                }
                sleep(150);
            }
        } catch (InterruptedException e) {
            LOGGER.info("Cazador interrumpido");
            Thread.currentThread().interrupt();  // vuelve a marcar la interrupción
        }
        dibujar(fila, col, "-", Color.WHITE);  // borra su última posición
    }
}