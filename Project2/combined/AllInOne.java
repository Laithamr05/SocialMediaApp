	public void insertSorted(T data) { // method to insert in sorted order based on name
		// If the list is empty, just insert the element
		if (isEmpty()) {
			insertFirst(data);
			return;
		}
		
		// For UserManager, sort by name
		if (data instanceof UserManager) {
			UserManager newUser = (UserManager) data;
			String newName = newUser.getName();
			
			Node<T> current = dummy.next;
			// Find the position where the name is greater
			while (current != dummy) {
				UserManager currentUser = (UserManager) current.data;
				String currentName = currentUser.getName();
				
				// Compare names alphabetically
				if (newName.compareToIgnoreCase(currentName) < 0) {
					break;
				}
				
				current = current.next;
			}
			
			// Insert before the current node
			Node<T> newNode = new Node<>(data);
			newNode.next = current;
			newNode.previous = current.previous;
			current.previous.next = newNode;
			current.previous = newNode;
		}
		// For other types, use the natural order defined by compareTo
		else {
			Node<T> current = dummy.next;
			while (current != dummy && data.compareTo(current.data) > 0) {
				current = current.next;
			}
			
			// Insert before the current node
			Node<T> newNode = new Node<>(data);
			newNode.next = current;
			newNode.previous = current.previous;
			current.previous.next = newNode;
			current.previous = newNode;
		}
	} 