import model.User;

import java.util.ArrayList;

public class Storage {
    public final UserManager userManager = new UserManager();
    public final EmailManager emailManager = new EmailManager();

    /**
     * Seed data
     */
    public void seed() {
        if (userManager.size() != 0) {
            return;
        }

        User[] users = new User[]{
                new User("user1@email.com", "password"),
                new User("user2@email.com", "password"),
                new User("user3@email.com", "password")
        };

        for (User user : users) {
            // I can't believe this is the easiest way #giveusstructsinjava
            userManager.add(user.getEmail(), user.getPassword());
        }

        ArrayList<String> recipients = new ArrayList<>();
        recipients.add(users[1].getEmail());
        recipients.add(users[2].getEmail());

        emailManager.sendEmail(
                users[0].getEmail(),
                recipients,
                "First Email",
                "This is my first email.\n\nBest regards,\nUser 1"
        );
    }
}

