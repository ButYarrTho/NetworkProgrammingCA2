
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, String> users = new HashMap<>();

    public synchronized boolean addUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, password);
        return true;
    }

    public synchronized boolean validateLogin(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public synchronized boolean userExists(String username) {
        return users.containsKey(username);
    }
}
