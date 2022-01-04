/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import PUBLIC.Configuration;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author thanh
 */
public class ServerBUS {
    public final int port;
    private static ServerSocket server = null;
    
    public static Vector<String> users = new Vector<>();
    public static Vector<String> users_waitting = new Vector<>();
    public static Vector<Room> waittingRooms = new Vector<>();
    public static Vector<WorkerServer> workers = new Vector<>();
    public static Vector<WorkerServer> chatWorkers = new Vector<>();

    public ServerBUS() throws IOException, Exception {
        port = Configuration.PORT;
        ExecutorService executor = Executors.newFixedThreadPool(Configuration.NUM_THREAD);

        try {
            server = new ServerSocket(port);
            System.out.println("Server binding at port " + port);
            System.out.println("Waiting for client...");
            while (true) {
                WorkerServer client = new WorkerServer(server.accept());
                executor.execute(client);
                workers.add(client);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }
    
}
