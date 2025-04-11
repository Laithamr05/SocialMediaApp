// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.util.NoSuchElementException;

public class CircularDoublyLinkedListIterator<T> implements java.util.Iterator<T> { // iterator for circular doubly linked list
    private Node<T> current; // current node being pointed to
    private Node<T> dummy; // dummy node (sentinel) for the linked list
    private boolean started = false; // flag to track if iteration has started

    public CircularDoublyLinkedListIterator(Node<T> dummy) { // constructor to initialize the iterator
        this.dummy = dummy; // store the dummy node
        this.current = dummy.next; // start at the first actual node
    }

    @Override
    public boolean hasNext() { // checks if there are more elements to iterate
        return !started || current != dummy; // true if not started or not back at dummy node
    }

    @Override
    public T next() { // returns the next element in the iteration
        if (!hasNext()) { // check if there are no more elements
            throw new NoSuchElementException(); // throw exception if no more elements
        }

        started = true; // mark iteration as started
        T data = current.data; // get data from current node
        current = current.next; // move to next node
        return data; // return the data
    }
} 