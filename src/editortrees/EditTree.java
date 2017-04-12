package editortrees;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

import debughelp.DisplayableBinaryTree;
import editortrees.Node.Code;

/**
 * 
 * @author zhang
 *
 */
public class EditTree implements CharSequence {
	private Node root;
	private int totalRotationCount;
	private int height;
	private int treeVersion;

	/**
	 * Construct an empty tree
	 */
	public EditTree() {
		root = Node.NULL_NODE;
		height = -1;
	}

	/**
	 * Construct a single-node tree whose element is c
	 * 
	 * @param c
	 */
	public EditTree(char c) {
		root = new Node(c);
		height = 0;
	}

	/**
	 * Create an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the length of the tree (repeatedly calling insert() would be
	 * O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		root = constructFromString(s, 0, s.length());
		height = balancedHeightFromSize(s.length());
		check();
	}

	/**
	 * construct a tree from a string recursively
	 * 
	 * @param string
	 *            original string
	 * @param start
	 *            index of the first element should be in this subtree
	 * @param end
	 *            index of the first element that should not in this subtree
	 *            after the included sequence
	 * @return root node of the constructed subtree
	 */
	private Node constructFromString(String string, int start, int end) {
		if (start == end)
			return Node.NULL_NODE;
		int mid = (start + end) / 2;
		int hl = balancedHeightFromSize(mid - start);
		int hr = balancedHeightFromSize(end - mid - 1);
		return new Node(string.charAt(mid), constructFromString(string, start, mid),
				constructFromString(string, mid + 1, end), Code.getCode(hr - hl));
	}

	private int balancedHeightFromSize(int length) {
		if (length == 0)
			return -1;
		int hl = (int) (Math.log(length) / Math.log(2));
		return hl;
	}

	/**
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		root = e.root.constructFromTree();
		height = e.height;
		check();
	}

	/**
	 * 
	 * returns the total number of rotations done in this tree since it was
	 * created. A double rotation counts as two.
	 *
	 * @return number of rotations since tree was created.
	 */
	public int totalRotationCount() {
		return totalRotationCount;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Character> itr = iterator();
		while (itr.hasNext()) {
			sb.append(itr.next());
		}
		return sb.toString();
	}

	/**
	 * This one asks for more info from each node. You can write it like the
	 * arraylist-based toString() method from the BST assignment. However, the
	 * output isn't just the elements, but the elements, ranks, and balance
	 * codes. Former CSSE230 students recommended that this method, while making
	 * it harder to pass tests initially, saves them time later since it catches
	 * weird errors that occur when you don't update ranks and balance codes
	 * correctly. For the tree with node b and children a and c, it should
	 * return the string: [b1=, a0=, c0=] There are many more examples in the
	 * unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in a
	 *         pre-order traversal of the tree.
	 */
	public String toDebugString() {
		if (root == Node.NULL_NODE)
			return "[]";
		String s = "[" + root.toDebugString();
		return s.substring(0, s.length() - 2) + "]";
	}

	/**
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		return height;
	}

	/**
	 * 
	 * @return the number of nodes in this tree
	 */
	@Override
	public int length() {
		return root.size();
	}

	/**
	 * 
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	@Override
	public char charAt(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= length())
			throw new IndexOutOfBoundsException();
		return root.get(pos);
	}

	/**
	 * This method operates in O(length*log N), where N is the size of this
	 * tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		return subSequence(pos, pos+length).toString();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if (start < 0 || end > length())
			throw new IndexOutOfBoundsException();
		StringBuilder sb = new StringBuilder();
		root.get(start, end, sb);
		return sb.toString();
	}

	/**
	 * find the index of the first match substring inside this editor tree.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the positiovgfn in this tree of the first occurrence of s; -1 if
	 *         s does not occur
	 */
	public int find(String s) {
		return find(s, 0);
	}

	/**
	 * find the index of the first match substring inside this editor tree after
	 * a given index.
	 * 
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		if (s.length() == 0)
			return pos;
		int l = root.find(s, pos, new LinkedList<Integer>());
		if (l == -1)
			return -1;
		return l - s.length() + 1;
	}

	/**
	 * add an element to the end of this tree
	 * 
	 * @param c
	 *            character to add to the end of this tree.
	 */
	public void add(char c) {
		add(c, length());
	}

