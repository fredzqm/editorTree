package editortrees;

import java.util.ArrayList;

import editortrees.Node.Code;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {
	private Node root;
	private int totalRotationCount;

	/**
	 * Construct an empty tree
	 */
	public EditTree() {
		root = Node.NULL_NODE;
		totalRotationCount = 0;
	}

	/**
	 * Construct a single-node tree whose element is c
	 * 
	 * @param c
	 */
	public EditTree(char c) {
		this();
		root = new Node(c);
	}

	/**
	 * Create an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the length of the tree (repeatedly calling insert() would be
	 * O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		this();
		root = constructFromString(s, 0, s.length());
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
		Code b;
		int hl = (int) (Math.log(mid - start) / Math.log(2));
		int hr = (int) (Math.log(end - mid - 1) / Math.log(2));
		if (hl == hr) {
			b = Code.SAME;
		} else if (hl < hr) {
			b = Code.RIGHT;
		} else {
			b = Code.LEFT;
		}
		return new Node(string.charAt(mid), constructFromString(string, start, mid),
				constructFromString(string, mid + 1, end), b);
	}

	/**
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		root = e.root.constructFromTree();
		totalRotationCount = 0;
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

	protected void setRoot(Node root) {
		this.root = root;
	}

	/**
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		return root.toString();
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
		return root.height();
	}

	/**
	 * 
	 * @return the number of nodes in this tree
	 */
	public int size() {
		return root.size();
	}

	/**
	 * 
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
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
		StringBuilder sb = new StringBuilder();
		root.get(pos, pos + length, sb);
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
		if (s.length() == 0)
			return 0;
		// create an arraylist of integers that will keep track of all matches,
		// so we won't jump over a match if string has repeated pattern.
		int l = root.find(s, new ArrayList<Integer>());
		if (l == -1)
			return -1;
		check();
		return l - s.length() + 1;
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
		int l;
		if (pos <= 0)
			l = root.find(s, new ArrayList<Integer>());
		else
			l = root.find(s, pos, new ArrayList<Integer>());
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
		H a = new H();
		root = root.addEnd(c, a);
		totalRotationCount += a.rotate;
		check();
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
		if (pos < 0 || pos > root.size())
			throw new IndexOutOfBoundsException();
		H a = new H();
		root = root.add(c, pos, a);
		totalRotationCount += a.rotate;
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
		if (pos < 0 || pos >= root.size())
			throw new IndexOutOfBoundsException();
		H a = new H();
		root = root.delete(pos, a);
		totalRotationCount += a.rotate;
		check();
		return a.deleted;
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.
		// replace by a real calculation.
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
		if (start < 0 || start + length >= this.size())
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
		int height = root.height();
		int heightOther = other.root.height();
		H a = new H();
		if (height >= heightOther) {
			// this tree is higher than the other tree
			if (heightOther == -1) {
				// do nothing if other tree is empty.
				return;
			} else if (heightOther == 0) {
				// just add a element if other root has only one element so
				// after
				// remove the glue, other tree is empty
				root = root.addEnd(other.root.element, a);
			} else {
				other.root = other.root.deleteSmallest(a);
				if (a.edited)
					a.edited = false;
				else
					heightOther--;
				root = root.concatRight(a, other.root, height - heightOther);
			}
		} else {
			// this tree is lower than the other tree
			if (height == -1) {
				// do nothing if other tree is empty.
				root = other.root;
				other.root = Node.NULL_NODE;
				return;
			} else if (height == 0) {
				// just add a element if this tree has only one element so after
				// remove the glue, this tree is empty
				root = other.root.add(root.element, 0, a);
			} else {
				root = root.deleteBiggest(a);
				if (a.edited)
					a.edited = false;
				else
					height--;
				root = other.root.concatLeft(a, root, heightOther - height);
			}
		}
		totalRotationCount += a.rotate + other.totalRotationCount;
		other.root = Node.NULL_NODE;
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
		if (pos < 0 || pos >= root.size())
			throw new IndexOutOfBoundsException();
		EditTree spl = new EditTree();
		H a = new H();
		H b = new H();
		root = root.split(pos, a, spl, b);
		totalRotationCount += a.rotate + b.rotate;
		check();
		spl.check();
		return spl;
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
		 * keep track of whether the tree need more modification to balance
		 */
		public boolean edited;
		/**
		 * store the element deleted from the tree
		 */
		public char deleted;
		/**
		 * store element that was removed some leave and will be used to help
		 * glue two subtree together.
		 */
		public char glue;
		/**
		 * keep track of the number of rotation happened during the modification
		 * operation
		 */
		public int rotate;
		/**
		 * store the height differernce compared to a certain subtree in split
		 * method
		 */
		public int hdiff;

		public H() {
			edited = false;
			deleted = '\n';
			glue = '\n';
			rotate = 0;
			hdiff = 1;
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
		if (root.getRank() < 1000)
			root.check();
	}

	/**
	 * a height method that is never wrong
	 * 
	 * @return height of tree
	 */
	public int slowHeight() {
		return root.slowHeight();
	}

	/**
	 * a size method that is never wrong
	 * 
	 * @return size of tree
	 */
	public int slowSize() {
		return root.slowSize();
	}

}