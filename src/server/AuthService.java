package server;

import java.sql.*;

public class AuthService {
    private static Connection connection; // соед с базой данных
    private static Statement statement; // состояние соединения

    public static void connect(){
        try {
//резервируем класс, с кот. б. работать sqlite
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String getNicknameByLoginAndPassword(String login, String password) {
        String query = String.format("select nickname from users where login = '%s' and password = '%s'", login, password); //запрос

        try {
            ResultSet rs = statement.executeQuery(query);// отправляем запрос
            if(rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
