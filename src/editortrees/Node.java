package editortrees;

import java.util.List;
import java.util.ListIterator;

import editortrees.EditTree.H;
import editortrees.EditTree.SH;
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

		public static Code getCode(int hdiff) {
			switch (hdiff) {
			case 0:
				return Code.SAME;
			case 1:
				return Code.RIGHT;
			case -1:
				return Code.LEFT;
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
		return new Node(getElement(), left.constructFromTree(), right.constructFromTree(), getBalance());
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
		return "" + getElement() + getRank() + getBalance() + ", " + left.toDebugString() + right.toDebugString();
	}

	public Code getBalance() {
		return balance;
	}

	private Node updateBalanceCode(Code leftCode, Code thisCode, Code rightCode) {
		if (leftCode != null)
			left.balance = leftCode;
		this.balance = thisCode;
		if (rightCode != null)
			right.balance = rightCode;
		return this;
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
		size++;
		if (pos <= getRank()) {
			left = left.add(c, pos, a);
			return addFromLeft(a);
		} else {
			right = right.add(c, pos - getRank() - 1, a);
			return addFromRight(a);
		}
	}

	private Node addFromLeft(H a) {
		if (a.treeBalanced)
			return this;
		if (getBalance() == Code.SAME) {
			return updateBalanceCode(null, Code.LEFT, null);
		} else if (getBalance() != Code.LEFT) {
			a.treeBalanced = true;
			return updateBalanceCode(null, Code.SAME, null);
		} else {
			return addRotateFromLeft(a);
		}
	}

	private Node addFromRight(H a) {
		if (a.treeBalanced)
			return this;
		if (getBalance() == Code.SAME) {
			return updateBalanceCode(null, Code.RIGHT, null);
		} else if (getBalance() == Code.LEFT) {
			a.treeBalanced = true;
			return updateBalanceCode(null, Code.SAME, null);
		} else {
			return addRotateFromRight(a);
		}
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
		if (left.getBalance() == Code.LEFT) {
			return singleRightRotate(a).updateBalanceCode(null, Code.SAME, Code.SAME);
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
		if (right.getBalance() == Code.RIGHT) {
			return singleLeftRotate(a).updateBalanceCode(Code.SAME, Code.SAME, null);
		}
		return doubleLeftRotate(a);
	}

	private Node doubleRightRotate(H a) {
		left = left.singleLeftRotate(a);
		return singleRightRotate(a).updateDoubleRotationCode();
	}

	private Node doubleLeftRotate(H a) {
		right = right.singleRightRotate(a);
		return singleLeftRotate(a).updateDoubleRotationCode();
	}

	/**
	 * three cases for double rotation, set balance codes for each case
	 * 
	 * @return
	 */
	private Node updateDoubleRotationCode() {
		if (getBalance() == Code.RIGHT) {
			return updateBalanceCode(Code.LEFT, Code.SAME, Code.SAME);
		} else if (getBalance() == Code.LEFT) {
			return updateBalanceCode(Code.SAME, Code.SAME, Code.RIGHT);
		} else {
			return updateBalanceCode(Code.SAME, Code.SAME, Code.SAME);
		}
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
			return deleteFromLeft(a);
		} else {
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
			return deleteFromRight(a);
		}
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
		if (a.treeBalanced)
			return this;
		if (getBalance() == Code.SAME) {
			a.treeBalanced = true;
			return updateBalanceCode(null, Code.RIGHT, null);
		} else if (getBalance() == Code.LEFT) {
			return updateBalanceCode(null, Code.SAME, null);
		} else if (getBalance() == Code.RIGHT) {
			if (right.getBalance() == Code.RIGHT) {
				return singleLeftRotate(a).updateBalanceCode(Code.SAME, Code.SAME, null);
			} else if (right.getBalance() == Code.SAME) {
				a.treeBalanced = true;
				return singleLeftRotate(a).updateBalanceCode(Code.RIGHT, Code.LEFT, null);
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
		if (a.treeBalanced)
			return this;
		if (getBalance() == Code.SAME) {
			a.treeBalanced = true;
			return updateBalanceCode(null, Code.LEFT, null);
		} else if (getBalance() == Code.RIGHT) {
			return updateBalanceCode(null, Code.SAME, null);
		} else if (getBalance() == Code.LEFT) {
			if (left.getBalance() == Code.LEFT) {
				return singleRightRotate(a).updateBalanceCode(null, Code.SAME, Code.SAME);
			} else if (left.getBalance() == Code.SAME) {
				a.treeBalanced = true;
				return singleRightRotate(a).updateBalanceCode(null, Code.RIGHT, Code.LEFT);
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
			if (getBalance() == Code.LEFT)
				right = right.concatRight(a, inserted, heightDiff - 2);
			else
				right = right.concatRight(a, inserted, heightDiff - 1);
		}
		return addFromRight(a);
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
			if (getBalance() == Code.RIGHT)
				left = left.concatLeft(a, inserted, heightDiff - 2);
			else
				left = left.concatLeft(a, inserted, heightDiff - 1);
		}
		return addFromLeft(a);
	}

	/**
	 * split this node at given position and stores and merge right subtree with
	 * tree other
	 * 
	 * @param pos
	 * @param height the current height of this tree
	 * @param sh the helper dummy helper object to store the returned data
	 */
	public void split(int pos, int height, SH sh) {
		if (this == NULL_NODE)
			throw new RuntimeException();
		int leftHeight = getBalance() == Code.RIGHT ? height - 2 : height - 1;
		int rightHeight = getBalance() == Code.LEFT ? height - 2 : height - 1;
		if (pos == getRank() || pos == getRank() + 1) {
			// basis case when we can cut this subtree besides the node
			sh.leftRoot = left;
			sh.leftHeight = leftHeight;
			sh.rightRoot = right;
			sh.rightHeight = rightHeight;
			if (pos == getRank()) {
				sh.rightRoot = sh.rightRoot.add(getElement(), 0, sh);
				if (!sh.isBalancedAndRest())
					sh.rightHeight++;
			} else {
				sh.leftRoot = sh.leftRoot.add(getElement(), sh.leftRoot.size(), sh);
				if (!sh.isBalancedAndRest())
					sh.leftHeight++;
			}
		} else if (pos < getRank()) {
			left.split(pos, leftHeight, sh);
			sh.deleted = getElement();
			if (rightHeight >= sh.rightHeight) {
				sh.rightRoot = right.concatLeft(sh, sh.rightRoot, rightHeight - sh.rightHeight);
				sh.rightHeight = rightHeight;
			} else {
				sh.rightRoot = sh.rightRoot.concatRight(sh, right, sh.rightHeight - rightHeight);
			}
			if (!sh.isBalancedAndRest())
				sh.rightHeight++;
		} else {
			right.split(pos - getRank() - 1, rightHeight, sh);
			sh.deleted = getElement();
			if (leftHeight >= sh.leftHeight) {
				sh.leftRoot = left.concatRight(sh, sh.leftRoot, leftHeight - sh.leftHeight);
				sh.leftHeight  = leftHeight;
			} else {
				sh.leftRoot = sh.leftRoot.concatLeft(sh, left, sh.leftHeight - leftHeight);
			}
			if (!sh.isBalancedAndRest())
				sh.leftHeight++;
		}
	}

	/**
	 * check whether this node has correct rank, balance code, whether NULL_NODE
	 * stays the same.
	 * 
	 * @param x
	 */
	public void check(X x) {
		if (this == NULL_NODE) {
			if (left != null || right != null || size != 0 || getBalance() != null || getElement() != 0)
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
		if (rightHeight - leftHeight != getBalance().hdiff())
			throw new RuntimeException("same: " + leftHeight + " " + rightHeight);
	}

}