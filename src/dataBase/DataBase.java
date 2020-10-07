package dataBase;

import Server.*;
import general.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class DataBase {
    public static final String DATA_BASE_URL = "jdbc:postgresql://pg:5432/studs";
    public static final String USER = "s285686";
    public static final String PASSWORD = "rri035";
    public static void createTable() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            System.out.println("Соединение успешно!");
            connection.setAutoCommit(false); // выключаем режим автокомит, повышаем произодительноть и тд
            Statement statement = connection.createStatement(); // класс Statment нужен для выполнения базовых sql запросов
            String sql = "CREATE SEQUENCE abubanditos_users_sequence INCREMENT BY 1;" +
                    "CREATE SEQUENCE abubanditos_human_beings_sequence INCREMENT BY 1;" +
                    "CREATE TABLE abubanditos_users (ID int PRIMARY KEY NOT NULL, USERNAME TEXT NOT NULL, PASSWORD TEXT);" +
                    "CREATE TABLE abubanditos_human_beings " +
                    "(KEY int PRIMARY KEY  NOT NULL," +
                    "ID            INTEGER NOT NULL," +
                    "NAME          TEXT    NOT NULL," +
                    "COORDINATE_X  INTEGER NOT NULL," +
                    "CREATION_DATE TIMESTAMP       ," +
                    "COORDINATE_Y  INTEGER NOT NULL," +
                    "REAL_HERO     boolean NOT NULL," +
                    "HAS_TOOTHPICK boolean NOT NULL," +
                    "IMPACT_SPEED  int     NOT NULL," +
                    "WEAPON_TYPE   TEXT            ," +
                    "MOOD          TEXT            ," +
                    "CAR_NAME      TEXT    NOT NULL," +
                    "USER_ID       int     NOT NULL)";
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Разъеденение успешно!");

        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
    }

    public static void createUser(User user) {   // создаем юзера
        try {
            user.setId(getUserId()); // даем юзеру id
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);

            String sql = "INSERT INTO abubanditos_users (ID, USERNAME, PASSWORD) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.executeUpdate();
            System.out.println("Пользователь добавлен!");
            preparedStatement.close();

            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
    }

    public static User getUser(String username) {
        User user = null;
        try{
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);
            String sql = "SELECT * FROM abubanditos_users WHERE username LIKE ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
            }
            preparedStatement.close();

            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
        return user;
    }
    private static int getUserId() {
        int id = 0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);
            String sql = "SELECT nextval('abubanditos_users_sequence')";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            preparedStatement.close();

            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
        return id;
    }
    public static GeneralCollection getShowData() {
        GeneralCollection generalCollection = new GeneralCollection();
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);
            String sql = "SELECT * FROM abubanditos_human_beings";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet =   preparedStatement.executeQuery();
            HumanBeing humanBeing;
            while (resultSet.next()) {
                Integer key = resultSet.getInt("KEY");
                Integer id = resultSet.getInt("ID");
                String name = resultSet.getString("NAME");
                Integer coordinateX = resultSet.getInt("COORDINATE_X");
                Long coordinateY = resultSet.getLong("COORDINATE_Y");
                LocalDateTime creationDate = resultSet.getTimestamp("CREATION_DATE").toLocalDateTime();
                Boolean isRealHero = resultSet.getBoolean("REAL_HERO");
                boolean hasToothpick = resultSet.getBoolean("HAS_TOOTHPICK");
                int impactSpeed = resultSet.getInt("IMPACT_SPEED");
                WeaponType weaponType;
                if (resultSet.getString("WEAPON_TYPE").equals("null")) {
                    weaponType = null;
                }else {
                    weaponType = WeaponType.valueOf(resultSet.getString("WEAPON_TYPE"));
                }
                Mood mood;
                if (resultSet.getString("MOOD").equals("null")) {
                    mood = null;
                }else {
                    mood = Mood.valueOf(resultSet.getString("MOOD"));
                }
                String carName = resultSet.getString("CAR_NAME");

                Coordinates coordinates = new Coordinates(coordinateX, coordinateY);
                Car car = carName.equals("") ? null : new Car(carName);

                humanBeing = new HumanBeing(name, coordinates, isRealHero, hasToothpick, impactSpeed, weaponType, mood, car);
                humanBeing.setDate(creationDate);
                humanBeing.setId(id);

                generalCollection.addHumanBeing(key, humanBeing);
            }
            preparedStatement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
        return generalCollection;
    }
    public static GeneralCollection getDataByUser(User user) throws IOException {
        GeneralCollection generalCollection = new GeneralCollection();
        user = getUser(user.getUsername());
        int userID = user.getId();
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);
            String sql = "SELECT * FROM abubanditos_human_beings WHERE USER_ID = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet =   preparedStatement.executeQuery();
            HumanBeing humanBeing;

            while (resultSet.next()) {
                Integer key = resultSet.getInt("KEY");
                Integer id = resultSet.getInt("ID");
                String name = resultSet.getString("NAME");
                Integer coordinateX = resultSet.getInt("COORDINATE_X");
                Long coordinateY = resultSet.getLong("COORDINATE_Y");
                LocalDateTime creationDate = resultSet.getTimestamp("CREATION_DATE").toLocalDateTime();
                Boolean isRealHero = resultSet.getBoolean("REAL_HERO");
                boolean hasToothpick = resultSet.getBoolean("HAS_TOOTHPICK");
                int impactSpeed = resultSet.getInt("IMPACT_SPEED");
                WeaponType weaponType;
                if (resultSet.getString("WEAPON_TYPE").equals("null")) {
                    weaponType = null;
                }else {
                    weaponType = WeaponType.valueOf(resultSet.getString("WEAPON_TYPE"));
                }
                Mood mood;
                if (resultSet.getString("MOOD").equals("null")) {
                    mood = null;
                }else {
                    mood = Mood.valueOf(resultSet.getString("MOOD"));
                }
                String carName = resultSet.getString("CAR_NAME");
                Car car = new Car(carName);
                Coordinates coordinates = new Coordinates(coordinateX, coordinateY);

                humanBeing = new HumanBeing(name, coordinates, isRealHero, hasToothpick, impactSpeed, weaponType, mood, car);
                humanBeing.setDate(creationDate);
                humanBeing.setId(id);

                generalCollection.addHumanBeing(key, humanBeing);
            }
            preparedStatement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
        return generalCollection;
    }

    public static void saveDataByUser(User user, GeneralCollection generalCollection) {
        user = getUser(user.getUsername());
        int userID = user.getId();
        Set<Integer> oldKeys= getKeysbyUser(userID);
        ConcurrentSkipListMap<Integer, HumanBeing> currentMap = generalCollection.getGenCollection();
        try {
            for (Iterator<Integer> iterator = currentMap.keySet().iterator(); iterator.hasNext(); ) {
                PreparedStatement preparedStatement;
                int key = (iterator.next());
                Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
                connection.setAutoCommit(false);
                String sql;
                HumanBeing humanBeing = currentMap.get(key);
                if (oldKeys.contains(key)) {
                    sql = "INSERT INTO abubanditos_human_beings" +
                            "(KEY, ID, NAME, COORDINATE_X, COORDINATE_Y, CREATION_DATE, " +
                            "REAL_HERO, HAS_TOOTHPICK, IMPACT_SPEED, WEAPON_TYPE, MOOD, CAR_NAME, USER_ID)" +
                            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, key);
                    preparedStatement.setInt(2, humanBeing.getId());
                    preparedStatement.setString(3, humanBeing.getName());
                    preparedStatement.setInt(4, humanBeing.getX());
                    preparedStatement.setLong(5, humanBeing.getY());
                    preparedStatement.setTimestamp(6, Timestamp.valueOf(humanBeing.getCreationDate()));
                    preparedStatement.setBoolean(7, humanBeing.getRealHero());
                    preparedStatement.setBoolean(8, humanBeing.getHasToothpick());
                    preparedStatement.setInt(9, humanBeing.getImpactSpeed());
                    if(humanBeing.getWeaponType() == null) {
                        preparedStatement.setString(10,"null");
                    }else {
                        preparedStatement.setString(10, humanBeing.getWeaponType().toString());
                    }
                    if(humanBeing.getMood() == null) {
                        preparedStatement.setString(11,"null");
                    }else {
                        preparedStatement.setString(11, humanBeing.getMood().toString());
                    }
                    preparedStatement.setString(12, humanBeing.getCar().getName());
                    preparedStatement.setInt(13, userID);
                } else {
                    sql = "INSERT INTO abubanditos_human_beings" +
                            "(KEY, ID, NAME, COORDINATE_X, COORDINATE_Y, CREATION_DATE, " +
                            "REAL_HERO, HAS_TOOTHPICK, IMPACT_SPEED, WEAPON_TYPE, MOOD, CAR_NAME, USER_ID)" +
                            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, key);
                    preparedStatement.setInt(2, humanBeing.getId());
                    preparedStatement.setString(3, humanBeing.getName());
                    preparedStatement.setInt(4, humanBeing.getX());
                    preparedStatement.setLong(5, humanBeing.getY());
                    preparedStatement.setTimestamp(6, Timestamp.valueOf(humanBeing.getCreationDate()));
                    preparedStatement.setBoolean(7, humanBeing.getRealHero());
                    preparedStatement.setBoolean(8, humanBeing.getHasToothpick());
                    preparedStatement.setInt(9, humanBeing.getImpactSpeed());
                    if (humanBeing.getWeaponType() == null) {
                        preparedStatement.setString(10, "null");
                    }else{
                        preparedStatement.setString(10, humanBeing.getWeaponType().toString());
                    }
                    if (humanBeing.getMood() == null) {
                        preparedStatement.setString(11, "null");
                    }else{
                        preparedStatement.setString(11, humanBeing.getMood().toString());
                    }
                    preparedStatement.setString(12, humanBeing.getCar().getName());
                    preparedStatement.setInt(13, userID);
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.commit();
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
    }

    private static Set<Integer> getKeysbyUser(int userID) {
        Set<Integer> Keys = new HashSet<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);
            String sql = "SELECT * FROM abubanditos_human_beings WHERE USER_ID = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Keys.add(resultSet.getInt("KEY"));
            }
            preparedStatement.close();

            connection.commit();

            sql = "DELETE FROM abubanditos_human_beings WHERE USER_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
        return Keys;
    }

    public static void clear() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);
            String sql = "DROP TABLE abubanditos_users, abubanditos_human_beings;\n" +
                    "DROP SEQUENCE abubanditos_users_sequence, abubanditos_human_beings_sequence;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
    }
    public static boolean existingKey(int key) {
        Boolean answer = false;
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", "s285686", "rri035");
            connection.setAutoCommit(false);
            String sql = "SELECT EXISTS(SELECT * FROM abubanditos_human_beings WHERE KEY = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                answer = resultSet.getBoolean("exists");
            }
            preparedStatement.close();
            connection.commit();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL ошибка");
        }
        return answer;
    }
}