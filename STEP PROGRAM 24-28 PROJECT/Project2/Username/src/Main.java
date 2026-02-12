// imports the core utility classes from java used for assign data to
// different contai
import java.util.*;

import java.util.concurrent.*;

import java.util.concurrent.atomic.AtomicInteger;

class UsernameService {

    private final ConcurrentHashMap<String, String> usernameToUser;

    private final ConcurrentHashMap<String, AtomicInteger> attemptCount;

    public UsernameService() {
        usernameToUser = new ConcurrentHashMap<>();
        attemptCount = new ConcurrentHashMap<>();
    }
    public boolean checkAvailability(String username) {
        attemptCount

                .computeIfAbsent(username, k -> new AtomicInteger(0))
                .incrementAndGet();

        return !usernameToUser.containsKey(username);
    }

    public boolean registerUsername(String username, String userId) {

        return usernameToUser.putIfAbsent(username, userId) == null;
    }

    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();


        for (int i = 1; i <= 20 && suggestions.size() < 3; i++) {
            String candidate = username + i;

            if (!usernameToUser.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        if (username.contains("_")) {
            String dotVariant = username.replace("_", ".");
            if (!usernameToUser.containsKey(dotVariant)) {
                suggestions.add(dotVariant);
            }
        }

        return suggestions;
    }

    public String getMostAttemptedUsername() {
        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptCount.entrySet()) {
            int attempts = entry.getValue().get();
            if (attempts > maxAttempts) {
                maxAttempts = attempts;
                mostAttempted = entry.getKey();
            }
        }
        return mostAttempted;
    }

    public static void main(String[] args) {
        UsernameService service = new UsernameService();

        service.registerUsername("john_doe", "user123");
        service.registerUsername("admin", "root001");

        System.out.println("john_doe available? "
                + service.checkAvailability("john_doe")); // false
        System.out.println("jane_smith available? "
                + service.checkAvailability("jane_smith")); // true

        System.out.println("Suggestions for john_doe: "
                + service.suggestAlternatives("john_doe"));

        for (int i = 0; i < 10_543; i++) {
            service.checkAvailability("admin");
        }

        System.out.println("Most attempted username: "
                + service.getMostAttemptedUsername());
    }
}
