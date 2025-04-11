import java.util.NoSuchElementException;

public class CircularDoublyLinkedListIterator<T> implements java.util.Iterator<T> {
    private Node<T> current;
    private Node<T> dummy;
    private boolean started = false;

    public CircularDoublyLinkedListIterator(Node<T> dummy) {
        this.dummy = dummy;
        this.current = dummy.next;
    }

    @Override
    public boolean hasNext() {
        return !started || current != dummy;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        started = true;
        T data = current.data;
        current = current.next;
        return data;
    }
} 