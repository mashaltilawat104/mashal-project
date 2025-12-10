import java.util.*;

abstract class Person {
    protected String id;
    protected String name;
    protected String role;
    protected String department;
    protected String entryTime;
    protected boolean isLate;

    public Person(String id, String name, String role, String department, String entryTime) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.department = department;
        this.entryTime = entryTime;
        this.isLate = false;
    }

    public void display() {
        System.out.println("ID: " + id + " | Name: " + name + 
                           " | Role: " + role + " | Dept: " + department +
                           " | Entry: " + entryTime + (isLate ? " (LATE)" : ""));
    }
}

class Student extends Person {
    private int semester;
    private double[] marks;
    private double cgpa;
    private String grade;
    private int lateCount;

    public Student(String id, String name, String department, int semester, double[] marks, String entryTime) {
        super(id, name, "Student", department, entryTime);
        this.semester = semester;
        this.marks = marks;
        this.cgpa = computeCGPA();
        this.grade = computeGrade();
        this.lateCount = 0;
    }

    private double computeCGPA() {
        double sum = 0;
        for (double m : marks) sum += m;
        return (sum / marks.length) / 25; // simple: 100→4 scale
    }

    private String computeGrade() {
        if (cgpa >= 3.5) return "A";
        if (cgpa >= 3.0) return "B";
        if (cgpa >= 2.0) return "C";
        return "D";
    }

    public void markLate() { lateCount++; }

    @Override
    public void display() {
        System.out.println("\n--- STUDENT ---");
        super.display();
        System.out.println("Semester: " + semester + " | CGPA: " + cgpa + " | Grade: " + grade +
                           " | Late Entries: " + lateCount);
    }
}

class Staff extends Person {
    public Staff(String id, String name, String department, String entryTime) {
        super(id, name, "Staff", department, entryTime);
    }

    @Override
    public void display() {
        System.out.println("\n--- STAFF ---");
        super.display();
    }
}

class Visitor extends Person {
    public Visitor(String id, String name, String entryTime) {
        super(id, name, "Visitor", "None", entryTime);
    }

    @Override
    public void display() {
        System.out.println("\n--- VISITOR ---");
        super.display();
    }
}

public class CampusSystem {
    static Scanner sc = new Scanner(System.in);
    static Student[] students = new Student[100];
    static int studentCount = 0;

    static Person[] log = new Person[300];
    static int logCount = 0;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Enter Campus");
            System.out.println("2. Add Student");
            System.out.println("3. View All Students");
            System.out.println("4. View Logs");
            System.out.println("5. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) enterCampus();
            else if (choice == 2) addStudent();
            else if (choice == 3) viewStudents();
            else if (choice == 4) viewLogs();
            else if (choice == 5) break;
        }
    }

    static void enterCampus() {
        System.out.print("Enter role (Student/Staff/Visitor): ");
        String role = sc.nextLine();

        System.out.print("Enter ID: ");
        String id = sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Entry Time (HH:mm): ");
        String time = sc.nextLine();

        if (role.equalsIgnoreCase("Student")) {
            Student s = findStudent(id);
            if (s == null) {
                System.out.println("Student not found. Cannot enter.");
                return;
            }

            if (time.compareTo("09:30") > 0) {
                s.isLate = true;
                s.markLate();
            }

            log[logCount++] = s;
            System.out.println("Student entry logged.");
        } 
        else if (role.equalsIgnoreCase("Staff")) {
            Staff st = new Staff(id, name, "General", time);
            log[logCount++] = st;
            System.out.println("Staff logged.");
        }
        else {
            if (time.compareTo("10:00") < 0) {
                System.out.println("Visitors allowed after 10:00!");
                return;
            }
            Visitor v = new Visitor(id, name, time);
            log[logCount++] = v;
            System.out.println("Visitor logged.");
        }
    }

    static void addStudent() {
        System.out.print("ID: ");
        String id = sc.nextLine();

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Department: ");
        String dept = sc.nextLine();

        System.out.print("Semester: ");
        int sem = sc.nextInt();

        System.out.print("Subjects count (3-5): ");
        int n = sc.nextInt();
        double[] marks = new double[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Marks " + (i+1) + ": ");
            marks[i] = sc.nextDouble();
        }
        sc.nextLine();

        System.out.print("Entry Time: ");
        String time = sc.nextLine();

        students[studentCount++] = new Student(id, name, dept, sem, marks, time);
        System.out.println("Student added.");
    }

    static void viewStudents() {
        for (int i = 0; i < studentCount; i++) {
            students[i].display();
        }
    }

    static void viewLogs() {
        for (int i = 0; i < logCount; i++) {
            log[i].display();
        }
    }

    static Student findStudent(String id) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].id.equals(id)) return students[i];
        }
        return null;
    }
}