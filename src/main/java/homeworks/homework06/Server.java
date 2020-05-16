package homeworks.homework06;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Server {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/java-hw-6";
    private static final String USER = "root";
    private static final String PASSWORD = "toor";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4444);
        Socket socket = serverSocket.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while (socket.isConnected()) {
            String command = in.readLine();

            if (null != command) {
                if (command.equals("createStudent")) {
                    String student = in.readLine();

                    if (createStudent(student)) {
                        out.println("success");
                    } else {
                        out.println("error");
                    }
                } else if (command.equals("getStudents")) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM student;");

                        while (resultSet.next()) {
                            Student student = new Student();
                            student.setFirstName(resultSet.getString("first_name"));
                            student.setLastName(resultSet.getString("last_name"));
                            student.setAge(resultSet.getInt("age"));

                            out.println(objectMapper.writeValueAsString(student));
                        }

                        resultSet.close();
                        connection.close();

                        out.println("noStudents");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        in.close();
        out.close();
        socket.close();
        serverSocket.close();
    }

    /**
     * Write given student to database.
     *
     * @param student JSON with student data.
     *
     * @return True on success, or false,
     */
    private static boolean createStudent(String student) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);

            Student studentObj = objectMapper.readValue(student, Student.class);

            PreparedStatement preparedStmt = connection.prepareStatement("INSERT INTO student (first_name, last_name, age) VALUES (?, ?, ?);");
            preparedStmt.setString(1, studentObj.getFirstName());
            preparedStmt.setString(2, studentObj.getLastName());
            preparedStmt.setInt(3, studentObj.getAge());
            preparedStmt.execute();

            connection.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
