import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Lesson6Task01 {

    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Иванов", "ПИ-21", 2, Map.of("Математика", 4, "Физика", 5)));
        students.add(new Student("Петров", "ПИ-21", 2, Map.of("Математика", 2, "Физика", 2)));
        students.add(new Student("Сидоров", "КН-33", 1, Map.of("Математика", 3, "Физика", 4)));

        System.out.println("До удаления со средним < 3:");
        printLastNamesLine(students);

        removeStudentsWithLowAverage(students);
        System.out.println("\nПосле удаления со средним < 3:");
        printLastNamesLine(students);

        promoteStudentsWithSufficientAverage(students);
        System.out.println("\nПосле перевода на следующий курс (средний >= 3):");
        printLastNamesLine(students);

        Set<Student> studentSet = new HashSet<>(students);
        System.out.println("\nСтуденты 2 курса:");
        printStudents(studentSet, 2);
    }

    public static void removeStudentsWithLowAverage(Collection<Student> students) {
        students.removeIf(student -> student.getAverageGrade() < 3.0);
    }

    public static void promoteStudentsWithSufficientAverage(Collection<Student> students) {
        for (Student student : students) {
            if (student.getAverageGrade() >= 3.0) {
                student.setCourse(student.getCourse() + 1);
            }
        }
    }

    public static void printStudents(Set<Student> students, int course) {
        for (Student student : students) {
            if (student.getCourse() == course) {
                System.out.println(student.getLastName());
            }
        }
    }

    private static void printLastNamesLine(Collection<Student> students) {
        for (Student student : students) {
            System.out.println(student.getLastName());
        }
    }
}

class Student {

    private String lastName;
    private String group;
    private int course;
    private final Map<String, Integer> gradesBySubject;

    public Student(String lastName, String group, int course, Map<String, Integer> gradesBySubject) {
        this.lastName = lastName;
        this.group = group;
        this.course = course;
        this.gradesBySubject = new HashMap<>(gradesBySubject);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public Map<String, Integer> getGradesBySubject() {
        return Map.copyOf(gradesBySubject);
    }

    public void setGrade(String subject, int grade) {
        gradesBySubject.put(subject, grade);
    }

    public double getAverageGrade() {
        if (gradesBySubject.isEmpty()) {
            return 0.0;
        }
        return gradesBySubject.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return course == student.course
                && Objects.equals(lastName, student.lastName)
                && Objects.equals(group, student.group)
                && Objects.equals(gradesBySubject, student.gradesBySubject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, group, course, gradesBySubject);
    }

    @Override
    public String toString() {
        return lastName;
    }
}
