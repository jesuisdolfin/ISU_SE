package edu.iastate.cs228.hw3;

import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author Charlie Dolphin
 */

/**
 * Implementation of the list interface based on linked nodes that store
 * multiple items per node. Rules for adding and removing elements ensure that
 * each node (except possibly the last one) is at least half full.
 */
public class StoutList<E extends Comparable<? super E>> extends AbstractSequentialList<E>
{
	/**
	 * Default number of elements that may be stored in each node.
	 */
	private static final int DEFAULT_NODESIZE = 4;

	/**
	 * Number of elements that can be stored in each node.
	 */
	private final int nodeSize;

	/**
	 * Dummy node for head. It should be private but set to public here only for
	 * grading purpose. In practice, you should always make the head of a linked
	 * list a private instance variable.
	 */
	public Node head;

	/**
	 * Dummy node for tail.
	 */
	private Node tail;

	/**
	 * Number of elements in the list.
	 */
	private int size;

	/**
	 * Constructs an empty list with the default node size.
	 */
	public StoutList() {
		this(DEFAULT_NODESIZE);
	}

	/**
	 * Constructs an empty list with the given node size.
	 * 
	 * @param nodeSize number of elements that may be stored in each node, must be
	 *                 an even number
	 */
	public StoutList (int nodeSize) {
		if (nodeSize <= 0 || (nodeSize % 2) != 0) {
            throw new IllegalArgumentException();
        }
			
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		this.nodeSize = nodeSize;
	}

	/**
	 * Constructor for grading only. Fully implemented.
	 * 
	 * @param head
	 * @param tail
	 * @param nodeSize
	 * @param size
	 */
	public StoutList (Node head, Node tail, int nodeSize, int size) {
		this.head = head;
		this.tail = tail;
		this.nodeSize = nodeSize;
		this.size = size;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean add(E item) throws NullPointerException {

		if (item == null) {
			throw new NullPointerException();
		}

		add(size, item);

		return true;
	}

	@Override
	public void add(int pos, E item) {
        if (item == null) {
			throw new NullPointerException();
		}
		if (pos < 0 || pos > size) {
			throw new IndexOutOfBoundsException();
		}

		StoutListNode ni = find(pos);
		add(ni.stoutNode, item, ni.offset);
	}

	@Override
	public E remove(int pos) {

		if (pos < 0 || pos >= size) {
			throw new IndexOutOfBoundsException();
		}

		StoutListNode ni = find(pos);
		return remove(ni);
	}

	/**
	 * Sort all elements in the stout list in the NON-DECREASING order. You may do
	 * the following. Traverse the list and copy its elements into an array,
	 * deleting every visited node along the way. Then, sort the array by calling
	 * the insertionSort() method. (Note that sorting efficiency is not a concern
	 * for this project.) Finally, copy all elements from the array back to the
	 * stout list, creating new nodes for storage. After sorting, all nodes but
	 * (possibly) the last one must be full of elements.
	 * 
	 * Comparator<E> must have been implemented for calling insertionSort().
	 */
	public void sort() {
		E[] dataArr = (E[]) new Comparable[size()];
		Iterator<E> newIterator = iterator();

		for (int i = 0; i < size; i++) {
			dataArr[i] = newIterator.next();
		}

		head.next = tail;
		tail.previous = head;
		size = 0;
		insertionSort(dataArr, new ObjectComparator()); // insertion sort
	}

	/**
	 * Sort all elements in the stout list in the NON-INCREASING order. Call the
	 * bubbleSort() method. After sorting, all but (possibly) the last nodes must be
	 * filled with elements.
	 * 
	 * Comparable<? super E> must be implemented for calling bubbleSort().
	 */
	public void sortReverse() {
		E[] revDataArr = (E[]) new Comparable[size];
		Iterator<E> iterator = iterator();

		for (int i = 0; i < size; i++) {
			revDataArr[i] = iterator.next();
		}

		head.next = tail;
		tail.previous = head;
		size = 0;
		bubbleSort(revDataArr);
		this.addAll(Arrays.asList(revDataArr));
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException();
		}

		return new StoutListIterator(index);
	}

