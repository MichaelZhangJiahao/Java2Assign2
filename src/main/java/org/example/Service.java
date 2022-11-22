package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static javafx.application.Platform.exit;


public class Service implements Runnable {
    Socket socket1;
    Socket socket2;
    Scanner in1;
    PrintWriter out1;
    Scanner in2;
    PrintWriter out2;
    int[][] chessboard = new int[3][3];

    public Service(Socket socket1, Socket socket2) {
        this.socket1 = socket1;
        this.socket2 = socket2;
    }

    @Override
    public void run() {
        try {
            in1 = new Scanner(socket1.getInputStream());
            in2 = new Scanner(socket2.getInputStream());
            out1 = new PrintWriter(socket1.getOutputStream());
            out2 = new PrintWriter(socket2.getOutputStream());
            process();
        } catch (IOException e) {
            System.out.println("Run Error");
        } catch (NoSuchElementException e) {
            System.out.println("Crash");
        }
    }

    public void process() {
        int player = 1;
        int x;
        int y;
        try {
            while (true) {
                if (player == 1) {
                    String str = in1.nextLine();
                    if (str.equals("crash")) {
                        System.out.println("Crash");
                        exit();
                        System.exit(-1);
                    }
                    x = Integer.parseInt(str);
                    y = Integer.parseInt(in1.nextLine());
                    chessboard[x][y] = 1;
                    out2.println(x);
                    out2.flush();
                    out2.println(y);
                    out2.flush();
                } else {
                    String str = in2.nextLine();
                    if (str.equals("crash")) {
                        System.out.println("Crash");
                        exit();
                        System.exit(-1);
                    }
                    x = Integer.parseInt(str);
                    y = Integer.parseInt(in2.nextLine());
                    chessboard[x][y] = -1;
                    out1.println(x);
                    out1.flush();
                    out1.println(y);
                    out1.flush();
                }
                player = -player;
            }
        } catch (NoSuchElementException e) {
            System.out.println("Crash");
        }

    }
}