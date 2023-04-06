import java.util.ArrayList;
import java.util.List;

public class UserControllerSimpler {

    // we use java.util.List here because... that's what we're taught to use
    // it's not the best choice, but it's the most common one
    private final List<User> users = new ArrayList<>();

    public boolean addUser(String name, String password, String country) {
        // this will always return true, because List#add always returns true
        return users.add(new User(name, password, country));
    }

    // note: no static methods are used here, because it's not a good practice
    // if you want to easily access the methods, you can implement the singleton pattern

}
