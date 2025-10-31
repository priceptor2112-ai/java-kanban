import java.util.Objects;

public class Group {
    private String title;
    private Age age;  // было AgeCategory
    private int duration;

    public Group(String title, Age age, int duration) {  // исправить здесь
        this.title = title;
        this.age = age;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public Age getAge() {
        return age;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return duration == group.duration &&
                Objects.equals(title, group.title) &&
                age == group.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, age, duration);
    }
}