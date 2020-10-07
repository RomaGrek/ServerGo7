package Client;

import commands.*;
import commands.LogIn;
import commands.Register;
import general.ClientInformation;
import general.User;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Класс Client для считывания команд, передачи их для выполнения на сервер и вывода результата выполнения
 */
public class Client {
    public static ClientCommandPusk CLIENT_COMMAND_DATA = new ClientCommandPusk(false);

    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static boolean loginUser(User user) throws IOException, ClassNotFoundException {
        LogIn login = new LogIn();
        login.setUser(user);
        Command command = exchangeCommands((Command)login);
        System.out.println(command.getMessage());
        return command.isValidCommand();
    }

    public static boolean registerUser(User user) throws IOException, ClassNotFoundException {
        Register register = new Register();
        register.setUser(user);
        Command command = exchangeCommands((Command)register);// <<<<--------вот тут походу не полностью проходит
        System.out.println(command.getMessage());
        return command.isValidCommand();
    }

    public static Command exchangeCommands(Command command) throws IOException, ClassNotFoundException {
        return exchangeCommands(command, InetAddress.getByName(ClientInformation.getHost()), ClientInformation.getPort());
    }

    public static Command exchangeCommands(Command command, InetAddress address, int port) throws IOException, ClassNotFoundException {
        Command command1 = null;
        try{
            Socket clientSocket = new Socket(address, port);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(command);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            command1 = (Command) in.readObject();
            in.close();
            if (command1.getMessage().contains("Завершение.")) {
                System.exit(0);
            }
        }catch (SocketException e) {
            e.printStackTrace();
        }
        return command1;
    }

    public static void run() throws IOException, ClassNotFoundException {
        System.out.println("Вы должны авторизоваться или войти\nДля авторизации введите -> login, для регистрации -> register");
        String line = "";
        boolean inputCorrect = false;
        while (!inputCorrect) {
            line = bufferedReader.readLine();
            if (line.toLowerCase().equals("login") || line.toLowerCase().equals("register")) {
                inputCorrect = true;
                continue;
            }
            if (line.toLowerCase().equals("exit")) {
                System.exit(0);
                continue;
            }
            System.out.println("Невалидный ввод. Для авторизации введите -> login, для регистрации -> register ");
        }
        String action = line;
        inputCorrect = false;
        line = "";
        while (!inputCorrect) {
            System.out.println("Введите логин, пожалуйста используйте только английские буквы и цифры");
            line = bufferedReader.readLine();
            if (Pattern.matches("^[a-zA-Z0-9]{1,}$", line))
                inputCorrect = true;
        }
        String username = line;
        inputCorrect = false;
        line = "";
        while (!inputCorrect) {
            System.out.println("Введите пароль, пожалуйста используйте только английские буквы и цифры");
            line = bufferedReader.readLine();
            if (Pattern.matches("^[a-zA-Z0-9]{1,}$", line))
                inputCorrect = true;
        }
        String password = encrypt(line);
        User user = new User(username, password);
        if (action.equals("register") &&
                !registerUser(user))
            System.exit(0);
        if (action.equals("login") &&
                !loginUser(user))
            System.exit(0);
        System.out.println("Приветствуем, " + username + "!");
        while (true) {
            System.out.println("Пожалуйста введите команду, что бы получить список всех команд, используйте команду \"help\".");
            line = bufferedReader.readLine();
            if (!line.isEmpty()) {
                CLIENT_COMMAND_DATA.setCommand(line);
                if (CLIENT_COMMAND_DATA.getCommand() != null) {
                    Command command = CLIENT_COMMAND_DATA.getCommand();
                    command.setUser(user);
                    if (command.isValidCommand() &&
                            !command.getMessage().equals("script")) {
                        command = exchangeCommands(command);
                        System.out.println(command.getMessage());
                        if (command.getMessage().contains("Logging out"))
                            System.exit(0);
                    }
                }
            }
        }
    }

    private static String encrypt(String password) {
        StringBuilder sha512 = new StringBuilder("");
        try {
            MessageDigest messageDigest = null;
            messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.reset();
            messageDigest.update(password.getBytes());
            byte[] digest = messageDigest.digest();
            BigInteger bigInteger = new BigInteger(1, digest);
            sha512 = new StringBuilder(bigInteger.toString(16));
            while (sha512.length() < 32)
                sha512.insert(0, "0");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha512.toString();
    }
}