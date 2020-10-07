package commands;

import dataBase.DataBase;
import general.GeneralCollection;
import general.HumanBeing;
import general.HumanBeingReader;


/**
 * Класс, реализующий программу insert key, которая добавляет новый элемент с заданным ключом
 */
public class Insert extends Command {
    private Integer key;
    private HumanBeing humanBeing;
    /**
     * Метод реалезует проверку на наличие обьекта с таким ключем в коллекции, и если такого ключа нету, то добавляет новый обьект в коллекцию
     * @param generalCollection класс с коллекцией, над которой производятся действия
     */
    @Override
    public void doing(GeneralCollection generalCollection) {
        if (!DataBase.existingKey(key)) {   // нужно что бы ключ был не нулевой и isValidCommand выполнялась первее чем ран111
            generalCollection.addHumanBeing(key, humanBeing);
            setMessage("Элемент " + humanBeing.toString() + " добавлен в коллекцию");
        } else {
            setMessage("Элемент с таким ключем уже есть в коллекции");
        }
    }
    /** Метод, который проверяет, что id целое число
     * @return
     */
    @Override
    public boolean isValidCommand(){
        try {
            key = Integer.parseInt(getValue());
            HumanBeingReader humanBeingReader = new HumanBeingReader();
            humanBeing = humanBeingReader.getHumanBeing();
            return true;
        }catch (NumberFormatException e) {
            System.out.println("Ключ должен быть типа: Integer");
            return false;
        }
    }
}
