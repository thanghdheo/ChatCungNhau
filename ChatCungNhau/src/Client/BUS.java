/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import PUBLIC.Configuration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author thanh
 */
public class BUS {
    static Socket socket;
    public static BufferedWriter out;
    public static BufferedReader in;
    public static String user = null;
    public static String user2 = null;

    public static void connect() throws IOException {
        if (socket == null) {
            socket = new Socket(Configuration.SERVER, Configuration.PORT);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Executors.newSingleThreadExecutor().execute(new WorkerClient(socket));
            System.out.println("Client connected");
        }
    }
    
    public static boolean waiting() {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(500);
                if (WorkerClient.isContinue) {
                    
                    break;
                }
                System.out.println("waitting...");
            }
            WorkerClient.isContinue = false;
        } catch (InterruptedException ex) {
        }
        return true;
    }
    
    public static boolean RequestUser(String username){
         try {
            connect();
            out.write("LOGIN"+ '\n');
            out.write(username + "\n");
            out.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public static boolean RequestChat() {
        try {
            out.write("CHAT" + "\n");
            out.flush();
            System.out.println("tíc tóc");
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public static boolean RequestCancleChat(){
        try {
            out.write("CANCEL" + "\n");
            out.flush();
            System.out.println("Huỷ chờ");
             return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
