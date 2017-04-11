package editortrees;

import java.util.List;
import java.util.ListIterator;

import editortrees.EditTree.H;

/**
 * A node in AVL tree
 * 
 * @author zhang
 *
 */
public class Node {
	public static final Node NULL_NODE = new Node();

	public enum Code {
		SAME, LEFT, RIGHT;

		@Override
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}

		/**
		 * 
		 * @return the inverse direction
		 */
		public Code inverse() {
			switch (this) {
			case SAME:
				return SAME;
			case RIGHT:
				return LEFT;
			case LEFT:
				return RIGHT;
			default:
				throw new RuntimeException();
			}
		}

		/**
		 * the height difference of right and left subtree given its balance
		 * code
		 * 
		 * @return height difference
		 */
		public int hdiff() {
			switch (this) {
			case SAME:
				return 0;
			case RIGHT:
				return 1;
			case LEFT:
				return -1;
			default:
				throw new RuntimeException();
			}
		}
	}

	protected char element;
	public Node left;
	public Node right;
	protected int size;
	protected Code balance;

	protected int getRank() {
		return left.size;
	}

	public Node() {
	}

	/**
	 * 
	 * construct a node given all of its data
	 * 
	 * @param element
	 * @param left
	 * @param right
	 * @param rank
	 * @param balance
	 */
	public Node(char element, Node left, Node right, Code balance) {
		this.element = element;
		this.left = left;
		this.right = right;
		this.size = this.left.size + this.right.size + 1;
		this.balance = balance;
	}

	/**
	 * construct a leave with given element.
	 * 
	 * @param c
	 */
	public Node(char c) {
		this(c, NULL_NODE, NULL_NODE, Code.SAME);
	}

	/**
	 * 
	 * construct a copy of this subtree
	 * 
	 * @return the root node
	 */
	public Node constructFromTree() {
		if (this == NULL_NODE)
			return NULL_NODE;
		return new Node(element, left.constructFromTree(), right.constructFromTree(), balance);
	}

	@Override
	public String toString() {
		if (this == NULL_NODE)
			return "";
		return left.toString() + element + right.toString();
	}

	public String toDebugString() {
		if (this == NULL_NODE)
			return "";
		return "" + element + getRank() + balance + ", " + left.toDebugString() + right.toDebugString();
	}

	public int height() {
		if (this == NULL_NODE)
			return -1;
		if (balance == Code.RIGHT)
			return left.height() + 2;
		return left.height() + 1;
	}

	public int size() {
		return size;
	}

	/**
	 * 
	 * get the element at given position of this subtree
	 * 
	 * @param pos
	 * @return
	 */
	public char get(int pos) {
		if (pos == getRank()) {
			return element;
		} else if (pos <= getRank()) {
			return left.get(pos);
		}
		return right.get(pos - getRank() - 1);
	}

	/**
	 * the string within given range and append to sb.
	 * 
	 * @param start
	 * @param end
	 * @param sb
	 *            string builder to collect all of its data
	 */
	public void get(int start, int end, StringBuilder sb) {
		if (start == end) {
			return;
		}
		if (end <= getRank()) {
			// If current node is after the sought string, only call get() in
			// the left subtree.
			left.get(start, end, sb);
		} else if (start <= getRank() & getRank() < end) {
			// If current node is inside the sought, call get() in both subtrees
			if (start < getRank())
				left.get(start, getRank(), sb);
			sb.append(element);
			if (getRank() + 1 < end)
				right.get(0, end - getRank() - 1, sb);
		}
		if (getRank() < start) {
			// If current node is before the sought string, only call get() in
			// the right subtree.
			right.get(start - getRank() - 1, end - getRank() - 1, sb);
		}
	}

	/**
	 * 
	 * find the position of first occurrence of certain string in this tree
	 *
	 * @param s
	 *            string to search for
	 * @param pos
	 *            the position to start searching
	 * @param found
	 *            information about already matched strings
	 * @return
	 */
	public int find(String s, int pos, List<Integer> found) {
		if (this == NULL_NODE) {
			if (pos != 0)
				throw new RuntimeException();
			return -1;
		}
		if (pos < getRank()) {
			int l = left.find(s, pos, found);
			if (l != -1)
				return l;
		}
		if (pos <= getRank()) {
			// updating the matching indexes in the array list, return the index
			// of
			// last character.
			ListIterator<Integer> itr = found.listIterator();
			while (itr.hasNext()) {
				int index = itr.next();
				if (s.charAt(index) == element) {
					if (index + 1 == s.length())
						return getRank();
					itr.set(index + 1);
				} else {
					itr.remove();
				}
			}
			// see if this can be the start of a match.
			if (element == s.charAt(0)) {
				if (s.length() == 1) {
					return getRank();
				}
				found.add(1);
			}

			int r = right.find(s, 0, found);
			if (r == -1)
				return -1;
			return getRank() + 1 + r;
		}
		return getRank() + 1 + right.find(s, pos - getRank() - 1, found);
	}

	/**
	 * add an element c into the end of this subtree
	 * 
	 * @param c
	 * @param a
	 * @return updated subtree root node
	 */
	public Node addEnd(char c, H a) {
		if (this == NULL_NODE)
			return new Node(c);
		size++;
		right = right.addEnd(c, a);
		if (a.treeBalanced) {
			// it will do nothing if it has been edited before.
		} else if (balance == Code.SAME) {
			// keep searching for unbalance.
			balance = Code.RIGHT;
		} else if (balance == Code.LEFT) {
			// height of subtree isn't changed, quit searching.
			a.treeBalanced = true;
			balance = Code.SAME;
		} else {
			// balance the tree
			a.treeBalanced = true;
			balance = Code.SAME;
			right.balance = Code.SAME;
			return singleLeftRotate(a);
		}
		return this;
	}

	/**
	 * insert a char c into the tree given index.
	 * 
	 * @param c
	 *            element to be inserted
	 * @param pos
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 * @throws IndexOutOfBoundsException
	 */
	public Node add(char c, int pos, H a) throws IndexOutOfBoundsException {
		if (this == NULL_NODE) {
			if (pos > 0)
				throw new RuntimeException();
			return new Node(c);
		}
		Code from;
		size++;
		if (pos <= getRank()) {
			// If pos equals rank, it will just add c to the end of left
			// subtree.
			left = left.add(c, pos, a);
			from = Code.LEFT;
		} else {
			right = right.add(c, pos - getRank() - 1, a);
			from = Code.RIGHT;
		}
		if (a.treeBalanced) {
			// it will do nothing if it has been edited before.
		} else if (balance == Code.SAME) {
			// keep searching unbalanced node
			balance = from;
		} else if (balance != from) {
			// finish searching unbalanced node
			a.treeBalanced = true;
			balance = Code.SAME;
		} else {
			// fixed unbalanced node
			if (from == Code.LEFT) {
				return addRotateFromLeft(a);
			} else if (from == Code.RIGHT) {
				return addRotateFromRight(a);
			}
		}
		return this;
	}

	/**
	 * 
	 * Do the rotation during the add process if the add process causes the
	 * unbalance This is rotate from left to right
	 * 
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 */
	private Node addRotateFromLeft(H a) {
		a.treeBalanced = true;
		if (left.balance == Code.LEFT) {
			// do single right rotate
			balance = Code.SAME;
			left.balance = Code.SAME;
			return singleRightRotate(a);
		}
		return doubleRightRotate(a);
	}

	/**
	 * 
	 * Do the rotation during the add process if the add process causes the
	 * inbalance This is rotate from right to left
	 *
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 */
	private Node addRotateFromRight(H a) {
		a.treeBalanced = true;
		if (right.balance == Code.RIGHT) {
			// do single left rotate
			balance = Code.SAME;
			right.balance = Code.SAME;
			return singleLeftRotate(a);
		}
		return doubleLeftRotate(a);
	}

	private Node doubleRightRotate(H a) {
		// do double right rotate
		left = left.singleLeftRotate(a);
		// three cases for double rotation, set balance codes for each case
		if (left.balance == Code.RIGHT) {
			left.left.balance = Code.LEFT;
			balance = Code.SAME;
			left.balance = Code.SAME;
		} else if (left.balance == Code.LEFT) {
			balance = Code.RIGHT;
			left.balance = Code.SAME;
			left.left.balance = Code.SAME;
		} else {
			balance = Code.SAME;
			left.left.balance = Code.SAME;
		}
		return singleRightRotate(a);
	}

	private Node doubleLeftRotate(H a) {
		// do double left rotate
		right = right.singleRightRotate(a);
		// three cases for double rotation, set balance codes for each case
		if (right.balance == Code.LEFT) {
			right.right.balance = Code.RIGHT;
			balance = Code.SAME;
			right.balance = Code.SAME;
		} else if (right.balance == Code.RIGHT) {
			balance = Code.LEFT;
			right.right.balance = Code.SAME;
			right.balance = Code.SAME;
		} else {
			balance = Code.SAME;
			right.right.balance = Code.SAME;
		}
		return singleLeftRotate(a);
	}

	/**
	 * 
	 * Single Left rotation
	 * 
	 * If modifyBalance is true, the balance code of both node involved in the
	 * rotation will be set to Code.SAME. a.rotate keeps track of the total
	 * rotations happening in the whole process.
	 * 
	 * @param modifyBalance
	 * @param a
	 *            a helper class
	 * @return
	 */
	private Node singleLeftRotate(H a) {
		a.rotate++;
		right.size = this.size;
		this.size = right.left.size + left.size + 1;
		Node rl = right.left;
		right.left = this;
		Node r = right;
		right = rl;
		return r;
	}

	/**
	 * 
	 * Single right rotation
	 * 
	 * If modifyBalance is true, the balance code of both node involved in the
	 * rotation will be set to Code.SAME. a.rotate keeps track of the total
	 * rotations happening in the whole process.
	 * 
	 * @param modifyBalance
	 * @param a
	 *            a helper class
	 * @return
	 */
	private Node singleRightRotate(H a) {
		a.rotate++;
		left.size = this.size;
		this.size = left.right.size + right.size + 1;
		Node lf = left.right;
		left.right = this;
		Node l = left;
		left = lf;
		return l;
	}

	/**
	 * delete the node at given position, stores it in a.delete and return the
	 * updated root node.
	 *
	 * @param pos
	 *            index to delete from this subtree
	 * @param a
	 *            helper class
	 * @return updated root node
	 * @throws IndexOutOfBoundsException
	 */
	public Node delete(int pos, H a) throws IndexOutOfBoundsException {
		if (this == NULL_NODE)
			throw new RuntimeException();
		size--;
		Code from = null;
		if (getRank() == pos) {
			a.deleted = element;
			if (left == NULL_NODE && right == NULL_NODE) {
				return NULL_NODE;
			} else if (left == NULL_NODE) {
				return right;
			} else if (right == NULL_NODE) {
				return left;
			}
			from = Code.RIGHT;
			right = right.deleteSmallest(a);
			element = a.glue;
		} else if (pos < getRank()) {
			from = Code.LEFT;
			left = left.delete(pos, a);
		} else {
			from = Code.RIGHT;
			right = right.delete(pos - getRank() - 1, a);
		}

		if (a.treeBalanced) {
			// Tree is already balanced, so just go back.
			return this;
		}
		if (from == Code.RIGHT) {
			return deleteFromRight(a);
		} else {
			return deleteFromLeft(a);
		}
	}

	/**
	 * delete the left most node in this subtree, stores its value in a.glue and
	 * then return the updated node
	 * 
	 * @param a
	 *            helper class
	 * @return updated node
	 * @throws IndexOutOfBoundsException
	 */
	public Node deleteSmallest(H a) throws IndexOutOfBoundsException {
		if (left == NULL_NODE) {
			a.glue = element;
			return right;
		}
		size--;
		left = left.deleteSmallest(a);
		if (a.treeBalanced) {
			// Tree is already balanced
			return this;
		}
		return deleteFromLeft(a);
	}

	private Node deleteFromLeft(H a) {
		if (balance == Code.SAME) {
			a.treeBalanced = true;
			balance = Code.RIGHT;
		} else if (balance == Code.LEFT) {
			balance = Code.SAME;
		} else if (balance == Code.RIGHT) {
			return deleteRotateFromLeft(a);
		}
		return this;
	}

	/**
	 * delete the left most node in this subtree, stores its value in a.glue and
	 * then return the updated node
	 * 
	 *
	 * @param a
	 *            helper class
	 * @return updated node
	 */
	public Node deleteBiggest(H a) {
		if (right == NULL_NODE) {
			a.glue = element;
			return left;
		}
		size--;
		right = right.deleteBiggest(a);
		if (a.treeBalanced) {
			// Tree is already balanced
			return this;
		}
		return deleteFromRight(a);
	}

	private Node deleteFromRight(H a) {
		if (balance == Code.SAME) {
			a.treeBalanced = true;
			balance = Code.LEFT;
		} else if (balance == Code.RIGHT) {
			balance = Code.SAME;
		} else if (balance == Code.LEFT) {
			return deleteRotateFromRight(a);
		}
		return this;
	}

	/**
	 * Do the rotation during the delete process if the delete process causes
	 * the unbalance because its left subtree's height decrease
	 * 
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 */
	private Node deleteRotateFromLeft(H a) {
		if (right.balance == Code.RIGHT) {
			// do single rotate
			balance = Code.SAME;
			right.balance = Code.SAME;
			return singleLeftRotate(a);
		} else if (right.balance == Code.SAME) {
			// do single rotate, but the height of this subtree is still the
			// same, so a.edited does not need to to be updated.
			a.treeBalanced = true;
			right.balance = Code.LEFT;
			return singleLeftRotate(a);
		}
		return doubleLeftRotate(a);
	}

	/**
	 * Do the rotation during the delete process if the delete process causes
	 * the unbalance because its right subtree's height decrease
	 * 
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 */
	private Node deleteRotateFromRight(H a) {
		if (left.balance == Code.LEFT) {
			// do single rotate
			balance = Code.SAME;
			left.balance = Code.SAME;
			return singleRightRotate(a);
		} else if (left.balance == Code.SAME) {
			// do single rotate, but the height of this subtree is still the
			// same, so a.edited does not need to to be updated.
			a.treeBalanced = true;
			left.balance = Code.RIGHT;
			return singleRightRotate(a);
		}
		return doubleRightRotate(a);
	}

	/**
	 * cancatante this subtree and another smaller subtree with the glue element
	 * in a.glue on the right side, given the difference between those two
	 * subtree.
	 * 
	 * @param a
	 *            helper class
	 * @param inserted
	 *            the node of the subtree to be inserted
	 * @param heightDiff
	 *            know height difference between two subtree
	 * @return the updated root node
	 */
	public Node concatRight(H a, Node inserted, int heightDiff) {
		if (this == NULL_NODE)
			throw new RuntimeException();
		if (heightDiff == 0) {
			// when
			return new Node(a.glue, this, inserted, Code.SAME);
		} else if (heightDiff == 1) {
			return new Node(a.glue, this, inserted, Code.LEFT);
		} else {
			this.size += inserted.size + 1;
			if (balance == Code.LEFT)
				right = right.concatRight(a, inserted, heightDiff - 2);
			else
				right = right.concatRight(a, inserted, heightDiff - 1);
		}
		if (a.treeBalanced) {
		} else if (balance == Code.SAME) {
			balance = Code.RIGHT;
		} else if (balance == Code.LEFT) {
			a.treeBalanced = true;
			balance = Code.SAME;
		} else {
			return addRotateFromRight(a);
		}
		return this;
	}

	/**
	 * cancatante this subtree and another smaller subtree with the glue element
	 * in a.glue on the left side, given the difference between those two
	 * subtree.
	 * 
	 * @param a
	 *            helper class
	 * @param inserted
	 *            the node of the subtree to be inserted
	 * @param heightDiff
	 *            know height difference between two subtree
	 * @param insertSize
	 *            the size of inserted subtree
	 * @return the updated root node
	 */
	public Node concatLeft(H a, Node inserted, int heightDiff) {
		if (this == NULL_NODE)
			throw new RuntimeException();
		if (heightDiff < 0) {
			throw new RuntimeException("" + heightDiff);
		}
		if (heightDiff == 0) {
			return new Node(a.glue, inserted, this, Code.SAME);
		} else if (heightDiff == 1) {
			return new Node(a.glue, inserted, this, Code.RIGHT);
		} else {
			size += inserted.size + 1;
			if (balance == Code.RIGHT)
				left = left.concatLeft(a, inserted, heightDiff - 2);
			else
				left = left.concatLeft(a, inserted, heightDiff - 1);
		}
		if (a.treeBalanced) {
		} else if (balance == Code.SAME) {
			balance = Code.LEFT;
		} else if (balance == Code.RIGHT) {
			a.treeBalanced = true;
			balance = Code.SAME;
		} else {
			return addRotateFromLeft(a);
		}
		return this;
	}

	/**
	 * split this node at given position and stores and merge right subtree in
	 * spl
	 * 
	 * @param pos
	 *            position to split
	 * @param a
	 *            first helper class
	 * @param spl
	 *            right subtree to be concanated
	 * @param b
	 *            second helper class
	 * @return root node of the left splited tree
	 */
	public Node split(int pos, H a, EditTree spl, H b) {
		if (this == NULL_NODE)
			throw new RuntimeException();
		if (pos == getRank() || pos == getRank() + 1) {
			// basis case when we can cut this subtree besides the node
			Node l = left;
			spl.setRoot(right);
			if (pos == getRank()) {
				spl.setRoot(spl.getRoot().add(element, 0, b));
				updateHdiff(b);
			} else {
				l = left.addEnd(element, a);
				updateHdiff(a);
			}
			synHdiff(a, b);
			return l;
		}
		if (pos < getRank()) {
			Node l = left.split(pos, a, spl, b);
			b.hdiff += balance.hdiff();
			b.glue = element;
			if (b.hdiff >= 0) {
				spl.setRoot(right.concatLeft(b, spl.getRoot(), b.hdiff));
				updateHdiff(b);
			} else {
				spl.setRoot(spl.getRoot().concatRight(b, right, -b.hdiff));
				updateHdiff(b);
				b.hdiff--;
			}
			a.hdiff++;
			synHdiff(a, b);
			return l;
		} else {
			Node l = right.split(pos - getRank() - 1, a, spl, b);
			a.hdiff -= balance.hdiff();
			a.glue = element;
			if (a.hdiff >= 0) {
				l = left.concatRight(a, l, a.hdiff);
				updateHdiff(a);
			} else {
				l = l.concatLeft(a, left, -a.hdiff);
				updateHdiff(a);
				a.hdiff--;
			}
			b.hdiff++;
			synHdiff(a, b);
			return l;
		}
	}

	/**
	 * 
	 * update the frame of referece of left and right hdiff to their parant node
	 *
	 * @param a
	 *            helper class for left tree
	 * @param b
	 *            helper class for right tree
	 */
	private void synHdiff(H a, H b) {
		if (balance == Code.LEFT)
			b.hdiff++;
		else if (balance == Code.RIGHT)
			a.hdiff++;
	}

	/**
	 * update the hdiff of this subtree after merging according to a.edited, and
	 * then set a.edited to false
	 *
	 * @param a
	 */
	private static void updateHdiff(H a) {
		if (a.treeBalanced)
			a.hdiff = 1;
		else
			a.hdiff = 0;
		a.treeBalanced = false;
	}

	/**
	 * check whether this node has correct rank, balance code, whether NULL_NODE
	 * stays the same.
	 *
	 */
	public void check() {
		if (this == NULL_NODE) {
			if (left != null || right != null || size != 0 || balance != null || element != 0)
				throw new RuntimeException("NULL_NODE changed!");
			return;
		}
		int s = slowSize();
		if (s != size)
			throw new RuntimeException("size: " + s + " " + size);
		int l = left.slowHeight();
		int r = right.slowHeight();
		switch (balance) {
		case SAME:
			if (l != r)
				throw new RuntimeException("same: " + l + " " + r);
			break;
		case LEFT:
			if (l != r + 1)
				throw new RuntimeException("left: " + l + " " + r);
			break;
		case RIGHT:
			if (l + 1 != r)
				throw new RuntimeException("right: " + l + " " + r);
			break;
		default:
		}
		left.check();
		right.check();
	}

	/**
	 * height method that will never be wrong
	 * 
	 * @return height
	 */
	public int slowHeight() {
		if (this == NULL_NODE)
			return -1;
		return Math.max(left.slowHeight(), right.slowHeight()) + 1;
	}

	/**
	 * size method that will never be wrong
	 *
	 * @return size
	 */
	public int slowSize() {
		if (this == NULL_NODE)
			return 0;
		return left.slowSize() + 1 + right.slowSize();
	}

}