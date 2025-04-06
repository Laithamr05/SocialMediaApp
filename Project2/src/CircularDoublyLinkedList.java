public class CircularDoublyLinkedList<T> {
	Node<T> dummy;
	Node head;
	private int size;

	public CircularDoublyLinkedList() {
		dummy = new Node<>(null);
		dummy.next = dummy;
		dummy.previous = dummy;
		size = 0;
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
		size++;
	}

	public void insertMiddle(T node, T data) {
		if (!contains(node)) {
			System.out.println("Node not found.");
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
		return size;
	}

	public void display() {
		int count = 1;
		if (dummy == null || dummy.next == dummy) {
			System.out.println("List is Empty");
			return;
		}
		Node<T> current = dummy.next;

		while (current != dummy) {
			System.out.println(count + ". " + current.data);
			current = current.next;
			count++;
		}

	}

	public boolean deleteAll() {

		Node<T> current = dummy.next;
		while (current != dummy) {
			dummy.next = null;
			dummy.previous = null;
			current = current.next;
		}
		dummy.next = dummy;
		dummy.previous = dummy;
		return true;
	}

	public void delete(T data) {
		if (!contains(data)) {
			System.out.println("Node not found");
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

	public void bubbleSort() {
		if (dummy.next == dummy || dummy.next.next == dummy) {
			return;
		}

		boolean swapped;
		do {
			swapped = false;
			Node<T> current = dummy.next;

			while (current.next != dummy) {
				if (((Comparable<T>) current.data).compareTo(current.next.data) > 0) {
					T temp = current.data;
					current.data = current.next.data;
					current.next.data = temp;
					swapped = true;
				}
				current = current.next;
			}
		} while (swapped);
	}

	public void displaySorted() {
		bubbleSort();
		int count = 1;
		if (dummy == null || dummy.next == dummy) {
			System.out.println("List is Empty");
			return;
		}
		Node<T> current = dummy.next;

		while (current != dummy) {
			System.out.println(count + ". " + current.data);
			current = current.next;
			count++;
		}
	}

	public boolean contains(T data) {
		if (size() == 0) {
			return false;
		}

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
		return size == 0;
	}

	public void clear() {
		dummy.next = dummy;
		dummy.previous = dummy;
		size = 0;
	}
}
