package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addUser(String login, String pass, String nickname) {
        try {
            String query = "INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, login);
            ps.setInt(2, pass.hashCode());
            ps.setString(3, nickname);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getNicknameByLoginAndPass(String login, String pass) {
        String query = String.format("select nickname, password from users where login='%s'", login);
        try {
            ResultSet rs = statement.executeQuery(query); // возвращает выборку через select
            int myHash = pass.hashCode();
            // кеш числа 12345
            // изменим пароли в ДБ на хеш от строки pass1

            if (rs.next()) {
                String nick = rs.getString(1);
                int dbHash = rs.getInt(2);
                if (myHash == dbHash) {
                    return nick;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int addToBlackList(String owner, String blackClient){
        PreparedStatement ps = null;
        try{
            ps = connection.prepareStatement("INSERT INTO blackList (owner, black) VALUES (?, ?)");
            ps.setString(1, owner);
            ps.setString(2, blackClient);
            return ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            statementClose(ps);
        } return 0;
    }

    public static int deleteFromBlackList(String owner, String blackClient){
        PreparedStatement ps = null;
        try{
            ps = connection.prepareStatement("DELETE FROM blackList WHERE owner = ? AND black = ?");
            ps.setString(1, owner);
            ps.setString(2, blackClient);
            return ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            statementClose(ps);
        } return 0;
    }

    private static void statementClose(PreparedStatement ps) {
        try {
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getBlackListByNickname(String nickname){
        List<String> blackList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement("SELECT * FROM blackList WHERE owner = ?");
            ps.setString(1, nickname);
            rs = ps.executeQuery();
            while (rs.next()){
                blackList.add(rs.getString(2));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            resultSetClose(rs);
            statementClose(ps);
        } return blackList;
    }

//    public static List<String> history(String nickname){
//        List<String> history = new ArrayList<>();
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        try {
//            ps = connection.prepareStatement("INSERT INTO history (history) WHERE owner = ?");
//            ps.setString(1, forHistory);
//            rs = ps.executeQuery();
//            while (rs.next()){
//                history.add(rs.getString(2));
//            }
//        } catch (SQLException e){
//            e.printStackTrace();
//        } finally {
//            resultSetClose(rs);
//            statementClose(ps);
//        } return history;
//    }

    private static void resultSetClose(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}