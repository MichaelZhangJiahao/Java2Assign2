package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static javafx.application.Platform.exit;

public class Client extends Application {
    public Scanner in;

    public PrintWriter out;
    public int curPlayer;
    public int clickX;
    public int clickY;
    public Integer currentPlayer = 1;
    private boolean waiting = true;

    public static final int[][] chessBoard = new int[3][3];

    public static int[] board = new int[9];
    public static final boolean[][] flag = new boolean[3][3];

    public static final int PLAY_1 = 1;
    public static final int PLAY_2 = -1;
    public static final int EMPTY = 0;
    public static final int BOUND = 90;
    public static final int OFFSET = 15;

    public static int move_cnt = 0;

    public static int[] magicSquare = new int[]{4, 9, 2, 3, 5, 7, 8, 1, 6};

    public static boolean gameOver = false;

    public static void main(String[] args) throws IOException {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        try {
            Controller controller = new Controller();

            controller.game_panel.setOnMouseClicked(event -> {
                clickX = (int) (event.getX() / BOUND);
                clickY = (int) (event.getY() / BOUND);
                if (currentPlayer == curPlayer && clickX >= 0 && clickX <= 2
                        && clickY >= 0 && clickY <= 2) {
                    if (chessBoard[clickX][clickY] == EMPTY) {
                        refreshBoard(clickX, clickY, currentPlayer, controller);
                    }
                    waiting = false;
                }
            });

            InetAddress ip = InetAddress.getByName("localhost");
            Socket socket = new Socket(ip, 8888);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());

            new Thread(() -> {
                curPlayer = Integer.parseInt(in.nextLine());
                if (curPlayer == 1) {
                    System.out.println(in.nextLine());
                }
                System.out.println(in.nextLine());
                while (!gameOver) {
                    if (curPlayer == 1) {
                        try {
                            waitPlayer();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        sendMove();
                        try {
                            getMove(controller);
                        } catch (NoSuchElementException e) {
                            if (gameOver) {
                                exit();
                                System.exit(-1);
                            }
                            System.out.println("Crash");
                            exit();
                            System.exit(-1);
                        }

//                        System.out.println("Player X turn!");
                    } else if (curPlayer == -1) {
                        try {
                            getMove(controller);
                        } catch (NoSuchElementException e) {
                            if (gameOver) {
                                exit();
                                System.exit(-1);
                            }
                            System.out.println("Crash");
                            exit();
                            System.exit(-1);
                        }


                        try {
                            waitPlayer();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        sendMove();
//                        System.out.println("Player O turn!");
                    }
                }
            }).start();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("FileNotFoundException");
        } catch (IOException e) {
            String exceptionSimpleName = e.getCause().getClass().getSimpleName();
            if ("ClientAbortException".equals(exceptionSimpleName)) {
                System.out.println("ClientAbortException");
            } else {
                System.out.println("General IOException");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Crash");
            exit();
            System.exit(-1);
        }
    }

    @Override
    public void stop() {
//        System.out.println("Crash");
        out.println("crash");
        out.flush();
    }

    public int checkWinner() {
        if (hasWon(1)) {
            gameOver = true;
            return 1;
        } else if (hasWon(-1)) {
            gameOver = true;
            return -1;
        } else if (move_cnt == 9) {
            gameOver = true;
            return 0;
        } else {
            gameOver = false;
            return 100;
        }
    }

    public boolean hasWon(int x) {
//        System.out.println(Arrays.toString(board));
//        System.out.println(Arrays.deepToString(chessBoard));
        try {
            int cnt = 0;
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    board[cnt] = chessBoard[j][k];
                    cnt++;
                }
            }

            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    for (int k = 0; k < 9; k++)
                        if (i != j && i != k && j != k)
                            if (board[i] == x && board[j] == x && board[k] == x)
                                if (magicSquare[i] + magicSquare[j] + magicSquare[k] == 15)
                                    return true;
            return false;
        } catch (NoSuchElementException e) {
            System.out.println("Run error");
        }
        return false;
    }

    public void sendMove() {
//        System.out.println("sendMove: " + clickX + "," + clickY);
        out.println(clickX);
        out.flush();
        out.println(clickY);
        out.flush();
    }

    public void waitPlayer() throws InterruptedException {
        int cnt = 0;
        while (waiting) {
            Thread.sleep(100);
            cnt++;
            if (cnt >= 50) {
                System.out.println("Crash");
                System.exit(-1);
            }
//            System.out.println(66666666);
        }
        waiting = true;
    }

    public void getMove(Controller controller) {
        int x = in.nextInt();
        int y = in.nextInt();
//        System.out.println("getMove: " + x + "," + y);
        try {
            Platform.runLater(() -> refreshBoard(x, y, currentPlayer, controller));
        } catch (NoSuchElementException e) {
            System.out.println("Run error");
        }
    }

    private void refreshBoard(int x, int y, int curPlayer, Controller controller) {
        if (checkWinner() == 1) {
//            System.out.println("O win!");
            controller.label1.setText("O win!");
            exit();
//            System.exit(0);
        } else if (checkWinner() == -1) {
//            System.out.println("X win!");
            controller.label1.setText("X win!");
            exit();
//            System.exit(0);
        } else if (checkWinner() == 0) {
//            System.out.println("DRAW!");
            controller.label1.setText("DRAW!");
            exit();
//            System.exit(0);
        } else {
//            System.out.println("No winner yet");
        }

        if (curPlayer == 1) {
            chessBoard[x][y] = PLAY_1;
            controller.label1.setText("Player X's turn");
        } else {
            chessBoard[x][y] = PLAY_2;
            controller.label1.setText("Player O's turn");
        }
        drawChess(controller);
        currentPlayer = -curPlayer;
        move_cnt++;


        if (checkWinner() == 1) {
//            System.out.println("O win!");
            controller.label1.setText("O win!");
//            exit();
//            System.exit(0);
        } else if (checkWinner() == -1) {
//            System.out.println("X win!");
            controller.label1.setText("X win!");
//            exit();
//            System.exit(0);
        } else if (checkWinner() == 0) {
//            System.out.println("DRAW!");
            controller.label1.setText("DRAW!");
//            exit();
//            System.exit(0);
        } else {
//            System.out.println("No winner yet");
        }
    }

    private void drawChess(Controller controller) {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j, controller);
                        break;
                    case PLAY_2:
                        drawLine(i, j, controller);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle(int i, int j, Controller controller) {
        Circle circle = new Circle();
        controller.base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine(int i, int j, Controller controller) {
        Line lineA = new Line();
        Line lineB = new Line();
        controller.base_square.getChildren().add(lineA);
        controller.base_square.getChildren().add(lineB);
        lineA.setStartX(i * BOUND + OFFSET * 1.5);
        lineA.setStartY(j * BOUND + OFFSET * 1.5);
        lineA.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        lineA.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        lineA.setStroke(Color.BLUE);

        lineB.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        lineB.setStartY(j * BOUND + OFFSET * 1.5);
        lineB.setEndX(i * BOUND + OFFSET * 1.5);
        lineB.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        lineB.setStroke(Color.BLUE);
        flag[i][j] = true;
    }
}