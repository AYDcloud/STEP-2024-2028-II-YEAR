import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameAvailabilityChecker {

    private final ConcurrentHashMap<String, Integer> usernameToUserId;
    private final ConcurrentHashMap<String, Integer> attemptFrequency;

    public UsernameAvailabilityChecker() {
        usernameToUserId = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
    }

    public void addUser(String username, int userId) {
        usernameToUserId.put(username.toLowerCase(), userId);
    }

    public boolean checkAvailability(String username) {
        username = username.toLowerCase();
        attemptFrequency.merge(username, 1, Integer::sum);
        return !usernameToUserId.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        username = username.toLowerCase();
        List<String> suggestions = new ArrayList<>();

        if (checkAvailability(username)) {
            suggestions.add(username);
            return suggestions;
        }

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameToUserId.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String dotVersion = username.replace("_", ".");
        if (!usernameToUserId.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        return attemptFrequency.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static void main(String[] args) {

        UsernameAvailabilityChecker checker = new UsernameAvailabilityChecker();

        checker.addUser("john_doe", 1);
        checker.addUser("admin", 2);
        checker.addUser("user123", 3);

        System.out.println("john_doe available? " +
                checker.checkAvailability("john_doe"));

        System.out.println("jane_smith available? " +
                checker.checkAvailability("jane_smith"));

        System.out.println("Suggestions for john_doe: " +
                checker.suggestAlternatives("john_doe"));

        for (int i = 0; i < 10543; i++) {
            checker.checkAvailability("admin");
        }

        System.out.println("Most attempted username: " +
                checker.getMostAttempted());
    }
}
