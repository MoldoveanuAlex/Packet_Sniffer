import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pcap4j.core.PcapNetworkInterface;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportDoc {
    public static void export(TabelPachete tabelPachete, PcapNetworkInterface adr) throws IOException {
        XSSFWorkbook document = new XSSFWorkbook();
        XSSFSheet pagina = document.createSheet("Raport");

        CellStyle centrare = document.createCellStyle();
        centrare.setAlignment(HorizontalAlignment.CENTER);

        String[] capTabel = {"Nr.", "Timestamp", "IP Sursa",
                "IP Destinatie","Port Sursa","Port Destinatie",
                "Protocol", "Serviciu", "Lungime Pachet (Bytes)",
                "Info"};

        Row cap = pagina.createRow(0);
        int nrCol = 0;
        for (String s : capTabel) {
            Cell cell = cap.createCell(nrCol++);
            cell.setCellValue((String) s);
            cell.setCellStyle(centrare);
        }
        pagina.autoSizeColumn(0);


        int nrRand = 1;
        for (int i=0;i < tabelPachete.model.getRowCount();i++) {
            Row row = pagina.createRow(nrRand++);
            int nrColoana = 0;
            for (int j = 0; j < tabelPachete.model.getColumnCount(); j++) {
                Cell cell = row.createCell(nrColoana++);
                cell.setCellValue((String) tabelPachete.model.getValueAt(i, j));
                if(nrCol!=10) cell.setCellStyle(centrare);
            }
        }
        for (int i=1; i < tabelPachete.model.getRowCount();i++)
            pagina.autoSizeColumn(i);

        LocalDateTime timp = LocalDateTime.now();
        DateTimeFormatter formatTimp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = timp.format(formatTimp);

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setSelectedFile(new File("Raport " +
                adr.getDescription().toString() + " " + timestamp.toString() + ".xlsx"));
        fileChooser.setDialogTitle("Salveaza raport");
        int result = fileChooser.showSaveDialog(new JFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            FileOutputStream outputStream = new FileOutputStream(filePath);
            document.write(outputStream);
            document.close();

        }
    }
}
