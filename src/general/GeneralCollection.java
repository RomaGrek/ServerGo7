package general;



import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Класс для дерева с объектами HumanBeing и его управлением
 */
public class GeneralCollection implements Serializable {
    LocalDateTime localDateTime;
    /**Поле genCollection, ключи - Integer, значения - HumanBeing*/
//    private TreeMap<Integer, HumanBeing> genCollection;
    private ConcurrentSkipListMap<Integer, HumanBeing> genCollection;

    /**
     * Чтение данных из файла HumanBeing.json генерация id происходит автоматически в диапазоне от 0 до 10000
     */
    public GeneralCollection() {
        this.localDateTime = LocalDateTime.now();
        this.genCollection = new ConcurrentSkipListMap<>();
    }



    /**
     * Удаляет все элементы из коллекции
     */
    public void clear() {
        if (genCollection.size() == 0) {
            System.out.println("Коллекция уже пуста");
        } else {
            genCollection.clear();
        }
    }


    /**
     * Переопределение метода toString
     * @return Строковое представление класса
     */
    @Override
    public String toString() {
        return "Тип: HumanBeing\n"
                + "Дата инициализации: " + localDateTime + '\n'
                + "Количество элементов: " + genCollection.size() + '\n';
    }

    /**
     * Удаляет элемент коллекции, ключ которого равен введенному
     * @param key ключ
     */
    public void removeKey(Integer key) {
        this.genCollection.remove(key);
    }

    public void setHumanBeingTreeMap(ConcurrentSkipListMap<Integer, HumanBeing> genCollection) {
        this.genCollection = genCollection;
    }

    /**
     * Геттер genCollection
     * @return возвращает коллекцию
     */
    public ConcurrentSkipListMap<Integer, HumanBeing>  getGenCollection() {
        return genCollection;
    }
    public void addHumanBeing(Integer key, HumanBeing humanBeing1) { // метода для создания нового обьекта и помещение его в коллекцию
        genCollection.put(key, humanBeing1);
    }


    public Integer getKeyById(Integer id){
        for (int key : genCollection.keySet()){
            if (Objects.equals(genCollection.get(key).getId(), id)){
                return key;
            }
        }
        return null;
    }

}


