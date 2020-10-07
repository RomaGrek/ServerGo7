package Server;

import commands.Command;
import dataBase.DataBase;
import general.GeneralCollection;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerOn implements Runnable {
    SocketChannel socket = null;
    GeneralCollection generalCollection = null;
    Command command = null;
    static ExecutorService executeIt = Executors.newCachedThreadPool();

    public ServerOn(SocketChannel socket, GeneralCollection generalCollection, Command command) {
        this.socket = socket;
        this.generalCollection = generalCollection;
        this.command = command;
    }

    @Override
    public void run() {
        if(command.getMessage().equals("Зарегестрироваться") || command.getMessage().equals("Войти")) {
            command.doing(generalCollection);
        }else{
            try {
                if (command.getForshowandinfo().equals("show")) {
                    generalCollection = DataBase.getShowData();
                } else if (command.getForshowandinfo().equals("info")){
                    generalCollection = DataBase.getShowData();
                } else {
                    generalCollection = DataBase.getDataByUser(command.getUser()); // тут можно сделать условие что если эьто команда шоу то показать все элементы коллекции которве есть
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            command.doing(generalCollection);
            if (!command.getForshowandinfo().equals("show") && !command.getForshowandinfo().equals("info")) {
                DataBase.saveDataByUser(command.getUser(), generalCollection);
            }
        }
        executeIt.execute(new ServerOut(socket, command.getUser(), command));
    }
}

