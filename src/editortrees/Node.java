package editortrees;

import java.util.List;
import java.util.ListIterator;

import editortrees.EditTree.H;
import editortrees.EditTree.X;

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

	private char element;
	private Node left;
	private Node right;
	private int size;
	private Code balance;

	public int getRank() {
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
		return new Node(getElement(), left.constructFromTree(), right.constructFromTree(), balance);
	}

	@Override
	public String toString() {
		if (this == NULL_NODE)
			return "";
		return left.toString() + getElement() + right.toString();
	}

	public String toDebugString() {
		if (this == NULL_NODE)
			return "";
		return "" + getElement() + getRank() + balance + ", " + left.toDebugString() + right.toDebugString();
	}

	public Code getBalance() {
		return balance;
	}

	public char getElement() {
		return element;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
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
			return getElement();
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
			left.get(start, end, sb);
		} else if (start <= getRank() & getRank() < end) {
			if (start < getRank())
				left.get(start, getRank(), sb);
			sb.append(getElement());
			if (getRank() + 1 < end)
				right.get(0, end - getRank() - 1, sb);
		}
		if (getRank() < start) {
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
			// of the last character.
			ListIterator<Integer> itr = found.listIterator();
			while (itr.hasNext()) {
				int index = itr.next();
				if (s.charAt(index) == getElement()) {
					if (index + 1 == s.length())
						return getRank();
					itr.set(index + 1);
				} else {
					itr.remove();
				}
			}
			// see if this can be the start of a match.
			if (getElement() == s.charAt(0)) {
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
			left = left.add(c, pos, a);
			from = Code.LEFT;
		} else {
			right = right.add(c, pos - getRank() - 1, a);
			from = Code.RIGHT;
		}
		if (a.treeBalanced)
			return this;
		if (balance == Code.SAME) {
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
			Node t = singleRightRotate(a);
			t.balance = Code.SAME;
			t.right.balance = Code.SAME;
			return t;
		}
		return doubleRightRotate(a);
	}

	/**
	 * 
	 * Do the rotation during the add process if the add process causes the
	 * Imbalance This is rotate from right to left
	 *
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 */
	private Node addRotateFromRight(H a) {
		a.treeBalanced = true;
		if (right.balance == Code.RIGHT) {
			// do single left rotate
			Node t = singleLeftRotate(a);
			t.balance = Code.SAME;
			t.left.balance = Code.SAME;
			return t;
		}
		return doubleLeftRotate(a);
	}

	private Node doubleRightRotate(H a) {
		// do double right rotate
		left = left.singleLeftRotate(a);
		// three cases for double rotation, set balance codes for each case
		return singleRightRotate(a).updateDoubleRotationCode();
	}

	private Node doubleLeftRotate(H a) {
		// do double left rotate
		right = right.singleRightRotate(a);
		// three cases for double rotation, set balance codes for each case
		return singleLeftRotate(a).updateDoubleRotationCode();
	}

	private Node updateDoubleRotationCode() {
		if (balance == Code.RIGHT) {
			left.balance = Code.LEFT;
			right.balance = Code.SAME;
		} else if (balance == Code.LEFT) {
			left.balance = Code.SAME;
			right.balance = Code.RIGHT;
		} else {
			left.balance = Code.SAME;
			right.balance = Code.SAME;
		}
		balance = Code.SAME;
		return this;
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
		if (pos < getRank()) {
			left = left.delete(pos, a);
			if (a.treeBalanced)
				return this;
			return deleteFromLeft(a);
		}
		if (getRank() < pos) {
			right = right.delete(pos - getRank() - 1, a);
		} else {
			a.deleted = getElement();
			if (left == NULL_NODE && right == NULL_NODE) {
				return NULL_NODE;
			} else if (left == NULL_NODE) {
				return right;
			} else if (right == NULL_NODE) {
				return left;
			}
			right = right.delete(0, a);
			char swap = getElement();
			this.element = a.deleted;
			a.deleted = swap;
		}
		if (a.treeBalanced)
			return this;
		return deleteFromRight(a);
	}

	/**
	 * Knowing the height of right subtree decreased due to a deletion, balance
	 * the tree
	 * 
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 */
	private Node deleteFromLeft(H a) {
		if (balance == Code.SAME) {
			a.treeBalanced = true;
			balance = Code.RIGHT;
		} else if (balance == Code.LEFT) {
			balance = Code.SAME;
		} else if (balance == Code.RIGHT) {
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
		return this;
	}

	/**
	 * Knowing the height of right subtree decreased due to a deletion, balance
	 * the tree
	 * 
	 * @param a
	 *            helper class
	 * @return updated subtree root node
	 */
	private Node deleteFromRight(H a) {
		if (balance == Code.SAME) {
			a.treeBalanced = true;
			balance = Code.LEFT;
		} else if (balance == Code.RIGHT) {
			balance = Code.SAME;
		} else if (balance == Code.LEFT) {
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
		return this;
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
			return new Node(a.deleted, this, inserted, Code.SAME);
		} else if (heightDiff == 1) {
			return new Node(a.deleted, this, inserted, Code.LEFT);
		} else {
			this.size += inserted.size + 1;
			if (balance == Code.LEFT)
				right = right.concatRight(a, inserted, heightDiff - 2);
			else
				right = right.concatRight(a, inserted, heightDiff - 1);
		}
		if (a.treeBalanced)
			return this;
		if (balance == Code.SAME) {
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
	 * Concatenate this subtree and another smaller subtree with the glue
	 * element in a.glue on the left side, given the difference between those
	 * two subtree.
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
			return new Node(a.deleted, inserted, this, Code.SAME);
		} else if (heightDiff == 1) {
			return new Node(a.deleted, inserted, this, Code.RIGHT);
		} else {
			size += inserted.size + 1;
			if (balance == Code.RIGHT)
				left = left.concatLeft(a, inserted, heightDiff - 2);
			else
				left = left.concatLeft(a, inserted, heightDiff - 1);
		}
		if (a.treeBalanced)
			return this;
		if (balance == Code.SAME) {
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
	 * split this node at given position and stores and merge right subtree with
	 * tree other
	 * 
	 * @param pos
	 *            position to split
	 * @param a
	 *            first helper class
	 * @param other
	 *            right subtree to be concatenate
	 * @param b
	 *            second helper class
	 * @return root node of the left spliced tree
	 */
	public Node split(int pos, H a, EditTree other, H b) {
		if (this == NULL_NODE)
			throw new RuntimeException();
		if (pos == getRank() || pos == getRank() + 1) {
			// basis case when we can cut this subtree besides the node
			Node l = left;
			other.setRoot(right);
			if (pos == getRank()) {
				other.setRoot(other.getRoot().add(getElement(), 0, b));
				updateHdiff(b);
			} else {
				l = left.add(getElement(), left.size(), a);
				updateHdiff(a);
			}
			synHdiff(a, b);
			return l;
		}
		if (pos < getRank()) {
			Node l = left.split(pos, a, other, b);
			b.hdiff += balance.hdiff();
			b.deleted = getElement();
			if (b.hdiff >= 0) {
				other.setRoot(right.concatLeft(b, other.getRoot(), b.hdiff));
				updateHdiff(b);
			} else {
				other.setRoot(other.getRoot().concatRight(b, right, -b.hdiff));
				updateHdiff(b);
				b.hdiff--;
			}
			a.hdiff++;
			synHdiff(a, b);
			return l;
		} else {
			Node l = right.split(pos - getRank() - 1, a, other, b);
			a.hdiff -= balance.hdiff();
			a.deleted = getElement();
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
	 * @param x
	 */
	public void check(X x) {
		if (this == NULL_NODE) {
			if (left != null || right != null || size != 0 || balance != null || getElement() != 0)
				throw new RuntimeException("NULL_NODE changed!");
			x.height = -1;
			x.size = 0;
			return;
		}
		left.check(x);
		int leftHeight = x.height;
		int leftSize = x.size;
		right.check(x);
		int rightHeight = x.height;
		int rightSize = x.size;

		// check size
		x.size = leftSize + rightSize + 1;
		if (x.size != size)
			throw new RuntimeException("size: " + x.size + " " + size);

		// check balanced code
		x.height = Math.max(leftHeight, rightHeight) + 1;
		if (rightHeight - leftHeight != balance.hdiff())
			throw new RuntimeException("same: " + leftHeight + " " + rightHeight);
	}

}