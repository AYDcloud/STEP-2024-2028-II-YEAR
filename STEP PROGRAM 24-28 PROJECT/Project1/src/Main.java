import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class FlashSaleInventoryManager {

    private final ConcurrentHashMap<String, AtomicInteger> inventory =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, LinkedHashMap<Long, Long>> waitingLists =
            new ConcurrentHashMap<>();

    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingLists.put(productId, new LinkedHashMap<>());
    }

    public int checkStock(String productId) {
        return inventory.get(productId).get();
    }

    public String purchaseItem(String productId, long userId) {
        AtomicInteger stock = inventory.get(productId);

        while (true) {
            int current = stock.get();

            if (current == 0) {
                synchronized (waitingLists.get(productId)) {
                    waitingLists.get(productId).put(userId, System.nanoTime());
                    return "Added to waiting list";
                }
            }

            if (stock.compareAndSet(current, current - 1)) {
                return "Success, remaining: " + (current - 1);
            }
        }
    }

    public static void main(String[] args) {
        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();
        manager.addProduct("IPHONE15_256GB", 2);

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 1));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 2));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 3));
    }
}
