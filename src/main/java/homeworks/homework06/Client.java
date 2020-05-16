package homeworks.homework06;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        if (1 != args.length || !(args[0].equals("add") || args[0].equals("list"))) {
            throw new IllegalArgumentException("The first argument must be \"add\" or \"list\"!");
        }

        Socket socket = new Socket("localhost", 4444);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ObjectMapper objectMapper = new ObjectMapper();

        if (args[0].equals("add")) {
            Student student = readStudent();

            out.println("createStudent");
            out.println(objectMapper.writeValueAsString(student));

            if (in.readLine().equals("success")) {
                System.out.println("New Student has been created successfully!");
            }
        } else if (args[0].equals("list")) {
            out.println("getStudents");

            System.out.println("Students From Database:");
            System.out.println("-------------------------------------------");
            System.out.println("-------------------------------------------");

            String response = in.readLine();
            while (!response.equals("noStudents")) {
                Student studentObj = objectMapper.readValue(response, Student.class);
                printStudent(studentObj);
                response = in.readLine();
            }
        }

        out.close();
        in.close();
    }

    /**
     * Read student data from STDIN and return its object.
     *
     * @return Students object.
     */
    private static Student readStudent() {
        Scanner sc = new Scanner(System.in);
        Student student = new Student();
        String firstName;
        String lastName;
        int age;

        System.out.println("Create new Student");
        System.out.println("----------------------------------------------");
        System.out.print("Enter the Students First Name: ");
        firstName = sc.nextLine();
        System.out.print("Enter the Students Last Name: ");
        lastName = sc.nextLine();
        System.out.print("Enter the Students Age: ");
        age = sc.nextInt();

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setAge(age);

        sc.close();

        return student;
    }

    /**
     * Print given Student.
     *
     * @param student Student to pint.
     */
    private static void printStudent(Student student) {
        System.out.println("First Name: " + student.getFirstName());
        System.out.println("Last Name: \t" + student.getLastName());
        System.out.println("Age: \t\t" + student.getAge());
        System.out.println("-------------------------------------------");
    }
}
