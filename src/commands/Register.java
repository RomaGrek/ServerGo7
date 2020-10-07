package commands;

import dataBase.*;
import general.GeneralCollection;
import general.User;

public class Register extends Command {
    public Register() {
        setMessage("Зарегестрироваться");
    }

    public void doing(GeneralCollection generalCollection) {
        User checkUser = DataBase.getUser(getUser().getUsername());
        if (checkUser != null) {
            setMessage("Этот логин уже занят");
        } else {
            DataBase.createUser(getUser());
            setMessage("Регистрация выполнена");
        }
    }

    public boolean isValidCommand() {
        return getMessage().equals("Регистрация выполнена");
    }
}