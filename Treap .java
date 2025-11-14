import java.util.Random;
import java.util.Stack;

public class Treap<E extends Comparable<E>> {
    private static class Node<E> {
        public E data;
        public int priority;
        public Node<E> left, right;

        public Node(E data, int priority) {
            if (data == null) throw new IllegalArgumentException("Node key cannot be null");
            this.data = data;
            this.priority = priority;
            this.left = null;
            this.right = null;
        }

        public Node<E> rotateRight() {
            Node<E> newRoot = left;
            left = newRoot.right;
            newRoot.right = this;
            return newRoot;
        }

        public Node<E> rotateLeft() {
            Node<E> newRoot = right;
            right = newRoot.left;
            newRoot.left = this;
            return newRoot;
        }

        public String toString() {
            return data + "," + priority;
        }
    }

    private Random priorityGenerator;
    private Node<E> root;

    public Treap() {
        priorityGenerator = new Random();
        root = null;
    }

    public Treap(long seed) {
        priorityGenerator = new Random(seed);
        root = null;
    }

    public boolean add(E key) {
        int priority = priorityGenerator.nextInt(Integer.MAX_VALUE);
        return add(key, priority);
    }

    public boolean add(E key, int priority) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        if (root == null) {
            root = new Node<>(key, priority);
            return true;
        }

        Stack<Node<E>> stack = new Stack<>();
        Node<E> cur = root;
        while (true) {
            stack.push(cur);
            int cmp = key.compareTo(cur.data);
            if (cmp == 0) {
                return false; // Duplicate
            } else if (cmp < 0) {
                if (cur.left == null) {
                    cur.left = new Node<>(key, priority);
                    stack.push(cur.left);
                    break;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new Node<>(key, priority);
                    stack.push(cur.right);
                    break;
                }
                cur = cur.right;
            }
        }
        while (stack.size() >= 2) {
            Node<E> child = stack.pop();
            Node<E> parent = stack.peek();
            if (child.priority > parent.priority) {
                if (child == parent.left) {
                    Node<E> newParent = parent.rotateRight();
                    if (stack.size() == 1) root = newParent;
                    else {
                        Node<E> grand = stack.get(stack.size() - 2);
                        if (grand.left == parent) grand.left = newParent;
                        else grand.right = newParent;
                    }
                } else {
                    Node<E> newParent = parent.rotateLeft();
                    if (stack.size() == 1) root = newParent;
                    else {
                        Node<E> grand = stack.get(stack.size() - 2);
                        if (grand.left == parent) grand.left = newParent;
                        else grand.right = newParent;
                    }
                }
            }
        }
        return true;
    }

    public boolean delete(E key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (root == null) return false;

        Node<E> parent = null;
        Node<E> current = root;
        boolean isLeftChild = false;

        while (current != null) {
            int cmp = key.compareTo(current.data);
            if (cmp == 0) break;
            parent = current;
            if (cmp < 0) {
                current = current.left;
                isLeftChild = true;
            } else {
                current = current.right;
                isLeftChild = false;
            }
        }
        if (current == null) return false; // Not found

        while (current.left != null || current.right != null) {
            if (current.left == null) {
                Node<E> newSubRoot = current.rotateLeft();
                if (parent == null) root = newSubRoot;
                else if (isLeftChild) parent.left = newSubRoot;
                else parent.right = newSubRoot;
                parent = newSubRoot;
                isLeftChild = true; // New node became parent's left
            } else if (current.right == null) {
                Node<E> newSubRoot = current.rotateRight();
                if (parent == null) root = newSubRoot;
                else if (isLeftChild) parent.left = newSubRoot;
                else parent.right = newSubRoot;
                parent = newSubRoot;
                isLeftChild = false; // New node became parent's right
            } else if (current.left.priority > current.right.priority) {
                Node<E> newSubRoot = current.rotateRight();
                if (parent == null) root = newSubRoot;
                else if (isLeftChild) parent.left = newSubRoot;
                else parent.right = newSubRoot;
                parent = newSubRoot;
                isLeftChild = false;
            } else {
                Node<E> newSubRoot = current.rotateLeft();
                if (parent == null) root = newSubRoot;
                else if (isLeftChild) parent.left = newSubRoot;
                else parent.right = newSubRoot;
                parent = newSubRoot;
                isLeftChild = true;
            }
            if (isLeftChild) current = parent.left;
            else current = parent.right;
        }
        if (parent == null) root = null;
        else if (isLeftChild) parent.left = null;
        else parent.right = null;

        return true;
    }

    private boolean find(Node<E> node, E key) {
        if (node == null) return false;
        int cmp = key.compareTo(node.data);
        if (cmp == 0) return true;
        else if (cmp < 0) return find(node.left, key);
        else return find(node.right, key);
    }

    public boolean find(E key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return find(root, key);
    }

    public String toString() {
        return toString(root);
    }

    private String toString(Node<E> node) {
        if (node == null) return "null";
        return node.toString() + " (" + toString(node.left) + ") (" + toString(node.right) + ")";
    }

    public static void main(String[] args) {
        Treap<Integer> testTree = new Treap<>();
        testTree.add(4, 19);
        testTree.add(2, 31);
        testTree.add(6, 70);
        testTree.add(1, 84);
        testTree.add(3, 12);
        testTree.add(5, 83);
        testTree.add(7, 26);
        System.out.println("Treap structure:");
        System.out.println(testTree);

        System.out.println("Find 4: " + testTree.find(4));
        System.out.println("Find 8: " + testTree.find(8));

        System.out.println("Deleting 6: " + testTree.delete(6));
        System.out.println("Treap after deleting 6:");
        System.out.println(testTree);

        System.out.println("Deleting 2: " + testTree.delete(2));
        System.out.println(testTree);

        System.out.println("Deleting 1: " + testTree.delete(1));
        System.out.println(testTree);
    }
}
