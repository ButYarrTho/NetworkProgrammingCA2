
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();

    public synchronized boolean addUser(User user) {
        if (users.containsKey(user.getEmail())) {
            return false;
        }
        users.put(user.getEmail(), user);
        return true;
    }

    public synchronized boolean validateLogin(String username, String password) {
        return users.containsKey(username) && users.get(username).getPassword().equals(password);
    }

    public synchronized boolean userExists(String username) {
        return users.containsKey(username);
    }
}
