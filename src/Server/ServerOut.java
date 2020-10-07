package Server;

import commands.Command;
import general.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;

public class ServerOut implements Runnable{
    SocketChannel socket = null;
    User user = null;
    Command command = null;

    public ServerOut(SocketChannel socket, User user, Command command) {
        this.socket = socket;
        this.user = user;
        this.command = command;
    }

    @Override
    public void run(){
        try (ObjectOutputStream toClient = new ObjectOutputStream(socket.socket().getOutputStream())) {
            toClient.writeObject(command);
        }catch (IOException e) {
            e.printStackTrace();
        }
}
}
