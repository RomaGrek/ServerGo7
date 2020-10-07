package Client;

import general.ClientInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class ClientGo {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Введите порт сервера");
            int port = Integer.parseInt(bufferedReader.readLine().trim());
            ClientInformation.setPort(port);
            System.out.println("Введите имя хоста");
            String host =bufferedReader.readLine().trim();
            ClientInformation.setHost(host);
            Client.run();
        }catch (NumberFormatException e){
            System.out.println("Невалидный порт");
            System.exit(0);
        } catch(UnknownHostException e){
            System.out.println("IP для этого хоста не найден");
            System.exit(0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
