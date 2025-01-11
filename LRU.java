import java.util.*;

class LRUCache {
    class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head, tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        head = new Node(0, 0); // Dummy head
        tail = new Node(0, 0); // Dummy tail
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            moveToHead(node); // Mark as recently used
            return node.value;
        }
        return -1; // Key not found
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.value = value;
            moveToHead(node); // Mark as recently used
        } else {
            if (cache.size() == capacity) {
                removeLRU();
            }
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addToHead(newNode);
        }
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void removeLRU() {
        Node lru = tail.prev;
        cache.remove(lru.key);
        removeNode(lru);
    }
}

public class LRUCacheTest {
    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(3);

        lruCache.put(1, 1);
        lruCache.put(2, 2);
        lruCache.put(3, 3);
        System.out.println(lruCache.get(1)); // Output: 1
        lruCache.put(4, 4); // Evicts key 2
        System.out.println(lruCache.get(2)); // Output: -1
        lruCache.put(5, 5); // Evicts key 3
        System.out.println(lruCache.get(3)); // Output: -1
        System.out.println(lruCache.get(4)); // Output: 4
        System.out.println(lruCache.get(5)); // Output: 5
    }
}
