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
        students.add(new Student("Иванов", "ИС-101", 2, Map.of("Математика", 4, "Физика", 5)));
        students.add(new Student("Петров", "ИС-101", 2, Map.of("Математика", 2, "Физика", 2)));
        students.add(new Student("Сидоров", "ИС-102", 1, Map.of("Математика", 3, "Физика", 4)));

        System.out.println("До удаления со средним < 3:");
        students.forEach(System.out::println);

        removeStudentsWithLowAverage(students);
        System.out.println("\nПосле удаления со средним < 3:");
        students.forEach(System.out::println);

        promoteStudentsWithSufficientAverage(students);
        System.out.println("\nПосле перевода на следующий курс (средний >= 3):");
        students.forEach(System.out::println);

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
                System.out.println(student.getName());
            }
        }
    }
}

class Student {

    private String name;
    private String group;
    private int course;
    private final Map<String, Integer> gradesBySubject;

    public Student(String name, String group, int course, Map<String, Integer> gradesBySubject) {
        this.name = name;
        this.group = group;
        this.course = course;
        this.gradesBySubject = new HashMap<>(gradesBySubject);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                && Objects.equals(name, student.name)
                && Objects.equals(group, student.group)
                && Objects.equals(gradesBySubject, student.gradesBySubject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, group, course, gradesBySubject);
    }

    @Override
    public String toString() {
        return "Student{"
                + "name='" + name + '\''
                + ", group='" + group + '\''
                + ", course=" + course
                + ", grades=" + gradesBySubject
                + ", average=" + String.format("%.2f", getAverageGrade())
                + '}';
    }
}
