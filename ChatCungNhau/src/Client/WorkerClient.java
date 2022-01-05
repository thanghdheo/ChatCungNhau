/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import static Client.Form.main;
import static Client.Form.start;
import Client.GUI.Main;
import Client.GUI.Start;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author thanh
 */
public class WorkerClient implements Runnable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    public static int status;
    public static boolean isContinue;

    public WorkerClient(Socket s) throws IOException {
        this.socket = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    @Override
    public void run() {
        while (true) {
            try {
                switch (in.readLine()) {
                    case "SUCCESS":
                        Success();
                        break;
                    case "FAILED":
                        Failed();
                        break;
                    case "GOCHAT":
                        GoChat();
                        break;
                    case "CANCEL":
                        cancleChat();
                        break;
                    case "OUTROOM":
                        outroom();
                        break;
                    case "GHEPCAP":
                        ghepCap();
                        break;
                    case "LOADCHAT":
                        loadChat();
                        break;
                    case "INFOUSER":
                        thongtinUser();
                        break;
                    case "PREPARESUCCESS":
                        chuanbichatsc();
                        break;
                    case "PREPAREFAILED":
                        chuanbichatfailed();
                        break;
                    case "MESSAGE":
                        hanldemessage();
                        break;
                    case "OUTCHATNOTI":
                        outchat();
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(WorkerClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void Success() {
        status = 1;
        isContinue = true;
    }

    private void Failed() {
        status = 0;
        isContinue = true;
    }

    private void GoChat() {
        try {
            String roomId = in.readLine();
            System.out.println("Room id la: " + roomId);

            TimeUnit.MILLISECONDS.sleep(2000);
            Start.flag = true;
            out.write("DONGYVAOPHONG" +"\n");
            if (JOptionPane.showConfirmDialog(null, "Chấp nhận vào phòng chat ?") == JOptionPane.YES_OPTION) {
                out.write("SUCCESS" + "\n");
                System.out.println("Chấp nhận vào phòng");

                Start.btnStart.setEnabled(false);

            } else {
                out.write("FAILED" + "\n");
                System.out.println("Từ chối vào phòng");
            }
            out.write(roomId + "\n"); // room id
            out.flush();
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ghepCap() throws IOException {
        out.write("GHEPCAP" + "\n");
        out.flush();
    }

    private void outroom() {
        Start.btnStart.setEnabled(true);
        Start.flag = false;
        Start.timecountdown();
        JOptionPane.showMessageDialog(null, "Phòng chat bị huỷ do đối phương không chấp nhận!!!");
    }

    private void cancleChat() {
        Start.btnStart.setText("TRÒ CHUYỆN NGAY !");
        Start.time = 0;
        Start.lbTime.setText("");
    }

    private void loadChat() throws IOException {
        System.out.println("Chuẩn bị");
        out.write("LOADCHAT" + "\n");
        out.flush();
        if (start != null) {
            start.setVisible(false);
        }
    }

    private void thongtinUser() throws IOException {
        BUS.user2 = in.readLine();
    }

    private void chuanbichatsc() {
        System.out.println("Vui lòng chờ");
        main = new Main();
        main.setVisible(true);
    }

    private void chuanbichatfailed() {
        JOptionPane.showMessageDialog(null, "Phòng chat đã bị huỷ do lỗi ");
        start = new Start();
        start.setVisible(true);
    }

    private void hanldemessage() throws IOException {
        Main.txMain.append(BUS.user2 + ": " + in.readLine() + "\n");
    }

    private void outchat() throws IOException {
        out.write("CLEAR" + "\n");
        out.flush();
        JOptionPane.showMessageDialog(null, "Bạn của bạn đã rời phòng !!! kết thúc!");
        if (main != null) {
            main.setVisible(false);
        }
        start = new Start();
        start.setVisible(true);
    }

}
