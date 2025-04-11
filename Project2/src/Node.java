// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

public class Node<T> { // generic node class for linked list implementation
	T data; // data stored in the node
	Node<T> next; // reference to the next node
	Node<T> previous; // reference to the previous node

	public Node(T data) { // constructor to create a new node with data
		this.data = data; // set the data
		this.next = null; // initialize next as null
		this.previous = null; // initialize previous as null
	}
}
