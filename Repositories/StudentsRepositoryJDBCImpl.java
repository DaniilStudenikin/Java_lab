package Repositories;

import models.Mentor;
import models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StudentsRepositoryJDBCImpl implements StudentsRepository {

    //language=SQL
    private static final String SQL_SELECT_BY_ID = "select * from student where id = ";
    private static final String SQL_SELECT_BY_AGE = "select * from student where age = ";
    private static final String SQL_INSERT = "insert into student (first_name, last_name, age, group_number) values(?,?,?,?)";
    private static final String SQL_UPDATE = "update student set first_name = ?, last_name = ?, age = ?, group_number = ? where id = ?";
    private static final String SQL_SELECT_ALL_WITH_MENTORS = "select student.id, student.first_name, student.last_name, student.age,\n" +
            " student.group_number, m.id as mentor_id, m.first_name fn_mentor, m.last_name ln_mentor\n" +
            " from student left join mentor m on student.id = m.student_id";

    private Connection connection;

    public StudentsRepositoryJDBCImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Student> findAllByAge(int age) {
        List<Student> student = new LinkedList<>();
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_BY_AGE + age);
            while (result.next()) {
                student.add(new Student(
                        result.getLong("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getInt("age"),
                        result.getInt("group_number")));
            }
            return student;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    // Необходимо вытащить список всех студентов, при этом у каждого студента должен быть проставлен список менторов
    // у менторов в свою очередь ничего проставлять (кроме имени, фамилии, id не надо)
    // student1(id, firstName, ..., mentors = [{id, firstName, lastName, null}, {}, ), student2, student3
    // все сделать одним запросом
    @Override
    public List<Student> findAll() {
        Statement statement = null;
        ResultSet result = null;
        List<Student> studentsFindAll = new ArrayList<>();
        List<Mentor> mentors = new ArrayList<>();
        Student student;
        Student student1;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_ALL_WITH_MENTORS);

            while (result.next()) {
                boolean b = true;
                for(int i = 0; i<studentsFindAll.size(); i++) {
                    if (studentsFindAll.get(i).getId() == result.getInt("id")) {
                        b = false;

                        studentsFindAll.get(i).addMentor(new Mentor(
                                result.getInt("mentor_id"),
                                result.getString("fn_mentor"),
                                result.getString("ln_mentor")
                        ));
                        break;
                    }
                }
                student = new Student(
                        result.getLong("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getInt("age"),
                        result.getInt("group_number"),
                        new ArrayList<>()
                );
                if (b) {
                    studentsFindAll.add(student);
                    if (result.getInt("mentor_id") != 0) {
                        studentsFindAll.get(studentsFindAll.size()-1).addMentor(new Mentor(
                                result.getInt("mentor_id"),
                                result.getString("fn_mentor"),
                                result.getString("ln_mentor")

                        ));
                    }
                }


            }
            return studentsFindAll;

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }

    }

    @Override
    public Student findById(Long id) {
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_BY_ID + id);
            if (result.next()) {
                return new Student(
                        result.getLong("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getInt("age"),
                        result.getInt("group_number")
                );
            } else return null;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    // просто вызывается insert для сущности
    // student = Student(null, 'Марсель', 'Сидиков', 26, 915)
    // studentsRepository.save(student);
    // // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
    @Override
    public void save(Student entity) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(SQL_INSERT);
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setInt(3, entity.getAge());
            preparedStatement.setInt(4, entity.getGroupNumber());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    // для сущности, у которой задан id выполнить обновление всех полей

    // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
    // student.setFirstName("Игорь")
    // student.setLastName(null);
    // studentsRepository.update(student);
    // (3, 'Игорь', null, 26, 915)

    @Override
    public void update(Student entity) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(SQL_UPDATE);
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setInt(3, entity.getAge());
            preparedStatement.setInt(4, entity.getGroupNumber());
            preparedStatement.setLong(5, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}
