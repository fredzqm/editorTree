package binarySearchTree;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * 
 * Implementation of most of the Set interface operations using a Binary Search
 * Tree
 *
 * @author Matt Boutell and <<< YOUR NAME HERE >>>.
 * @param <T>
 */

public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
	public final NullBinaryNode NULL_NODE = new NullBinaryNode();
	private BinaryNode root;
	private int treeVersion;

	/**
	 * 
	 * construct a Binary search tree with its root as a null node.
	 */
	public BinarySearchTree() {
		root = NULL_NODE;
		treeVersion = 0;
	}

	// For manual tests only
	void setRoot(BinaryNode n) {
		this.root = n;
	}

	/**
	 * 
	 * @return true if the the list is empty
	 */
	public boolean isEmpty() {
		if (root == NULL_NODE) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 *
	 * @return the size of the array
	 */
	public int size() {
		return root.size();
	}

	/**
	 * 
	 *
	 * @return the height of the whole tree
	 */
	public int height() {
		return root.height();
	}

	/**
	 * 
	 * check each node of the tree recursively, to see if the left subnode is
	 * smaller and right subnode is larger.
	 * 
	 * @param item
	 *            the item to search for
	 * @return true if the tree is not ordered.
	 */
	public boolean containsNonBST(T item) {
		return root.containsNonBST(item);
	}

	/**
	 * This method loops through all the element in the tree and is O(n).
	 *
	 * @return the arraylist counterpart of this tree
	 */
	public ArrayList<T> toArrayList() {
		ArrayList<T> list = new ArrayList<T>();
		root.addItemsToList(list);
		return list;
	}

	/**
	 * 
	 *
	 * @return the array counterpart of this tree
	 */
	public Object[] toArray() {
		ArrayList<T> list = toArrayList();
		int length = list.size();
		Object[] array = new Object[length];
		for (int i = 0; i < length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	@Override
	public String toString() {
		return toArrayList().toString();
	}

	// binary sorted tree methods below
	/**
	 * insert an element into the tree, assuming that the tree is already
	 * ordered.
	 *
	 * @param item
	 * @return true if this element is sucessfully inserted; false if there is
	 *         an identical element in the tree already
	 */
	public boolean insert(T item) {
		if (item == null)
			throw new IllegalArgumentException();
		Boolean b = new Boolean();
		root = root.insert(item, b);
		if (b.get())
			treeVersion++;
		return b.get();
	}

	/**
	 * 
	 * @param item
	 *            the element to check for
	 * @return true if this element is contained in the tree
	 */
	public boolean contains(T item) {
		if (item == null)
			return false;
		return root.contains(item);
	}

	/**
	 * 
	 * @param item
	 *            the item to remove
	 * @return true if the item is found in the tree and successfully removed;
	 *         false if this elemen is not in the tree
	 */
	public boolean remove(T item) {
		if (item == null)
			throw new IllegalArgumentException();
		Boolean b = new Boolean();
		root = root.remove(item, b);
		if (b.get())
			treeVersion++;
		return b.get();
	}

	/**
	 * A super simple class with only one field -- a boolean. It just helps tree
	 * keep track whether insert, remove are successful.
	 * 
	 * @author zhangq2. Created Mar 26, 2015.
	 */
	class Boolean {
		boolean found;

		Boolean() {
			found = false;
		}

		public void set(boolean a) {
			found = a;
		}

		public boolean get() {
			return found;
		}
	}

	// Not private, since we need access for manual testing.
	class BinaryNode {
		private T data;
		protected BinaryNode left;
		protected BinaryNode right;

		public BinaryNode(T element) {
			this.data = element;
			this.left = NULL_NODE;// NULL_NODE;s
			this.right = NULL_NODE;// NULL_NODE;
		}

		public T getData() {
			return this.data;
		}

		public BinaryNode getLeft() {
			return this.left;
		}

		public BinaryNode getRight() {
			return this.right;
		}

		// For manual testing
		public void setLeft(BinaryNode left) {
			this.left = left;
		}

		public void setRight(BinaryNode right) {
			this.right = right;
		}

		/**
		 * 
		 * @return the size of the subtree from this node
		 */
		public int size() {
			return left.size() + right.size() + 1;
		}

		/**
		 * 
		 * the height of an empty node is -1
		 * 
		 * @return the height of the subtree from this node
		 */
		public int height() {
			return 1 + Math.max(left.height(), right.height());
		}

		/**
		 * recursively check whether item is in the whole tree, without the
		 * assumption that the tree is sorted
		 *
		 * @param item
		 * @return true if the element is included in this tree
		 */
		public boolean containsNonBST(T item) {
			if (data.equals(item))
				return true;
			return left.containsNonBST(item) || right.containsNonBST(item);
		}

		/**
		 * used to implement {@link BinarySearchTree#toArrayList()}.
		 * 
		 * add elements into the list in order.
		 * 
		 * @param list
		 *            add element of left subtree, itself and right subtree to
		 *            this arrayList, so item will be in the order.
		 */
		public void addItemsToList(ArrayList<T> list) {
			left.addItemsToList(list);
			list.add(this.getData());
			right.addItemsToList(list);
		}

		@Override
		public String toString() {
			ArrayList<T> list = new ArrayList<T>();
			addItemsToList(list);
			return list.toString();
		}

		// binary sorted tree methods
		/**
		 * 
		 *
		 * @param item
		 *            item to be inserted
		 * @param b
		 *            the Boolean object to keep track of whehter insertion is
		 *            successful or not
		 * @return the updated node in this side; itself if no changed is made
		 *         in this subtree
		 */
		public BinaryNode insert(T item, Boolean b) {
			if (data.compareTo(item) > 0) {
				left = left.insert(item, b);
			} else if (data.compareTo(item) < 0) {
				right = right.insert(item, b);
			} else {
				return this;
			}
			return this;
		}

		/**
		 * check if item element is in this subtree
		 *
		 * @param item
		 * @return true if item is found here
		 */
		public boolean contains(T item) {
			if (getData().equals(item))
				return true;
			if (getData().compareTo(item) > 0)
				return left.contains(item);
			return right.contains(item);
		}

		/**
		 * 
		 *
		 * @param item
		 * @param b
		 *            the Boolean object to keep track of whether removal is
		 *            successful or not
		 * @return an updated node in this position; itself if nothing is
		 *         changed inside
		 */
		public BinaryNode remove(T item, Boolean b) {
			if (getData().equals(item)) {
				b.set(true);
				if (left != NULL_NODE && right != NULL_NODE) {
					replaceItselfByLeft();
					return this;
				} else if (left != NULL_NODE) {
					return left;
				} else if (right != NULL_NODE) {
					return right;
				} else {
					return NULL_NODE;
				}
			}
			if (getData().compareTo(item) > 0) {
				left = left.remove(item, b);
			} else {
				right = right.remove(item, b);
			}
			return this;
		}

		/**
		 * replace this node by the largest element in its left subtree. This
		 * method is used when we want to remove a node with both left and right
		 * subtrees
		 */
		private void replaceItselfByLeft() {
			if (left.right == NULL_NODE) {
				data = left.data;
				left = left.left;
			} else
				data = left.removeLargest();
		}

		/**
		 * 
		 * remove the largest element in the subtree
		 * 
		 * @return the data of the largest element
		 */
		private T removeLargest() {
			if (right.right == NULL_NODE) {
				T largest = right.data;
				right = NULL_NODE;
				return largest;
			}
			return right.removeLargest();
		}
	}

	/**
	 * 
	 * A subclass of BinaryNode to represent NULL_NODE. It has lots of method
	 * overiding its superclass, so we can avoid null pointer exception and if
	 * stated in each recursion to check if the node is NULL_NODE
	 * 
	 * @author zhangq2. Created Mar 26, 2015.
	 */
	class NullBinaryNode extends BinaryNode {

		public NullBinaryNode() {
			super(null);
			left = this;
			right = this;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public int height() {
			return -1;
		}

		@Override
		public boolean containsNonBST(T item) {
			return false;
		}

		@Override
		public void addItemsToList(ArrayList<T> list) {
			// there is nothing in Null node, so just skip this node and move
			// on.
		}

		@Override
		public BinaryNode insert(T item, Boolean b) {
			b.set(true);
			return new BinaryNode(item);
		}

		@Override
		public boolean contains(T item) {
			return false;
		}

		@Override
		public BinaryNode remove(T item, Boolean b) {
			return this;
		}
	}

	public Iterator<T> inefficientIterator() {
		return toArrayList().iterator();
	}

	private class PreOrderIterator implements Iterator<T> {
		private Stack<BinaryNode> stack;
		private BinaryNode current;
		private int version;

		public PreOrderIterator() {
			stack = new Stack<BinaryNode>();
			version = treeVersion;
			current = NULL_NODE;
			if (root != NULL_NODE)
				stack.push(root);
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			if (treeVersion != version)
				throw new ConcurrentModificationException();
			current = stack.pop();
			if (current.getRight() != NULL_NODE)
				stack.push(current.getRight());
			if (current.getLeft() != NULL_NODE)
				stack.push(current.getLeft());
			return current.getData();
		}

		@Override
		public void remove() {
			if (current == NULL_NODE) {
				throw new IllegalStateException();
			}
			BinarySearchTree.this.remove(current.data);
			version = treeVersion;
			current = NULL_NODE;
		}
	}

	public Iterator<T> preOrderIterator() {
		return new PreOrderIterator();
	}

	private class InOrderIterator implements Iterator<T> {
		private Stack<BinaryNode> stack;
		private int version;
		private BinaryNode current;
		
		public InOrderIterator() {
			version = treeVersion;
			stack = new Stack<BinarySearchTree<T>.BinaryNode>();
			current = NULL_NODE;
			advance(root);
		}

		private void advance(BinaryNode node) {
			while (node != NULL_NODE) {
				stack.push(node);
				node = node.getLeft();
			}
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			if (treeVersion != version)
				throw new ConcurrentModificationException();
			current = stack.pop();
			BinaryNode next = current.getRight();
			while (next != NULL_NODE) {
				stack.push(next);
				next = next.getLeft();
			}
			return current.getData();
		}

		@Override
		public void remove() {
			if (current == NULL_NODE) {
				throw new IllegalStateException();
			}
			BinarySearchTree.this.remove(current.data);
			version = treeVersion;
			current = NULL_NODE;
		}
	}

	public Iterator<T> postOrderIterator() {
		return new postOrderIterator();
	}

	private class postOrderIterator implements Iterator<T> {
		private Stack<BinaryNode> stack;
		private int version;
		private BinaryNode current;

		public postOrderIterator() {
			version = treeVersion;
			stack = new Stack<BinarySearchTree<T>.BinaryNode>();
			current = root;
			while (current != NULL_NODE) {
				while (current != NULL_NODE) {
					stack.push(current);
					current = current.getLeft();
				}
				current = stack.peek().getRight();
			}
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			if (treeVersion != version)
				throw new ConcurrentModificationException();
			BinaryNode next = stack.peek().getRight();
			if (next != current || current == NULL_NODE) {
				while (next != NULL_NODE) {
					while (next != NULL_NODE) {
						stack.push(next);
						next = next.getLeft();
					}
					next = stack.peek().getRight();
				}
			}
			current = stack.pop();
			return current.getData();
		}

		@Override
		public void remove() {
			if (current == NULL_NODE) {
				throw new IllegalStateException();
			}
			BinarySearchTree.this.remove(current.data);
			current = NULL_NODE;
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new InOrderIterator();
	}

	class MysteryIterator implements Iterator<T> {
		LinkedList<BinaryNode> quene;

		public MysteryIterator() {
			quene = new LinkedList<BinaryNode>();
			quene.offer(root);
		}

		@Override
		public boolean hasNext() {
			return !quene.isEmpty();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			BinaryNode next = quene.poll();
			if (next.left != NULL_NODE)
				quene.offer(next.left);
			if (next.right != NULL_NODE)
				quene.offer(next.right);
			return next.getData();
		}

	}

	public Iterator<T> mysteryIterator() {
		return new MysteryIterator();
	}

}
