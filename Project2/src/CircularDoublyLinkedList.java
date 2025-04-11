// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.util.Collections;
import java.util.ArrayList;

public class CircularDoublyLinkedList<T extends Comparable<T>> { // generic circular doubly linked list implementation
	Node<T> dummy; // dummy node that serves as sentinel for the list

	public CircularDoublyLinkedList() { // constructor to initialize an empty list
		dummy = new Node<>(null); // create dummy node with null data
		dummy.next = dummy; // point next to itself (empty list)
		dummy.previous = dummy; // point previous to itself (empty list)
	}

	public void add(T data) { // method to add data to the list
		insertLast(data); // insert at the end of list
	}

	public void insertFirst(T data) { // method to insert at the beginning of the list
		Node<T> newNode = new Node(data); // create new node with data
		newNode.next = dummy.next; // link new node to first real node
		newNode.previous = dummy; // link new node back to dummy
		dummy.next.previous = newNode; // update first node's previous to new node
		dummy.next = newNode; // update dummy's next to new node
	}

	public void insertLast(T data) { // method to insert at the end of the list
		Node<T> newNode = new Node<>(data); // create new node with data
		newNode.previous = dummy.previous; // link new node to last real node
		newNode.next = dummy; // link new node to dummy
		dummy.previous.next = newNode; // update last node's next to new node
		dummy.previous = newNode; // update dummy's previous to new node
	}

	public void insertMiddle(T node, T data) { // method to insert after a specific node
		if (!contains(node)) { // check if specified node exists
			return;
		}

		Node<T> current = dummy.next; // start at first real node
		while (current != dummy) { // iterate through list
			if (current.data.equals(node)) { // find the node to insert after
				Node<T> newNode = new Node<>(data); // create new node with data
				newNode.next = current.next; // link new node to node after current
				newNode.previous = current; // link new node back to current
				current.next.previous = newNode; // update next node's previous to new node
				current.next = newNode; // update current's next to new node
				return;
			}
			current = current.next; // move to next node
		}
	}

	public int size() { // method to get size of the list
		int count = 0; // initialize counter
		Node<T> current = dummy.next; // start at first real node
		while (current != dummy) { // iterate through list
			count++; // increment counter
			current = current.next; // move to next node
		}
		return count; // return final count
	}

	public void display() { // method to display all elements in the list
		Node<T> current = dummy.next; // start at first real node
		while (current != dummy) { // iterate through list
			System.out.println(current.data); // print current node's data
			current = current.next; // move to next node
		}
	}

	public void delete(T data) { // method to delete a node with specific data
		if (!contains(data)) { // check if data exists in list
			return;
		}

		Node<T> current = dummy.next; // start at first real node
		while (current != dummy) { // iterate through list
			if (current.data.equals(data)) { // find node with matching data
				current.previous.next = current.next; // update previous node's next
				current.next.previous = current.previous; // update next node's previous
				current.next = null; // clear reference to next
				current.previous = null; // clear reference to previous
				return;
			}
			current = current.next; // move to next node
		}
	}

	public void displaySorted() { // method to display elements in sorted order
		ArrayList<T> list = new ArrayList<T>(); // create temporary list
		Node<T> current = dummy.next; // start at first real node
		while (current != dummy) { // iterate through list
			list.add(current.data); // add data to temporary list
			current = current.next; // move to next node
		}
		Collections.sort(list); // sort the temporary list
		for (int i = 0; i < list.size(); i++) { // iterate through sorted list
			T item = list.get(i); // get current item
			System.out.println(item); // print item
		}
	}

	public boolean contains(T data) { // method to check if list contains data
		Node<T> current = dummy.next; // start at first real node
		while (current != dummy) { // iterate through list
			if (current.data.equals(data)) { // check if current node's data matches
				return true; // found match
			}
			current = current.next; // move to next node
		}
		return false; // no match found
	}

	public boolean isEmpty() { // method to check if list is empty
		return dummy.next == dummy; // empty if dummy points to itself
	}

	public void clear() { // method to clear the list
		dummy.next = dummy; // point next to itself (empty list)
		dummy.previous = dummy; // point previous to itself (empty list)
	}

	public java.util.Iterator<T> iterator() { // method to get an iterator for the list
		return new CircularDoublyLinkedListIterator<>(dummy); // create new iterator with dummy node
	}
}
