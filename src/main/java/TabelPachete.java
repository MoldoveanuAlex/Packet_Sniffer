import org.pcap4j.core.PcapNetworkInterface;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TabelPachete extends JFrame {

    Model model;

    TabelPachete(Object[][] obj, String[] capTabel, final PcapNetworkInterface adr) {
        super("Analizator Pachete");

        JPanel panou = new JPanel(new BorderLayout());
        model = new Model(obj, capTabel);

        final JTable tabel = new JTable(model);
        panou.add(new JScrollPane(tabel));

        final JPanel detaliiPanel = new JPanel(new BorderLayout());
        final JTextArea detaliiText = new JTextArea();
        detaliiText.setEditable(false);
        detaliiPanel.add(new JScrollPane(detaliiText), BorderLayout.CENTER);
        panou.add(detaliiPanel, BorderLayout.SOUTH);
        detaliiPanel.setVisible(false);

        tabel.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                int selectedRow = tabel.getSelectedRow();
                if (selectedRow != -1) {
                    detaliiText.setText("Detalii pachet:\n" + model.getPacketDetails(selectedRow));
                    detaliiPanel.setVisible(true);
                }
                tabel.setSelectionBackground(Color.BLUE);

            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton export = new JButton("Salveaza raport");
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ExportDoc.export(TabelPachete.this, adr);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        topPanel.add(export);
        add(topPanel, BorderLayout.NORTH);
        add(panou, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setPreferredSize(new Dimension(1920, 1080));

        pack();
    }


    class Model extends AbstractTableModel{
        
        ArrayList<Object[]> rand;
        String[] capTabel;
        ArrayList<String> infos;

        Model(Object[][] obj, String[] capTabel){
            this.capTabel = capTabel;
            rand = new ArrayList<Object[]>();
            Collections.addAll(rand, obj);
            infos = new ArrayList<String>();
        }

        private Object[] getRowData(int row) {
            Object[] rowData = new Object[model.getColumnCount()];

            for (int i = 0; i < model.getColumnCount(); i++) {
                rowData[i] = model.getValueAt(row, i);
            }
            return rowData;
        }

        @Override
        public int getRowCount() {
            return rand.size();
        }
        @Override
        public int getColumnCount() {
            return capTabel.length;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rand.get(rowIndex)[columnIndex];
        }
        public String getColumnName(int index) {
            return capTabel[index];
        }

        void adauga(String nr, String timestamp, String srsIP,
                    String desIP, String srsPort, String desPort,
                    String protocol, String lungime, String info,
                    String serviciu){
            String[] str = new String[10];
            String spatiu = "   ";
            str[0] = spatiu+nr;
            str[1] = spatiu+timestamp;
            str[2] = spatiu+srsIP;
            str[3] = spatiu+desIP;
            str[4] = spatiu+srsPort;
            str[5] = spatiu+desPort;
            str[6] = spatiu+protocol;
            str[7] = spatiu+serviciu;
            str[8] = spatiu+lungime;
            str[9] = spatiu+info;
            rand.add(str);
            fireTableDataChanged();
        }

        void adaugaInfo(String info){
            infos.add(info);
        }

        public String getPacketDetails(int selectedRow) {
            return infos.get(selectedRow);
        }
    }
}
