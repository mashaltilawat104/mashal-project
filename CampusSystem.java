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
    protected int semester;
    protected double[] marks;
    protected double cgpa;
    protected String grade;
    protected int lateCount;

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
        return (sum / marks.length) / 25; // simple 100→4 scale
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
        System.out.println("Semester: " + semester + " | CGPA: " + String.format("%.2f", cgpa) + 
                           " | Grade: " + grade + " | Late Entries: " + lateCount);
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

    static int invalidAttempts = 0;

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Enter Campus");
            System.out.println("2. Add New Student Record");
            System.out.println("3. View All Students");
            System.out.println("4. View Campus Entry Log");
            System.out.println("5. View Late Entries");
            System.out.println("6. Department-Wise Student Summary");
            System.out.println("7. Search Student by ID");
            System.out.println("8. View Top 3 Students by CGPA");
            System.out.println("9. Exit");
            System.out.print("Choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch(choice) {
                case 1: enterCampus(); break;
                case 2: addStudent(); break;
                case 3: viewStudents(); break;
                case 4: viewLogs(); break;
                case 5: viewLateLogs(); break;
                case 6: departmentSummary(); break;
                case 7: searchStudent(); break;
                case 8: topCGPA(); break;
                case 9: System.out.println("Exiting..."); break;
                default: System.out.println("Invalid choice."); break;
            }
        } while(choice != 9);
    }

    // -------------------- Campus Entry --------------------
    static void enterCampus() {
        if(invalidAttempts >= 3) {
            System.out.println("Entry temporarily locked. Try again later.");
            return;
        }

        System.out.print("Enter role (Student/Staff/Visitor): ");
        String role = sc.nextLine();

        System.out.print("Enter ID: ");
        String id = sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        if(!validateName(name)) {
            System.out.println("Invalid name! Entry denied.");
            invalidAttempts++;
            return;
        }

        System.out.print("Enter Entry Time (HH:mm): ");
        String time = sc.nextLine();

        if(role.equalsIgnoreCase("Student")) {
            if(!isValidStudentID(id)) { System.out.println("Invalid Student ID!"); invalidAttempts++; return; }

            Student s = findStudent(id);
            if(s == null) {
                System.out.println("Student not found. Cannot enter.");
                invalidAttempts++;
                return;
            }
            if(isStudentLate(time)) { s.isLate = true; s.markLate(); }
            log[logCount++] = s;
            System.out.println("Student entry logged.");

        } else if(role.equalsIgnoreCase("Staff")) {
            if(!isValidStaffID(id)) { System.out.println("Invalid Staff ID!"); invalidAttempts++; return; }
            Staff st = new Staff(id, name, "General", time);
            if(isStaffLate(time)) st.isLate = true;
            log[logCount++] = st;
            System.out.println("Staff entry logged.");

        } else if(role.equalsIgnoreCase("Visitor")) {
            if(isVisitorEarly(time)) {
                System.out.println("Visitors allowed after 10:00! Entry denied.");
                invalidAttempts++;
                return;
            }
            Visitor v = new Visitor(id, name, time);
            log[logCount++] = v;
            System.out.println("Visitor entry logged.");
        } else {
            System.out.println("Unknown role! Entry denied.");
            invalidAttempts++;
        }
    }

    // -------------------- Add Student --------------------
    static void addStudent() {
        System.out.print("ID: ");
        String id = sc.nextLine();
        if(!isValidStudentID(id)) { System.out.println("Invalid ID!"); return; }

        System.out.print("Name: ");
        String name = sc.nextLine();
        if(!validateName(name)) { System.out.println("Invalid name!"); return; }

        System.out.print("Department: ");
        String dept = sc.nextLine();

        System.out.print("Semester: ");
        int sem = sc.nextInt();

        System.out.print("Subjects count (3-5): ");
        int n = sc.nextInt();
        double[] marks = new double[n];
        for(int i=0;i<n;i++) {
            System.out.print("Marks " + (i+1) + ": ");
            marks[i] = sc.nextDouble();
        }
        sc.nextLine();

        System.out.print("Entry Time: ");
        String time = sc.nextLine();

        students[studentCount++] = new Student(id, name, dept, sem, marks, time);
        System.out.println("Student added successfully.");
    }

    // -------------------- View Students --------------------
    static void viewStudents() {
        if(studentCount == 0) { System.out.println("No students."); return; }
        for(int i=0;i<studentCount;i++) students[i].display();
    }

    // -------------------- View Logs --------------------
    static void viewLogs() {
        if(logCount == 0) { System.out.println("No entries."); return; }
        for(int i=0;i<logCount;i++) log[i].display();
    }

    static void viewLateLogs() {
        boolean found = false;
        for(int i=0;i<logCount;i++) {
            if(log[i].isLate) { log[i].display(); found = true; }
        }
        if(!found) System.out.println("No late entries.");
    }

    // -------------------- Department Summary --------------------
    static void departmentSummary() {
        Map<String, Integer> deptCount = new HashMap<>();
        for(int i=0;i<studentCount;i++) {
            String dept = students[i].department.toLowerCase();
            deptCount.put(dept, deptCount.getOrDefault(dept,0)+1);
        }
        if(deptCount.isEmpty()) { System.out.println("No student records."); return; }
        for(String dept : deptCount.keySet()) {
            System.out.println("Department: " + dept + " | Students: " + deptCount.get(dept));
        }
    }

    // -------------------- Search Student --------------------
    static void searchStudent() {
        System.out.print("Enter Student ID to search: ");
        String id = sc.nextLine();
        Student s = findStudent(id);
        if(s != null) s.display();
        else System.out.println("Student not found.");
    }

    // -------------------- Top 3 CGPA --------------------
    static void topCGPA() {
        System.out.print("Enter Department for top CGPA: ");
        String dept = sc.nextLine().toLowerCase();
        ArrayList<Student> deptStudents = new ArrayList<>();
        for(int i=0;i<studentCount;i++) {
            if(students[i].department.equalsIgnoreCase(dept)) deptStudents.add(students[i]);
        }
        if(deptStudents.size() == 0) { System.out.println("No students in this department."); return; }

        deptStudents.sort((a,b) -> Double.compare(b.cgpa,a.cgpa)); // descending
        System.out.println("Top CGPA Students:");
        for(int i=0;i<Math.min(3,deptStudents.size());i++) deptStudents.get(i).display();
    }

    // -------------------- Utilities --------------------
    static Student findStudent(String id) {
        for(int i=0;i<studentCount;i++) if(students[i].id.equals(id)) return students[i];
        return null;
    }

    static boolean isValidStudentID(String id) { return id.matches("ST\\d{6}"); }
    static boolean isValidStaffID(String id) { return id.matches("SF\\d{5}"); }
    static boolean validateName(String name) {
        if(name.isEmpty()) return false;
        if(name.matches(".*\\d.*")) return false;           // contains digit
        if(name.matches("(.)\\1{3,}")) return false;       // repeated char >=4
        return true;
    }

    static boolean isStudentLate(String entryTime) {
        String[] parts = entryTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        return (hour > 9) || (hour == 9 && min > 30);
    }

    static boolean isStaffLate(String entryTime) {
        String[] parts = entryTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        return (hour > 8) || (hour == 8 && min > 45);
    }

    static boolean isVisitorEarly(String entryTime) {
        String[] parts = entryTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        return (hour < 10);
    }
}
