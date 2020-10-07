package Server;

import dataBase.DataBase;
import general.GeneralCollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ForkJoinPool;

public class ServerGo {
    static ForkJoinPool executer = new ForkJoinPool();

    public static void main(String[] args) throws IOException {
        System.out.println("Для запуска сервера нажмите Enter.");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String mode = bufferedReader.readLine().trim().toLowerCase();
        if (mode.equals("newdb")) {
            DataBase.createTable();
            System.exit(0);
        }
        if (mode.equals("cleardb")) {
            DataBase.clear();
            System.exit(0);
        }
        if(mode.equals("")) {
            System.out.println("Введите порт:");
            String line = bufferedReader.readLine();
            int port = Integer.parseInt(line);
            System.out.println("Сервер запущен");
            GeneralCollection generalCollection = new GeneralCollection();
            SocketAddress adress = new InetSocketAddress(port);
            while (true) {
                try (ServerSocketChannel channel = ServerSocketChannel.open()) {
                    channel.bind(adress);
                    SocketChannel socket = channel.accept();
                    if (socket.isOpen()) {
                        executer.execute(new ServerIn(socket, generalCollection));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
