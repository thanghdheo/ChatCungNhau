/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thanh
 */
public class WorkerServer implements Runnable {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private String user = null;
    public String user2 = null;
    private String roomId = null;
    public WorkerServer workerUser2;

    public WorkerServer(Socket s) throws IOException, Exception {
        this.socket = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    @Override
    public void run() {
         System.out.println("Client " + socket.toString() + " accepted");
        try {
            while (true) {
                try {
                    switch (in.readLine()) {
                        case "LOGIN":
                            login();
                            break;
                        case "CHAT":
                            gochat();
                            break;
                        case "CANCEL":
                            cancleRoom();
                            break;
                        case "GHEPCAP":
                            ghepcap();
                            break;
                        case "DONGYVAOPHONG":
                            kiemtra();
                            break;
                        case "LOADCHAT":
                            chuanbichat();
                            break;
                        case "MESSAGE":
                            hanldemessage();
                            break;
                        case "OUTCHAT":
                            outchat();
                            break;
                        default: break;
                    }
                } catch (Exception ex) {
                    break;
                }
            }
            clean();
            in.close();
            out.close();
            socket.close();
            System.out.println("Đóng");
        } catch (IOException ex) {
            Logger.getLogger(WorkerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void login() throws IOException {
        String username = in.readLine();
        boolean flag = true;
        // kiểm tra user có online chưa
        for (String usr : ServerBUS.users) {
            if (usr.equals(username)) {
                flag = false;
                break;
            }
        }

        if (flag) {
            ServerBUS.users.add(username);
            user = username;
            System.out.println(user + " đăng nhập thành công");
            out.write("SUCCESS" + "\n");
        } else {
            System.out.println("Tài khoản đã đăng nhập");
            out.write("FAILED" + "\n");
        }
        out.flush();

    }

    private void gochat() {
        try {
            if (ServerBUS.users_waitting.add(user)) {
                System.out.println( user + " vào phòng chờ");
                out.write("SUCCESS" + "\n");
                out.flush();
                int size = ServerBUS.users_waitting.size();
        
                if (size == 2) {
                    sendRoomID(0, 1);
                } 
            } else {
                out.write("FAILED" + "\n");
                out.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

  
    private void sendRoomID(int user1, int user2) throws IOException {
        String usr1 = ServerBUS.users_waitting.get(user1);
        String usr2 = ServerBUS.users_waitting.get(user2);
        System.out.println("Người chat 1 :" +usr1);
        System.out.println("Người chat 2 :" +usr2);

        Room waittingRoom = new Room();
        String id = "roomID-" + (new Random().nextInt(89999999) + 10000000);
        waittingRoom.setRoomID(id);
        System.out.println("Room id: " + id);
        for (WorkerServer worker : ServerBUS.workers) {
            String usr = worker.user;
            System.out.println("");
            if (usr.equals(usr1)) {
                System.out.println("Gửi accept user " + usr1);
                sendAccept(worker, id);
                waittingRoom.setUser1(worker.user);
            }
            if (usr.equals(usr2)){
                System.out.println("Gửi accept user " + usr2);
                sendAccept(worker, id);
                waittingRoom.setUser2(worker.user);
            }
        }
        ServerBUS.waittingRooms.add(waittingRoom);
    }
    
    private void sendAccept(WorkerServer worker, String roomId) throws IOException {
        worker.out.write("GOCHAT" + "\n");
        worker.out.write(roomId + "\n");
        worker.out.flush();
        System.out.println("Đã gửi mã phòng");
        ServerBUS.users_waitting.remove(worker.user);
    }
    
     private void cancleRoom() {
        try {
            if (ServerBUS.users_waitting.remove(user)) {
                System.out.println(user + " đã thoát phòng chờ");
                out.write("SUCCESS" + "\n");
            } else {
                out.write("FAILED" + "\n");
            }
            out.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void kiemtra() throws IOException {
        System.out.println("Bắt đầu kiểm tra");
        switch (in.readLine()) {
            case "SUCCESS":
                acceptRoom();
                break;
            case "FAILED":
                denyRoom();
                break;
        }
    }
    
    private void acceptRoom() throws IOException {
        roomId = in.readLine();
                // kiểm tra user nào click accept bằng roomid
                for (Room r : ServerBUS.waittingRooms) {
                    if (r.getRoomID().equals(roomId)) {
                        int currentUser = 0;
                        if (r.getUser1().equals(user)) {
                            currentUser = 1;
                            r.setUser1Accept(r.ACCEPT);
                            user2 = r.getUser2();
                            System.out.println("user " + user + " accept");
                        } else {
                            r.setUser2Accept(r.ACCEPT);
                            System.out.println("user " + user + " accept");
                            user2 = r.getUser1();
                        }

                        // khi user này accept mà có 1 user deny trước đó
                        if (r.getUser1Accept() == r.DENY || r.getUser2Accept() == r.DENY) {
                            if (ServerBUS.waittingRooms.remove(r) && ServerBUS.users_waitting.add(user)) {
                                System.out.println("Huỷ room: " + r.getRoomID());
                                System.out.println("Cho người dùng " + user + " vào lại hàng chờ");
                                out.write("OUTROOM" + "\n");
                                out.flush();
                                roomId = null;
                                user2 = null;
                                int size = ServerBUS.users_waitting.size();
        
                                if (size == 2) {
                                    sendRoomID(0, 1);
                                } 
                            }
                        }

                        // cuối
                        if (r.getUser1Accept() == r.ACCEPT && r.getUser2Accept() == r.ACCEPT) {
                            System.out.println("2 user accept");
                            if (currentUser == 1) {
                                for (WorkerServer worker : ServerBUS.workers) {
                                    if (worker.user.equals(r.getUser2())) {
                                        openChat(worker);
                                        break;
                                    }
                                }
                            } else {
                                for (WorkerServer worker : ServerBUS.workers) {
                                    if (worker.user.equals(r.getUser1())) {
                                        openChat(worker);
                                        break;
                                    }
                                }
                            }
                            ServerBUS.waittingRooms.remove(r);
                        }
                        break;
                    }
                }
    }

    private void openChat(WorkerServer worker) throws IOException {
        out.write("LOADCHAT" + "\n");
        out.flush();
        worker.out.write("LOADCHAT" + "\n");
        worker.out.flush();
        ServerBUS.chatWorkers.add(this);
        ServerBUS.chatWorkers.add(worker);
    }

    private void denyRoom() throws IOException {
        roomId = in.readLine();
        // kiểm tra user nào click deny bằng roomid
        for (Room r : ServerBUS.waittingRooms) {
            if (r.getRoomID().equals(roomId)) {
                if (r.getUser1().equals(user)) {
                    r.setUser1Accept(r.DENY);
                    System.out.println("user " + user + " deny");

                    // kiểm tra user trước đó nhấn accept
                    if (r.getUser2Accept() == r.ACCEPT) {
                        for (WorkerServer worker : ServerBUS.workers) {
                            if (worker.user.equals(r.getUser2())) {
                                System.out.println("Huỷ room: " + r.getRoomID());
                                ServerBUS.waittingRooms.remove(r);
                                sendCancle(worker);
                                break;
                            }
                        }
                    }
                } else {
                    r.setUser2Accept(r.DENY);
                    System.out.println("user " + user + " deny");

                    // kiểm tra user trước đó nhấn accept
                    if (r.getUser1Accept() == r.ACCEPT) {
                        for (WorkerServer worker : ServerBUS.workers) {
                            if (worker.user.equals(r.getUser1())) {
                                System.out.println("Huỷ room: " + r.getRoomID());
                                ServerBUS.waittingRooms.remove(r);
                                sendCancle(worker);
                                break;
                            }
                        }
                    }
                }

                System.out.println("Người dùng " + user + " đã thoát phòng chờ");
                roomId = null;
                out.write("CANCEL" + "\n");
                out.flush();

                if (r.getUser1Accept() == r.DENY && r.getUser2Accept() == r.DENY) {
                    ServerBUS.waittingRooms.remove(r);
                    System.out.println("Huỷ room: " + r.getRoomID());
                }
                break;
            }
        }
    }

    private void sendCancle(WorkerServer worker) throws IOException {
        ServerBUS.users_waitting.add(worker.user);
        System.out.println(worker.user + " vào hàng chờ");
        worker.out.write("OUTROOM" +"\n");
        worker.out.flush();
        worker.out.write("GHEPCAP" +"\n");
        worker.out.flush();
        worker.roomId = null;
    }

    private void ghepcap() throws IOException {
        int size = ServerBUS.users_waitting.size();

        if (size == 2) {
            sendRoomID(0, 1);
        }
    }

    private void chuanbichat() throws IOException {
        System.out.println("Chuan bi chat");
        workerUser2 = layNguoiDung2();
        if (workerUser2 != null) {
            thongtinUser2();
            out.write("PREPARESUCCESS" + "\n");
            System.out.println(user + " hoàn tất!");
        } else {
            out.write("PREPAREFAILED" + "\n");
            System.out.println("Khởi tạo user " + user + " thất bại!");
        }
        out.flush();
    }

    private WorkerServer layNguoiDung2() {
        for (WorkerServer w : ServerBUS.chatWorkers)
            if (w.roomId.equals(roomId) && w.user.equals(user2))
                return w;
        return null;
    }

    private void thongtinUser2() throws IOException {
        out.write("INFOUSER" + "\n");
        out.write(user2 + "\n");
        out.flush();
    }

    private void hanldemessage() throws IOException {
        workerUser2.out.write("MESSAGE" + "\n");
        workerUser2.out.write(in.readLine() + "\n");
        workerUser2.out.flush();
    }

    private void outchat() throws IOException {
        System.out.println("Thoát phòng");
        workerUser2.out.write("OUTCHATNOTI" + "\n");
        workerUser2.out.flush();
        clear();
    }

    private void clear() {
        workerUser2 = null;
        user2 = null;
        roomId = null;
        ServerBUS.chatWorkers.remove(this);
    }
    
    private void clean() throws IOException {
        for (Room r : ServerBUS.waittingRooms) {
            if (r.getUser1().equals(user)) {
                r.setUser1Accept(r.DENY);
                System.out.println("user " + user + " disconnected game");
                break;
            } else if (r.getUser2().equals(user)) {
                r.setUser2Accept(r.DENY);
                System.out.println("user " + user + " disconnected game");
                break;
            }
        }
        ServerBUS.users_waitting.remove(user);
        ServerBUS.users.remove(user);
        ServerBUS.workers.remove(this);
    }
    
}