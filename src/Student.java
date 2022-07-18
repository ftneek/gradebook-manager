import java.util.Objects;

public class Student {
    private int id;
    private String name;

    @Override
    public String toString() {
        return getId() + "." + getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id;
    }

    public Student(int id) {
        this(id, "Unnamed");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
