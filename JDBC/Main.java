package JDBC;

import java.sql.*;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/Java_Lab";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Surgearrest1";

    public static void main(String[] args) throws SQLException {
        SimpleDataSource dataSource = new SimpleDataSource();
        Connection connection = dataSource.openConnection(URL, USER, PASSWORD);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select" +
                " * from student");

        while (resultSet.next()) {
            System.out.println("ID " + resultSet.getInt("id"));
            System.out.println("First name " + resultSet.getString("first_name"));
            System.out.println("Second name " + resultSet.getString("second_name"));
            System.out.println("Age " + resultSet.getInt("age"));
            System.out.println("Group number " + resultSet.getInt("group_number"));
        }

        System.out.println("-------------------------------------------");
        resultSet.close();
        resultSet = statement.executeQuery("select s.id as s_id, *\n" +
                "from student s\n" +
                "         inner join mentor m on s.id  = m.student_id;\n");
        connection.close();

        while (resultSet.next()) {
            System.out.println("ID " + resultSet.getInt("s_id"));
        }

    }
}
