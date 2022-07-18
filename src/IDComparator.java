import java.util.Comparator;

public class IDComparator implements Comparator<Student> {

    //Sorting students based on id
    @Override
    public int compare(Student o1, Student o2) {
        return o1.getId() - o2.getId();
    }
}
