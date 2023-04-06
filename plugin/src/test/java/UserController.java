import java.util.HashSet;
import java.util.Set;

public class UserController {

    // we use java.util.Set here because it doesn't allow duplicates
    private final Set<User> users = new HashSet<>();

    public boolean addUser(String name, String password, String country) {
        // this will return false if the user already exists, and true if it doesn't
        // this is because Set#add returns false if the element already exists
        return users.add(new User(name, password, country));
    }

    // note: no static methods are used here, because it's not a good practice
    // if you want to easily access the methods, you can implement the singleton pattern

}
