package uvm;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaMatrizHilos extends JFrame {

    /* Textos que se repiten en los botones */
    private static final String TXT_INICIAR = "Iniciar";
    private static final String TXT_PAUSA = "Pausa";
    private static final String TXT_TERMINAR = "Terminar";

    private transient HiloCazador hcaz;
    private transient HiloClima hcli;
    private transient HiloLimpiador hlim;
    private transient HiloRebotador hreb;
    private transient HiloSerpiente hser;

    // Matriz de 15x15 JLabels (Recurso Compartido)
    private JLabel[][] matriz = new JLabel[15][15];

    // Botones Hilo Serpiente
    private JButton btIniciarS;
    private JButton btPausaS;
    private JButton btTerminarS;

    // Botones Hilo Cazador
    private JButton btIniciarC;
    private JButton btPausaC;
    private JButton btTerminarC;

    // Botones Hilo Rebotador
    private JButton btIniciarR;
    private JButton btPausaR;
    private JButton btTerminarR;

    // Botones Hilo Clima
    private JButton btIniciarCl;
    private JButton btPausaCl;
    private JButton btTerminarCl;

    // Botones Hilo Limpiador
    private JButton btIniciarL;
    private JButton btPausaL;
    private JButton btTerminarL;

    public VentanaMatrizHilos() {
        super("Práctica de Concurrencia - Matriz Compartida");
        inicializarGUI();
        asociarEventos();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Centrar en pantalla
    }

    private void inicializarGUI() {
        setLayout(new BorderLayout(10, 10));

        // -----------------------------------------------------------------
        // CENTRO: Matriz 15x15 JLabels
        // -----------------------------------------------------------------
        JPanel panelMatriz = new JPanel(new GridLayout(15, 15, 2, 2));
        panelMatriz.setBorder(BorderFactory.createTitledBorder("Matriz Compartida (15x15)"));

        for (int fila = 0; fila < 15; fila++) {
            for (int col = 0; col < 15; col++) {
                JLabel lbl = new JLabel("-", SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(Color.WHITE);
                lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                lbl.setFont(new Font("Monospaced", Font.BOLD, 18));

                matriz[fila][col] = lbl;
                panelMatriz.add(lbl);
            }
        }
        add(panelMatriz, BorderLayout.CENTER);

        // -----------------------------------------------------------------
        // NORTE: Paneles Serpiente y Cazador
        // -----------------------------------------------------------------
        JPanel panelNorte = new JPanel(new GridLayout(1, 2, 5, 5));

        // Subpanel Serpiente
        JPanel panelSerpiente = new JPanel(new FlowLayout());
        panelSerpiente.setBorder(BorderFactory.createTitledBorder("Hilo Serpiente"));
        btIniciarS = new JButton(TXT_INICIAR);
        btPausaS = new JButton(TXT_PAUSA);
        btTerminarS = new JButton(TXT_TERMINAR);
        panelSerpiente.add(btIniciarS);
        panelSerpiente.add(btPausaS);
        panelSerpiente.add(btTerminarS);

        // Subpanel Cazador
        JPanel panelCazador = new JPanel(new FlowLayout());
        panelCazador.setBorder(BorderFactory.createTitledBorder("Hilo Cazador"));
        btIniciarC = new JButton(TXT_INICIAR);
        btPausaC = new JButton(TXT_PAUSA);
        btTerminarC = new JButton(TXT_TERMINAR);
        panelCazador.add(btIniciarC);
        panelCazador.add(btPausaC);
        panelCazador.add(btTerminarC);

        panelNorte.add(panelSerpiente);
        panelNorte.add(panelCazador);
        add(panelNorte, BorderLayout.NORTH);

        // -----------------------------------------------------------------
        // SUR: Paneles Rebotador y Clima
        // -----------------------------------------------------------------
        JPanel panelSur = new JPanel(new GridLayout(1, 2, 5, 5));

        // Subpanel Rebotador
        JPanel panelRebotador = new JPanel(new FlowLayout());
        panelRebotador.setBorder(BorderFactory.createTitledBorder("Hilo Rebotador"));
        btIniciarR = new JButton(TXT_INICIAR);
        btPausaR = new JButton(TXT_PAUSA);
        btTerminarR = new JButton(TXT_TERMINAR);
        panelRebotador.add(btIniciarR);
        panelRebotador.add(btPausaR);
        panelRebotador.add(btTerminarR);

        // Subpanel Clima
        JPanel panelClima = new JPanel(new FlowLayout());
        panelClima.setBorder(BorderFactory.createTitledBorder("Hilo Clima"));
        btIniciarCl = new JButton(TXT_INICIAR);
        btPausaCl = new JButton(TXT_PAUSA);
        btTerminarCl = new JButton(TXT_TERMINAR);
        panelClima.add(btIniciarCl);
        panelClima.add(btPausaCl);
        panelClima.add(btTerminarCl);

        panelSur.add(panelRebotador);
        panelSur.add(panelClima);
        add(panelSur, BorderLayout.SOUTH);

        // -----------------------------------------------------------------
        // WEST: Panel Limpiador (Botones en Vertical)
        // -----------------------------------------------------------------
        JPanel panelLimpiador = new JPanel(new GridLayout(3, 1, 5, 5));
        panelLimpiador.setBorder(BorderFactory.createTitledBorder("Limpiador"));
        btIniciarL = new JButton(TXT_INICIAR);
        btPausaL = new JButton(TXT_PAUSA);
        btTerminarL = new JButton(TXT_TERMINAR);

        panelLimpiador.add(btIniciarL);
        panelLimpiador.add(btPausaL);
        panelLimpiador.add(btTerminarL);

        JPanel panelWestContenedor = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelWestContenedor.add(panelLimpiador);
        add(panelWestContenedor, BorderLayout.WEST);
    }

    private void asociarEventos() {
        // Eventos Hilo Serpiente (con lambda)
        btIniciarS.addActionListener(e -> iniciarSerpiente());
        btPausaS.addActionListener(e -> pausarSerpiente());
        btTerminarS.addActionListener(e -> terminarSerpiente());

        // Eventos Hilo Cazador (con lambda)
        btIniciarC.addActionListener(e -> iniciarCazador());
        btPausaC.addActionListener(e -> pausarCazador());
        btTerminarC.addActionListener(e -> terminarCazador());

        // Eventos Hilo Rebotador
        btIniciarR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarRebotador();
            }
        });
        btPausaR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarRebotador();
            }
        });
        btTerminarR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminarRebotador();
            }
        });

        // Eventos Hilo Clima
        btIniciarCl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarClima();
            }
        });
        btPausaCl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarClima();
            }
        });
        btTerminarCl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminarClima();
            }
        });

        // Eventos Hilo Limpiador
        btIniciarL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarLimpiador();
            }
        });
        btPausaL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarLimpiador();
            }
        });
        btTerminarL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminarLimpiador();
            }
        });
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Serpiente)
    // -----------------------------------------------------------------
    private void iniciarSerpiente() {
        /* Solo crea un hilo nuevo si no hay uno corriendo */
        if (hser == null || !hser.isAlive()) {
            hser = new HiloSerpiente(matriz);
            hser.start();
        }
    }

    private void pausarSerpiente() {
        /* Pausa si estaba corriendo, o reanuda si estaba en pausa */
        if (hser != null) {
            hser.alternarPausa();
        }
    }

    private void terminarSerpiente() {
        if (hser != null) {
            hser.terminar();
            hser = null;
        }
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Cazador)
    // -----------------------------------------------------------------
    private void iniciarCazador() {
        /* Solo crea un hilo nuevo si no hay uno corriendo */
        if (hcaz == null || !hcaz.isAlive()) {
            hcaz = new HiloCazador(matriz);
            hcaz.start();
        }
    }

    private void pausarCazador() {
        /* Pausa si estaba corriendo, o reanuda si estaba en pausa */
        if (hcaz != null) {
            hcaz.alternarPausa();
        }
    }

    private void terminarCazador() {
        if (hcaz != null) {
            hcaz.terminar();
            hcaz = null;
        }
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Rebotador)
    // -----------------------------------------------------------------
    private void iniciarRebotador() {
        /* Solo crea un hilo nuevo si no hay uno corriendo */
        if (hreb == null || !hreb.isAlive()) {
            hreb = new HiloRebotador(matriz);
            hreb.start();
        }
    }

    private void pausarRebotador() {
        /* Pausa si estaba corriendo, o reanuda si estaba en pausa */
        if (hreb != null) {
            hreb.alternarPausa();
        }
    }

    private void terminarRebotador() {
        if (hreb != null) {
            hreb.terminar();
            hreb = null;
        }
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Clima)
    // -----------------------------------------------------------------
    private void iniciarClima() {
        /* Solo crea un hilo nuevo si no hay uno corriendo */
        if (hcli == null || !hcli.isAlive()) {
            hcli = new HiloClima(matriz);
            hcli.start();
        }
    }

    private void pausarClima() {
        /* Pausa si estaba corriendo, o reanuda si estaba en pausa */
        if (hcli != null) {
            hcli.alternarPausa();
        }
    }

    private void terminarClima() {
        if (hcli != null) {
            hcli.terminar();
            hcli = null;
        }
    }

    // -----------------------------------------------------------------
    // Métodos privados para manejo de eventos (Limpiador)
    // -----------------------------------------------------------------
    private void iniciarLimpiador() {
        /* Solo crea un hilo nuevo si no hay uno corriendo */
        if (hlim == null || !hlim.isAlive()) {
            hlim = new HiloLimpiador(matriz);
            hlim.start();
        }
    }

    private void pausarLimpiador() {
        /* Pausa si estaba corriendo, o reanuda si estaba en pausa */
        if (hlim != null) {
            hlim.alternarPausa();
        }
    }

    private void terminarLimpiador() {
        if (hlim != null) {
            hlim.terminar();
            hlim = null;
        }
    }

    // Métodos auxiliares para acceso de los estudiantes
    public JLabel[][] getMatrizLabels() {
        return matriz;
    }

}