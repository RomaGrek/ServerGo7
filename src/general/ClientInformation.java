package general;

public class ClientInformation {
    static String host = "localhost";
    static int port = 8000;

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        ClientInformation.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ClientInformation.port = port;
    }
}
