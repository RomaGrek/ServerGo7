package Server;

import commands.Command;
import general.GeneralCollection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerIn implements Runnable{
    SocketChannel socket = null;
    GeneralCollection generalCollection = null;
    static ExecutorService executeIt = Executors.newCachedThreadPool();


    public ServerIn(SocketChannel socket, GeneralCollection generalCollection) {
        this.socket = socket;
        this.generalCollection = generalCollection;
    }

    @Override
    public void run() {
        try (ObjectInputStream fromClient = new ObjectInputStream(socket.socket().getInputStream())){
            Command command = (Command) fromClient.readObject();
            executeIt.execute(new ServerOn(socket, generalCollection, command));
            Thread.sleep(500);
        }catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }try{
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
