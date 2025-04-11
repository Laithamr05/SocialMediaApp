import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class CircularDoublyLinkedList<T extends Comparable<T>> {
	Node<T> dummy;

	public CircularDoublyLinkedList() {
		dummy = new Node<>(null);
		dummy.next = dummy;
		dummy.previous = dummy;
	}

	public void add(T data) {
		insertLast(data);
	}

	public void insertFirst(T data) {
		Node<T> newNode = new Node(data);
		newNode.next = dummy.next;
		newNode.previous = dummy;
		dummy.next.previous = newNode;
		dummy.next = newNode;
	}

	public void insertLast(T data) {
		Node<T> newNode = new Node<>(data);
		newNode.previous = dummy.previous;
		newNode.next = dummy;
		dummy.previous.next = newNode;
		dummy.previous = newNode;
	}

	public void insertMiddle(T node, T data) {
		if (!contains(node)) {
			return;
		}

		Node<T> current = dummy.next;
		while (current != dummy) {
			if (current.data.equals(node)) {
				Node<T> newNode = new Node<>(data);
				newNode.next = current.next;
				newNode.previous = current;
				current.next.previous = newNode;
				current.next = newNode;
				return;
			}
			current = current.next;
		}
	}

	public int size() {
		int count = 0;
		Node<T> current = dummy.next;
		while (current != dummy) {
			count++;
			current = current.next;
		}
		return count;
	}

	public void display() {
		Node<T> current = dummy.next;
		while (current != dummy) {
			System.out.println(current.data);
			current = current.next;
		}
	}

	public boolean deleteAll() {
		Node<T> current = dummy.next;
		while (current != dummy) {
			Node<T> next = current.next;
			current.next = null;
			current.previous = null;
			current = next;
		}
		dummy.next = dummy;
		dummy.previous = dummy;
		return true;
	}

	public void delete(T data) {
		if (!contains(data)) {
			return;
		}

		Node<T> current = dummy.next;
		while (current != dummy) {
			if (current.data.equals(data)) {
				current.previous.next = current.next;
				current.next.previous = current.previous;
				current.next = null;
				current.previous = null;
				return;
			}
			current = current.next;
		}
	}

	public void displaySorted() {
		List<T> list = new ArrayList<T>();
		Node<T> current = dummy.next;
		while (current != dummy) {
			list.add(current.data);
			current = current.next;
		}
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			T item = list.get(i);
			System.out.println(item);
		}
	}

	public boolean contains(T data) {
		Node<T> current = dummy.next;
		while (current != dummy) {
			if (current.data.equals(data)) {
				return true;
			}
			current = current.next;
		}
		return false;
	}

	public boolean isEmpty() {
		return dummy.next == dummy;
	}

	public void clear() {
		dummy.next = dummy;
		dummy.previous = dummy;
	}

	public java.util.Iterator<T> iterator() {
		return new CircularDoublyLinkedListIterator<>(dummy);
	}
}