	/**
	 * add an element to a specific positon
	 * 
	 * @param c
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             id pos is negative or too large for this tree
	 */
	public void add(char c, int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos > length())
			throw new IndexOutOfBoundsException();
		H a = new H();
		root = root.add(c, pos, a);
		totalRotationCount += a.rotate;
		if (!a.treeBalanced)
			height++;
		treeVersion++;
		check();
	}

	/**
	 * delete an elemeent from a specific position
	 * 
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= length())
			throw new IndexOutOfBoundsException();
		H a = new H();
		root = root.delete(pos, a);
		totalRotationCount += a.rotate;
		if (!a.treeBalanced)
			height--;
		treeVersion++;
		check();
		return a.deleted;
	}

	/**
	 * This method is provided for you, and should not need to be changed. If
	 * split() and concatenate() are O(log N) operations as required, delete
	 * should also be O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length) throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.length())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete" : "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
		if (this == other)
			throw new IllegalArgumentException();
		int heightThis = height();
		int heightOther = other.height();
		H a = new H();
		if (heightThis >= heightOther) {
			// this tree is higher than the other tree
			if (heightOther == -1) {
				// other is empty, do nothing
			} else if (heightOther == 0) {
				this.add(other.root.getElement());
			} else {
				other.root = other.root.delete(0, a);
				if (!a.isBalancedAndRest())
					heightOther--;
				root = root.concatRight(a, other.root, heightThis - heightOther);
				if (!a.treeBalanced)
					this.height++;
			}
		} else {
			// this tree is lower than the other tree
			if (heightThis == -1) {
				this.root = other.root;
				this.height = other.height;
			} else if (heightThis == 0) {
				other.add(this.root.getElement(), 0);
				this.root = other.root;
				this.height = other.height;
			} else {
				this.root = root.delete(length() - 1, a);
				if (!a.isBalancedAndRest())
					heightThis--;
				this.root = other.root.concatLeft(a, root, heightOther - heightThis);
				this.height = other.height;
				if (!a.treeBalanced)
					this.height++;
			}
		}
		totalRotationCount += a.rotate + other.totalRotationCount;
		other.root = Node.NULL_NODE;
		other.height = -1;
		treeVersion++;
		check();
	}

	/**
	 * split this editor into two editor tree at the given position. node after
	 * that position will be removed from the original tree and returned
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= length())
			throw new IndexOutOfBoundsException();
		SH result = new SH();
		root.split(pos, height, result);
		// left tree
		this.root = result.leftRoot;
		this.height = result.leftHeight;
		treeVersion++;
		check();
		// right tree
		EditTree editTree = new EditTree();
		editTree.root = result.rightRoot;
		editTree.height = result.rightHeight;
		editTree.check();

		totalRotationCount += result.rotate;
		return editTree;
	}

	/**
	 * Another helper class for split, it stores the left and right tree splited
	 * and their height, so we can easily concanete it with another node
	 * 
	 * @author zhang
	 *
	 */
	public static class SH extends H {

		/**
		 * The result of the splited tree
		 */
		public Node leftRoot, rightRoot;

		/**
		 * the height of those two splited tree
		 */
		public int leftHeight, rightHeight;
	}

	/**
	 * 
	 * Helper class that helps us keep track of the status of various method
	 * that will change the tree structure, including add, delete, concatenate,
	 * split.
	 * 
	 * 
	 * @author zhangq2. Created Apr 21, 2015.
	 */
	public static class H {
		/**
		 * keep track of whether the tree is already balanced. true if the tree
		 * is already balanced and requires no more rotation to balance
		 */
		public boolean treeBalanced;

		/**
		 * store element that was removed some leave and will be used to help
		 * glue two subtree together.
		 */
		public char deleted;

		/**
		 * keep track of the number of rotation happened during the modification
		 * operation
		 */
		public int rotate;

		public boolean isBalancedAndRest() {
			if (treeBalanced) {
				treeBalanced = false;
				return true;
			}
			return false;
		}
	}

	/**
	 * I add this method to all methods in editor tree that will modify the
	 * structure of tree, so if there is no exception throw, I can be 100% sure
	 * that all fields have been updated and there is no quiet bugs. Maybe this
	 * would be a good approach for later courses, to speed up the debugging
	 * process and catch all bugs where they are born.
	 * 
	 * Also, since it is O(n), it is not very wise to check huge tree and
	 * consume way to many time. I only check small trees.
	 * 
	 */
	public void check() {
		if (Node.NULL_NODE.getLeft() != null || Node.NULL_NODE.getRight() != null || Node.NULL_NODE.size() != 0
				|| Node.NULL_NODE.getBalance() != null || Node.NULL_NODE.getElement() != 0)
			throw new RuntimeException("NULL_NODE changed!");
		if (length() < 10000) {
			try {
				root.check(height);
			} catch (RuntimeException e) {
				DisplayableBinaryTree t = new DisplayableBinaryTree(this);
				t.show(true);
				while (true)
					;
			}
		}
	}

	public Iterator<Character> iterator() {
		return new InOrderIterator();
	}

	private class InOrderIterator implements Iterator<Character> {
		private Stack<Node> stack;
		private int version;

		public InOrderIterator() {
			stack = new Stack<Node>();
			version = treeVersion;
			advance(root);
		}

		private void advance(Node current) {
			while (current != Node.NULL_NODE) {
				stack.push(current);
				current = current.getLeft();
			}
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public Character next() {
			if (!hasNext())
				throw new NoSuchElementException();
			if (treeVersion != version)
				throw new ConcurrentModificationException();
			Node current = stack.pop();
			advance(current.getRight());
			return current.getElement();
		}

	}

}