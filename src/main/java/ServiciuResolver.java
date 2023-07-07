import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class ServiciuResolver {

    private static String[][] readCSV() throws IOException {
        String line;
        int row = 0, col = 0;
        String[][] data;
        try (BufferedReader br = new BufferedReader(
                new FileReader("src/main/resources/service-names-port-numbers.csv"))) {
            while ((line = br.readLine()) != null) {
                col = Math.max(col, line.split(",").length);
                row++;
            }
            data = new String[row][col];
            br.close();
            int i = 0;
            BufferedReader br2 = new BufferedReader(
                    new FileReader("src/main/resources/service-names-port-numbers.csv"));
            while ((line = br2.readLine()) != null) {
                String[] values = line.split(",");
                for (int j = 0; j < values.length; j++) {
                    data[i][j] = values[j];
                }
                i++;
            }
        }
        return data;
    }

    public String numeServiciu(String srcPort, String desPort, String protocol) throws IOException {
        String[][] data = readCSV();
        String numeServiciuSrc = "Necunoscut";
        String numeServiciuDes = "Necunoscut";
        for(int i=1;i< data.length;i++) {
            if (Objects.equals(srcPort, data[i][1])
                    && protocol.toLowerCase().equals(data[i][2])
                    && !Objects.equals(data[i][0], ""))
                numeServiciuSrc = data[i][0];
            if (Objects.equals(desPort, data[i][1])
                    && protocol.toLowerCase().equals(data[i][2])
                    && !Objects.equals(data[i][0], ""))
                numeServiciuDes = data[i][0];
        }
        if(Objects.equals(numeServiciuSrc, "Necunoscut")) return numeServiciuDes;
        if(Objects.equals(numeServiciuDes, "Necunoscut")) return numeServiciuSrc;
        return numeServiciuSrc + " + " + numeServiciuDes;
    }

}
