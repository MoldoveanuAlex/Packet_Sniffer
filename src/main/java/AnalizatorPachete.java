import com.formdev.flatlaf.FlatDarkLaf;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import javax.swing.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class AnalizatorPachete {

    private static final String COUNT_KEY = AnalizatorPachete.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, -1); // -1 -> loop infinit

    private static final String READ_TIMEOUT_KEY = AnalizatorPachete.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 100); // [ms]

    private static final String SNAPLEN_KEY = AnalizatorPachete.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]

    public final String[] capTabel = {"Nr.", "Timestamp",
            "IP Sursa", "IP Destinatie","Port Sursa",
            "Port Destinatie","Protocol", "Serviciu",
            "Lungime Pachet (Bytes)","Info"};

    private PcapNetworkInterface adr; //adr = adaptor de retea

    private PcapHandle handle = null;

    int nrPachete = 0;
    TabelPachete tabelPachete = null;
    int timpCaptura = 60; //in secunde

    Timer timer = new Timer();

    private AnalizatorPachete(PcapNetworkInterface adr){
        this.adr = adr;
        arataTabel();
    }

    void arataTabel(){
        String[][] tabel = {};
        tabelPachete = new TabelPachete(tabel, capTabel, adr);
    }

    void start(){
        try{
            handle = adr.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            startTimer();
            handle.loop(COUNT, listener);
        }
        catch(PcapNativeException | NotOpenException | InterruptedException e){
            e.printStackTrace();
        } finally{
            handle.close();
            timer.cancel();
        }
    }


    PacketListener listener
            = new PacketListener() {
        @Override
        public void gotPacket(Packet pachet) {
            try {
                printeazaPachet(pachet, handle.getTimestamp().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    synchronized private void incrementNr(){
        nrPachete++;
    }

    synchronized private int getNrPachete(){
        return nrPachete;
    }

    public void printeazaPachet(Packet pachet, String timestamp) throws IOException {
        String srsIP, desIP;
        int srsPort=0;
        int desPort=0;
        String info;

        if(!pachet.contains(IpPacket.class)){
            return;
        }
        IpPacket ipPachet = pachet.get(IpPacket.class);

        srsIP = ipPachet.getHeader().getSrcAddr().getHostAddress();
        desIP = ipPachet.getHeader().getDstAddr().getHostAddress();

        String protocol = ipPachet.getHeader().getProtocol().name();

        if (protocol.equals("TCP") ){
            TcpPacket tcpPct = pachet.get(TcpPacket.class);
            srsPort = tcpPct.getHeader().getSrcPort().valueAsInt();
            desPort = tcpPct.getHeader().getDstPort().valueAsInt();
        }else if (protocol.equals("UDP") ){
            UdpPacket udpPct = pachet.get(UdpPacket.class);
            srsPort = udpPct.getHeader().getSrcPort().valueAsInt();
            desPort = udpPct.getHeader().getDstPort().valueAsInt();
        }else{
            return;
        }
        int lungime = pachet.length();

        info = pachet.toString();
        incrementNr();

        String serviciu = new ServiciuResolver().numeServiciu(String.valueOf(srsPort),
                String.valueOf(desPort), protocol);

        tabelPachete.model.adaugaInfo(info);
        tabelPachete.model.adauga(String.valueOf(getNrPachete()), timestamp, srsIP,
                desIP, String.valueOf(srsPort), String.valueOf(desPort),
                protocol, String.valueOf(lungime), String.valueOf(info), serviciu);
    }



    public void stop(){
        try {
            handle.breakLoop();

        } catch (NotOpenException e){
            e.printStackTrace();
        }
    }

    void seteazaTimp(int t){
        timpCaptura = t > 0 ? t : 60;
    }

    void startTimer(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        }, timpCaptura* 1000L);
    }

    public static void main(String[] args) throws Exception {

        PcapNetworkInterface adr;

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch( Exception ex ) {
            ex.printStackTrace();
        }

        ADRSelector selector = new ADRSelector();
        selector.selector();

        adr = selector.getAdrSelectat();

        AnalizatorPachete analizatorPachete = new AnalizatorPachete(adr);

        analizatorPachete.seteazaTimp(selector.getTimpSetat());

        analizatorPachete.start();
    }
}
