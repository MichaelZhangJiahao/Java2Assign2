package main.java.org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("Waiting clients...");
        while (true) {
            Socket socket1 = serverSocket.accept();
            PrintWriter out1 = new PrintWriter(socket1.getOutputStream());
            System.out.println("A client enter the game.");
            out1.println(1);
            out1.flush();
            out1.println("Please wait");
            out1.flush();
            Socket socket2 = serverSocket.accept();
            PrintWriter out2 = new PrintWriter(socket2.getOutputStream());
            System.out.println("Server game start");
            out1.println("Player O game Start.");
            out1.flush();
            out2.println(-1);
            out2.flush();
            out2.println("Player X game Start.");
            out2.flush();
            new Thread(new Service(socket1, socket2)).start();
        }
    }
}
