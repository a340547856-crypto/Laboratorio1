package uvm;
 
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
 
public class HiloClima extends Thread {
 
    /* Variable compartida: true si está lloviendo.
       static synchronized para que cualquier hilo la lea sin choques */
    private static boolean lluvia = false;
 
    public static synchronized void setLluvia(boolean valor) {
        lluvia = valor;
    }
 
    public static synchronized boolean getLluvia() {
        return lluvia;
    }
 
    private JLabel[][] matriz;
    private boolean pausado = false;
    private boolean terminado = false;
 
    public HiloClima(JLabel[][] matriz) {
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
 
    /* Pinta el fondo de las celdas vacías ("-") con invokeLater */
    private void pintarFondo(Color color) {
        SwingUtilities.invokeLater(() -> {
            for (int f = 0; f < 15; f++) {
                for (int c = 0; c < 15; c++) {
                    if ("-".equals(matriz[f][c].getText())) {
                        matriz[f][c].setBackground(color);
                    }
                }
            }
        });
    }
 
    @Override
    public void run() {
        try {
            while (!debeTerminar()) {
                /* Cambia el clima y lo guarda en la variable compartida */
                setLluvia(!getLluvia());
                if (getLluvia()) {
                    pintarFondo(new Color(180, 210, 255));  // lluvia: azul claro
                } else {
                    pintarFondo(new Color(255, 255, 180));  // sol: amarillo claro
                }
                sleep(3000);
            }
        } catch (InterruptedException e) {
            System.out.println("Clima interrumpido");
        }
 
        pintarFondo(Color.WHITE);  // al terminar, regresa el fondo a blanco
    }
}
