import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ComparatorTest {

    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>(Arrays.asList("a", "c", "b", "f", "g", "a2"));
        strings.sort(Comparator.naturalOrder());

        strings.forEach(System.out::println);
    }
}
