import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

public class ADRSelector {

    private PcapNetworkInterface adrSelectat;
    private PcapNetworkInterface adrSemiSelectat;
    private int timpSetat=60;

    public void selector() throws Exception {

        final JFrame frame = new JFrame("Selector adaptor de retea");

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel panouButoane = new JPanel();
        panouButoane.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton lightMode = new JButton("Mod luminos");
        lightMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                } catch( Exception ex ) {
                ex.printStackTrace();
            }
                SwingUtilities.updateComponentTreeUI(mainPanel);
            }
        });
        panouButoane.add(lightMode);


        JButton darkMode = new JButton("Mod intunecat");
        darkMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } catch( Exception ex ) {
                    ex.printStackTrace();
                }
                SwingUtilities.updateComponentTreeUI(mainPanel);
            }
        });
        panouButoane.add(darkMode);

        JLabel title = new JLabel("Selecteaza adaptorul de retea si timpul pentru captura:");
        title.setFont(new Font("Calibri", Font.BOLD, 25));

        JPanel panouAdr = new JPanel();
        List<PcapNetworkInterface> listaAdr = Pcaps.findAllDevs();
        int randuri = (int) Math.ceil(listaAdr.size() / 3.0);
        panouAdr.setLayout(new GridLayout(randuri, 3, 20, 20));
        panouAdr.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel adrLabel = new JLabel("Adaptor de retea:");
        panouAdr.add(adrLabel);

        for (final PcapNetworkInterface adr : listaAdr) {
            JButton butonAdr = new JButton(adr.getDescription());
            butonAdr.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adrSemiSelectat = adr;
                }
            });
            panouAdr.add(butonAdr);
        }

        final JPanel inputTimp = new JPanel();
        inputTimp.setLayout(new GridLayout(1,3));

        JLabel timpLabel = new JLabel("Timp pentru captura (in secunde, default=60):");
        inputTimp.add(timpLabel);

        final JTextField inputField = new JTextField("60");
        inputTimp.add(inputField);

        JButton inputButton = new JButton("Seteaza timp");
        inputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int timp = Integer.parseInt(inputField.getText());
                    if (timp > 0) {
                        timpSetat = timp;
                        inputTimp.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Timpul pentru captura trebuie sa fie un numar pozitiv mai mare decat 0.", "Eroare", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Introduceti un numar valid pentru timpul de captura.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        inputTimp.add(inputButton);

        JPanel startPanel = new JPanel();
        startPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (adrSemiSelectat == null) {
                    JOptionPane.showMessageDialog(frame, "Selectati un adaptor de retea.",
                            "Eroare", JOptionPane.ERROR_MESSAGE);
                } else {
                    adrSelectat = adrSemiSelectat;
                    frame.dispose();
                }
            }
        });
        startPanel.add(startButton);


        mainPanel.add(title);

        JScrollPane scrollPane = new JScrollPane(panouAdr);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane);

        JPanel inputPanel = new JPanel(new GridLayout(3,1));
        inputPanel.add(inputTimp);
        inputPanel.add(startPanel);
        inputPanel.add(panouButoane);

        mainPanel.add(inputPanel);

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1000, 800));
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.pack();

        while (adrSelectat == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public PcapNetworkInterface getAdrSelectat() {
        return adrSelectat;
    }

    public int getTimpSetat() {
        return timpSetat;
    }
}