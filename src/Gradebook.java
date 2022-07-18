import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class Gradebook extends HashMap<Student,Character> {

    public static boolean isValidGrade(char grade){
        return grade == 'A' || grade == 'B' || grade == 'C' || grade == 'D' || grade == 'F' || grade == 'N';
    }

    public boolean addStudent(Student student, char grade) {
        if (!isValidGrade(grade)) {
            grade = 'N';
        }
        return !this.containsKey(student) && this.put(student, grade) == null;

    }

    public boolean addStudent(Student student) {
        return addStudent(student, 'N');
    }

    public boolean deleteStudent(int id) {
        return this.remove(new Student(id)) != null;
    }

    public boolean updateStudent(int id, String newName) {
        //remove student, add back with correct name/grade
        Student student = new Student(id, newName);
        return this.containsKey(student) && this.put(student, this.remove(student)) == null;
    }

    public boolean updateGrade(int id, char newGrade){
        return isValidGrade(newGrade) && this.replace(new Student(id), newGrade) != null;

    }

    public String printGrades(Comparator<Student> comparator){
        //create tree map using comparator, use putall
        StringBuilder stringBuilder = new StringBuilder();
        TreeMap<Student, Character> map = new TreeMap<>(comparator);
        map.putAll(this);
        for (Entry<Student, Character> student : map.entrySet()) {
            stringBuilder.append(student.getKey() + "." + student.getValue() + "\n");
        }
        return stringBuilder.toString();
    }

}