	/**
	 * Returns a string representation of this list showing the internal structure
	 * of the nodes.
	 */
	public String toStringInternal() {
		return toStringInternal(null);
	}

	/**
	 * Returns a string representation of this list showing the internal structure
	 * of the nodes and the position of the iterator.
	 *
	 * @param iter an iterator for this list
	 */
	public String toStringInternal(ListIterator<E> iter) {
		int count = 0;
		int position = -1;
		if (iter != null) {
			position = iter.nextIndex();
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		Node current = head.next;
		while (current != tail) {
			sb.append('(');
			E data = current.data[0];
			if (data == null) {
				sb.append("-");
			}
			else {
				if (position == count) {
					sb.append("| ");
					position = -1;
				}
				sb.append(data.toString());
				++count;
			}

			for (int i = 1; i < nodeSize; ++i) {
				sb.append(", ");
				data = current.data[i];
				if (data == null) {
					sb.append("-");
				}
				else {
					if (position == count) {
						sb.append("| ");
						position = -1;
					}
					sb.append(data.toString());
					++count;

					// iterator at end
					if (position == size && count == size) {
						sb.append(" |");
						position = -1;
					}
				}
			}
			sb.append(')');
			current = current.next;
			if (current != tail)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Node type for this list. Each node holds a maximum of nodeSize elements in an
	 * array. Empty slots are null.
	 */
	private class Node {
		/**
		 * Array of actual data elements.
		 */
		// Unchecked warning unavoidable.
		public E[] data = (E[]) new Comparable[nodeSize];

		/**
		 * Link to next node.
		 */
		public Node next;

		/**
		 * Link to previous node;
		 */
		public Node previous;

		/**
		 * Index of the next available offset in this node, also equal to the number of
		 * elements in this node.
		 */
		public int count;

		/**
		 * Adds an item to this node at the first available offset. Precondition: count
		 * < nodeSize
		 * 
		 * @param item element to be added
		 */
		void addItem(E item) {
			if (count >= nodeSize) {
				return;
			}
			data[count++] = item;
			// useful for debugging
			// System.out.println("Added " + item.toString() + " at index " + count + " to
			// node " + Arrays.toString(data));
		}

		/**
		 * Adds an item to this node at the indicated offset, shifting elements to the
		 * right as necessary.
		 * 
		 * Precondition: count < nodeSize
		 * 
		 * @param offset array index at which to put the new element
		 * @param item   element to be added
		 */
		void addItem(int offset, E item) {
			if (count >= nodeSize) {
				return;
			}
			for (int i = count - 1; i >= offset; --i) {
				data[i + 1] = data[i];
			}
			++count;
			data[offset] = item;
			// useful for debugging
//      System.out.println("Added " + item.toString() + " at index " + offset + " to node: "  + Arrays.toString(data));
		}

		/**
		 * Deletes an element from this node at the indicated offset, shifting elements
		 * left as necessary. Precondition: 0 <= offset < count
		 * 
		 * @param offset
		 */
		void removeItem(int offset) {
			E item = data[offset];
			for (int i = offset + 1; i < nodeSize; ++i) {
				data[i - 1] = data[i];
			}
			data[count - 1] = null;
			--count;
		}
	}

	private class StoutListIterator implements ListIterator<E> {
		// constants you possibly use ...

		// instance variables ...
		private int currIndex;

		private StoutListNode lastAccessed;

		private boolean canRemove;

		/**
		 * Default constructor
		 */
		public StoutListIterator() {
			currIndex = 0;
			lastAccessed = null;
			canRemove = false;
		}

		/**
		 * Constructor that finds node at a given position.
		 * 
		 * @param pos
		 */
		public StoutListIterator (int pos) {
			if (pos < 0 || pos > size) {
				throw new IndexOutOfBoundsException();
			}

			currIndex = pos;
			lastAccessed = null;
			canRemove = false;
		}

		@Override
		public boolean hasNext() {
			return (currIndex < size);
		}

		@Override
		public E next() {
			if (hasNext()) {
				StoutListNode slNode = find(currIndex++);
				lastAccessed = slNode;
				canRemove = true;
				return (slNode.stoutNode.data[slNode.offset]);
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			if (!canRemove) {
				throw new IllegalStateException();
			}

			StoutListNode currentNodeIndex = find(currIndex);

			if (lastAccessed.stoutNode != currentNodeIndex.stoutNode || lastAccessed.offset < currentNodeIndex.offset) {
				currIndex--; // decrement the index
			}
			StoutList.this.remove(lastAccessed);
			lastAccessed = null;
			canRemove = false;
		}

		// Other methods you may want to add or override that could possibly facilitate
		// other operations, for instance, addition, access to the previous element,
		// etc.
		//
		// ...
		//

		@Override
		public int nextIndex() {
			return currIndex;
		}

		@Override
		public boolean hasPrevious() {
			return currIndex > 0;
		}

		@Override
		public int previousIndex() {
			return currIndex - 1;
		}

		@Override
		public E previous() {
			if (hasPrevious()) {
				StoutListNode slNode;

				if (currIndex == size) {
					slNode = find(--currIndex);

				}
				else {
					slNode = find(--currIndex);
					lastAccessed = slNode;
				}

				canRemove = true;
				return slNode.stoutNode.data[slNode.offset];
			}

			throw new NoSuchElementException();
		}

		@Override
		public void set(E item) {
			if (item == null) {
				throw new NullPointerException();
			}
			if (!canRemove) {
				throw new IllegalStateException();
			}
			lastAccessed.stoutNode.data[lastAccessed.offset] = item;
		}

		@Override
		public void add(E item) {
			if (item == null) {
				throw new NullPointerException();
			}

			canRemove = false;
			StoutList.this.add(currIndex++, item);
		}
	}

	/**
	 * Sort an array arr[] using the insertion sort algorithm in the NON-DECREASING
	 * order.
	 * 
	 * @param arr  array storing elements from the list
	 * @param comp comparator used in sorting
	 */
	private void insertionSort(E[] arr, Comparator<? super E> comp) {
		for (int i = 1; i < arr.length; i++) {
			E temp = arr[i];
			int prevIndex = i - 1;

			while (prevIndex > -1 && comp.compare(arr[prevIndex], temp) > 0) {
				arr[prevIndex + 1] = arr[prevIndex];
				prevIndex--;
			}
			arr[prevIndex + 1] = temp;
		}
		this.addAll(Arrays.asList(arr));
	}

	/**
	 * Sort arr[] using the bubble sort algorithm in the NON-INCREASING order. For a
	 * description of bubble sort please refer to Section 6.1 in the project
	 * description. You must use the compareTo() method from an implementation of
	 * the Comparable interface by the class E or ? super E.
	 * 
	 * @param arr array holding elements from the list
	 */
	private void bubbleSort(E[] arr) {
		boolean hasSwapped = false;

		for (int i = 1; i < arr.length; i++) {
			if (arr[i - 1].compareTo(arr[i]) < 0) {
				swap(arr, i - 1, i);
				hasSwapped = true;
			}
		}

		if (hasSwapped == true) {
			bubbleSort(arr);
		}
	}

	private void swap(E[] arr, int i, int j) {
		E temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	/*
	 * Class representing a node and its offset within the StoutList.
	 */
	private class StoutListNode {
		public Node stoutNode;
		public int offset;

		public StoutListNode (Node node, int offset) {
			this.stoutNode = node;
			this.offset = offset;
		}
	}

	/**
	 * Method for connecting two nodes.
	 */
	private void link(Node currNode, Node otherNode) {
		otherNode.previous = currNode;
		otherNode.next = currNode.next;
		currNode.next.previous = otherNode;
		currNode.next = otherNode;
	}

	/**
	 * Unlink a node
	 */
	private void unlink(Node currNode) {
		currNode.previous.next = currNode.next;
		currNode.next.previous = currNode.previous;
	}

	/**
	 * Method to find the StoutListNode (node and offset within node) corresponding
	 * to the given position in the list.
	 *
	 * @param position the position of the element in the list
	 * @return StoutListNode object containing the node and offset of the element
	 */
	private StoutListNode find(int position) {
		if (position == -1) {
            return new StoutListNode(head, 0);
        }
		if (position == size) {
            return new StoutListNode(tail, 0);
        }
			
		Node currentNode = head.next;
		int index = currentNode.count - 1;

		while (currentNode != tail && position > index) {
			currentNode = currentNode.next;
			index += currentNode.count;
		}
		int nodeOffset = currentNode.count + position - index - 1;
		return new StoutListNode(currentNode, nodeOffset);
	}

	/**
	 * Add an element e at a certain position
	 */
	private StoutListNode add(Node stoutNode, E e, int offset) {
		if (e == null) {
			throw new NullPointerException();
		}

		StoutListNode slNode;

		if (offset == 0) {
			if (size == 0) {
				Node newNodeData = new Node();
				newNodeData.addItem(e);
				link(head, newNodeData);
				slNode = new StoutListNode(newNodeData, 0);
			}
			else if (stoutNode.previous.count < nodeSize && stoutNode.previous != head) {
				stoutNode.previous.addItem(e);
				slNode = new StoutListNode(stoutNode.previous, stoutNode.previous.count - 1);
			}
			else if (stoutNode == tail && stoutNode.previous.count == nodeSize) {
				Node newNodeData = new Node();
				newNodeData.addItem(e);
				link(tail.previous, newNodeData);
				slNode = new StoutListNode(newNodeData, 0);
			}
			else {
				stoutNode.addItem(offset, e);
				slNode = new StoutListNode(stoutNode, offset);
			}
		}
		else {
			if (stoutNode.count < nodeSize) {
				stoutNode.addItem(offset, e);
				slNode = new StoutListNode(stoutNode, offset);
			}
			else {
				Node newNodeData = new Node();
				link(stoutNode, newNodeData);

				for (int i = nodeSize - 1; i >= nodeSize - nodeSize / 2; i--) {
					newNodeData.addItem(0, stoutNode.data[i]);
					stoutNode.removeItem(i);
				}
				if (offset <= nodeSize / 2) {
					stoutNode.addItem(offset, e);
					slNode = new StoutListNode(stoutNode, offset);
				}
				else {
					newNodeData.addItem(offset - nodeSize / 2, e);
					slNode = new StoutListNode(newNodeData, offset - nodeSize / 2);
				}
			}
		}

		size++;
		return slNode;
	}

	/**
	 * Removes node from the StoutList
	 */
	private E remove(StoutListNode node) {
		E item = node.stoutNode.data[node.offset];

		if (node.stoutNode.next == tail && node.stoutNode.count == 1) {
			unlink(node.stoutNode);
		}
		else if (node.stoutNode.next == tail || node.stoutNode.count > nodeSize / 2) {
			node.stoutNode.removeItem(node.offset);
		}
		else if (node.stoutNode.count <= nodeSize / 2) {
			node.stoutNode.removeItem(node.offset);

			if (node.stoutNode.next.count > nodeSize / 2) {
				node.stoutNode.addItem(node.stoutNode.next.data[0]);
				node.stoutNode.next.removeItem(0);
			}
			else {
				for (E element : node.stoutNode.next.data) {
					if (element != null) {
						node.stoutNode.addItem(element);
					}
				}

				unlink(node.stoutNode.next);
			}
		}
		size--;
		return item;
	}

	/**
	 * Object comparator class to compare two Type E objects.
	 */
	private static class ObjectComparator<E extends Comparable<? super E>> implements Comparator<E> {
		@Override
		public int compare(E x, E y) {
			return x.compareTo(y);
		}
	}
}