import JDBC.SimpleDataSource;
import Repositories.StudentsRepository;
import Repositories.StudentsRepositoryJDBCImpl;
import models.Student;

import java.sql.*;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/Java_Lab";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Surgearrest1";

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(URL,USER,PASSWORD);
        StudentsRepository studentsRepository = new StudentsRepositoryJDBCImpl(connection);
        System.out.println(studentsRepository.findById(2L));
        Student student = new Student(null, "Allaguly", "Ahmedov", 19, 913);
        studentsRepository.update(student);
        connection.close();
    }
}
