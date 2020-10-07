package commands;

import Client.Client;
import Client.ClientCommandPusk;
import general.GeneralCollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ExecuteScript extends Command {
    private ClientCommandPusk commandData;

    private ArrayList<String> workingScripts;

    public ExecuteScript(ArrayList<String> workingScripts) {
        this.commandData = new ClientCommandPusk(true);
        this.workingScripts = workingScripts;
    }

    public ExecuteScript() {
        this.commandData = new ClientCommandPusk(true);
        this.workingScripts = new ArrayList<>();
    }

    public void doing(GeneralCollection humanBeingMap) {}

    public boolean isValidCommand() {
        String fileName = getValue();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            try {
                this.workingScripts.add("execute_script " + fileName);
                String input = bufferedReader.readLine();
                while (input != null) {
                    if (input.contains("execute_script")) {
                        if (this.workingScripts.contains(input)) {
                            System.out.println(input.split(" ")[1] + " is already working. Command skipped.\n");
                        } else {
                            Command command = new ExecuteScript(this.workingScripts);
                            System.out.println(input.split(" ")[1]);
                            command.setValue(input.split(" ")[1]);
                            command.setUser(getUser());
                            command.isValidCommand();
                        }
                    } else {
                        this.commandData.setCommand(input);
                        Command command = this.commandData.getCommand();
                        command.setUser(getUser());
                        command.isValidCommand();
                        command = Client.exchangeCommands(command);
                        System.out.println(command.getMessage());
                        if (command.getMessage().contains("Logging out"))
                            System.exit(0);
                    }
                    input = bufferedReader.readLine();
                }
                bufferedReader.close();
            } catch (Throwable throwable) {
                try {
                    bufferedReader.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
        } catch (IOException ex) {
            System.out.println("Файл " + fileName + " не найден или доступ к нему заблокирован.\n");
        } catch (Exception e) {
            System.out.println("Невалидная строка в execute_script " + fileName + "\n");
            e.printStackTrace();
        }
        System.out.println("скрипт " + fileName + " закончил работать");
        setMessage("script");
        this.workingScripts.remove("execute_script " + fileName);
        return true;
    }
}
