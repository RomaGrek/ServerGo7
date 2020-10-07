package commands;

import dataBase.*;
import general.*;
import general.GeneralCollection;

public class LogIn extends Command{
    public LogIn(){
        this.setMessage("Войти");
    }

    @Override
    public void doing(GeneralCollection generalCollection) {
        User checkUser = DataBase.getUser(this.getUser().getUsername());
        if (checkUser == null) {
            this.setMessage("Такого имя пользователя не существует");
        } else if (!checkUser.getPassword().equals(getUser().getPassword())) {
            this.setMessage("Неверный пароль");
        } else {
            this.setMessage("Вход выполнен!");
        }
    }


    @Override
    public boolean isValidCommand() {
        return this.getMessage().equals("Вход выполнен!");
    }
}
