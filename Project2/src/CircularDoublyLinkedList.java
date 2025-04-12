// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Implements a generic circular doubly linked list data structure
public class CircularDoublyLinkedList<T> implements Iterable<T> {
	// Inner class for Node implementation
	public class Node<T> {
		T data;
		Node<T> next;
		Node<T> previous;

		// Creates a new node with the given data
		public Node(T data) {
			this.data = data;
			this.next = null;
			this.previous = null;
		}
	}

	// Reference to the dummy node that marks the start/end of the list
	private Node<T> dummy;
	// Current size of the list
	private int size;

	// Creates an empty circular doubly linked list
	public CircularDoublyLinkedList() {
		dummy = new Node<>(null);
		dummy.next = dummy;
		dummy.previous = dummy;
		size = 0;
	}

	// Adds a new element to the end of the list
	public void add(T data) {
		insertLast(data);
	}

	// Inserts a new element at the beginning of the list
	public void insertFirst(T data) {
		Node<T> newNode = new Node<>(data);
		newNode.next = dummy.next;
		newNode.previous = dummy;
		dummy.next.previous = newNode;
		dummy.next = newNode;
		size++;
	}

	// Inserts a new element at the end of the list
	public void insertLast(T data) {
		Node<T> newNode = new Node<>(data);
		newNode.next = dummy;
		newNode.previous = dummy.previous;
		dummy.previous.next = newNode;
		dummy.previous = newNode;
		size++;
	}

	// Inserts a new element at the specified position
	public void insertMiddle(T data, int position) {
		if (position < 0 || position > size) {
			throw new IndexOutOfBoundsException("Invalid position");
		}
		if (position == 0) {
			insertFirst(data);
		} else if (position == size) {
			insertLast(data);
		} else {
			Node<T> current = dummy.next;
			for (int i = 0; i < position; i++) {
				current = current.next;
			}
			Node<T> newNode = new Node<>(data);
			newNode.next = current;
			newNode.previous = current.previous;
			current.previous.next = newNode;
			current.previous = newNode;
			size++;
		}
	}

	// Returns the current size of the list
	public int size() {
		return size;
	}

	// Displays the contents of the list
	public void display() {
		Node<T> current = dummy.next;
		while (current != dummy) {
			System.out.print(current.data + " ");
			current = current.next;
		}
		System.out.println();
	}

	// Deletes the first occurrence of the specified element
	public void delete(T data) {
		Node<T> current = dummy.next;
		while (current != dummy) {
			if (current.data.equals(data)) {
				current.previous.next = current.next;
				current.next.previous = current.previous;
				size--;
				return;
			}
			current = current.next;
		}
	}

	// Displays the contents of the list in sorted order
	public void displaySorted() {
		CircularDoublyLinkedList<T> sortedList = new CircularDoublyLinkedList<>();
		Node<T> current = dummy.next;
		while (current != dummy) {
			sortedList.insertSorted(current.data);
			current = current.next;
		}
		sortedList.display();
	}

	// Checks if the list contains the specified element
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

	// Checks if the list is empty
	public boolean isEmpty() {
		return size == 0;
	}

	// Removes all elements from the list
	public void clear() {
		dummy.next = dummy;
		dummy.previous = dummy;
		size = 0;
	}

	// Returns an iterator for the list
	@Override
	public Iterator<T> iterator() {
		return new CircularDoublyLinkedListIterator(dummy);
	}

	// helps go through list
	private class CircularDoublyLinkedListIterator implements Iterator<T> {
		private Node<T> current;
		private Node<T> dummy;
		private boolean firstPass = true;

		CircularDoublyLinkedListIterator(Node<T> dummy) {
			this.dummy = dummy;
			this.current = dummy.next;
		}

		@Override
		public boolean hasNext() {
			return current != dummy || firstPass;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T data = current.data;
			current = current.next;
			firstPass = false;
			return data;
		}
	}

	// adds item in sorted order
	public void insertSorted(T data) {
		if (size == 0) {
			insertLast(data);
			return;
		}

		if (data instanceof UserManager) {
			UserManager newUser = (UserManager) data;
			Node<T> current = dummy.next;
			while (current != dummy) {
				UserManager currentUser = (UserManager) current.data;
				if (newUser.compareTo(currentUser) < 0) {
					Node<T> newNode = new Node<>(data);
					newNode.next = current;
					newNode.previous = current.previous;
					current.previous.next = newNode;
					current.previous = newNode;
					size++;
					return;
				}
				current = current.next;
			}
		} else {
			Node<T> current = dummy.next;
			while (current != dummy) {
				if (((Comparable<T>) data).compareTo(current.data) < 0) {
					Node<T> newNode = new Node<>(data);
					newNode.next = current;
					newNode.previous = current.previous;
					current.previous.next = newNode;
					current.previous = newNode;
					size++;
					return;
				}
				current = current.next;
			}
		}
		insertLast(data);
	}

	// compares two items
	private int compare(T a, T b) {
		if (a instanceof UserManager && b instanceof UserManager) {
			UserManager userA = (UserManager) a;
			UserManager userB = (UserManager) b;
			return userA.getName().compareToIgnoreCase(userB.getName());
		}
		return 0;
	}

	// sorts list using bubble sort
	public void sort() {
		if (dummy.next == dummy || size <= 1) {
			return;
		}

		boolean swapped;
		Node<T> current;
		Node<T> last = null;

		do {
			swapped = false;
			current = dummy.next;

			while (current.next != last) {
				if (compare(current.data, current.next.data) > 0) {
					T temp = current.data;
					current.data = current.next.data;
					current.next.data = temp;
					swapped = true;
				}
				current = current.next;
			}
			last = current;
		} while (swapped);
	}

	// another way to sort list
	public void bubbleSort() {
		if (dummy.next == dummy || size <= 1) {
			return;
		}

		boolean swapped;
		do {
			swapped = false;
			Node<T> current = dummy.next;
			Node<T> next = current.next;
			
			do {
				if (current.data instanceof UserManager) {
					UserManager user1 = (UserManager) current.data;
					UserManager user2 = (UserManager) next.data;
					if (user1.getName().compareTo(user2.getName()) > 0) {
						T temp = current.data;
						current.data = next.data;
						next.data = temp;
						swapped = true;
					}
				}
				current = next;
				next = next.next;
			} while (next != dummy);
		} while (swapped);
	}
}
