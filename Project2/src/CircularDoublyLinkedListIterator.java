// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.util.Iterator;
import java.util.NoSuchElementException;

// Implements an iterator for the CircularDoublyLinkedList class
public class CircularDoublyLinkedListIterator<T> implements Iterator<T> {
    // Reference to the dummy node of the list
    private CircularDoublyLinkedList.Node<T> dummy;
    // Current node being pointed to
    private CircularDoublyLinkedList.Node<T> current;
    // Flag to track if we've completed one full iteration
    private boolean firstPass;

    // Creates a new iterator starting at the dummy node
    public CircularDoublyLinkedListIterator(CircularDoublyLinkedList.Node<T> dummy) {
        this.dummy = dummy;
        this.current = dummy;
        this.firstPass = true;
    }

    // Checks if there are more elements to iterate over
    @Override
    public boolean hasNext() {
        if (firstPass) {
            return current.next != dummy;
        }
        return current.next != dummy && current.next != dummy.next;
    }

    // Returns the next element in the iteration
    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        current = current.next;
        if (current == dummy.next) {
            firstPass = false;
        }
        return current.data;
    }
} 