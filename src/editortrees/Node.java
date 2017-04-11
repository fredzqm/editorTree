package editortrees;

import java.util.ArrayList;

import editortrees.EditTree.H;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {
	public static final Node NULL_NODE = new Null_Node();

	public enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
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

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to
	// the
	// "publicly visible" effects

	protected char element;
	public Node left; // subtrees
	public Node right;
	protected int rank; // inorder position of this node within its own subtree.
	protected Code balance;

	// Node parent; // You may want this field.
	// Feel free to add other fields that you find useful

	// You will probably want to add several other methods

	// For the following methods, you should fill in the details so that they
	// work correctly

	/**
	 * construct a leave with given element.
	 * 
	 * @param c
	 */
	public Node(char c) {
		element = c;
		left = NULL_NODE;
		right = NULL_NODE;
		rank = 0;
		balance = Code.SAME;
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
	public Node(char element, Node left, Node right, int rank, Code balance) {
		this.element = element;
		this.left = left;
		this.right = right;
		this.rank = rank;
		this.balance = balance;
	}

	/**
	 * 
	 * construct a copy of this subtree
	 * 
	 * @return the root node
	 */
	public Node constructFromTree() {
		return new Node(element, left.constructFromTree(),
				right.constructFromTree(), rank, balance);
	}

	@Override
	public String toString() {
		return left.toString() + element + right.toString();
	}

	public String toDebugString() {
		return "" + element + rank + balance + ", " + left.toDebugString()
				+ right.toDebugString();
	}

	public int height() {
		if (balance == Code.RIGHT)
			return right.height() + 1;
		return left.height() + 1;
	}

	public int size() {
		return rank + 1 + right.size();
	}

	/**
	 * 
	 * get the element at given position of this subtree
	 * 
	 * @param pos
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (pos == rank) {
			return element;
		} else if (pos <= rank) {
			return left.get(pos);
		}
		return right.get(pos - rank - 1);
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
		if (end <= rank) {
			// If current node is after the sought string, only call get() in
			// the left subtree.
			left.get(start, end, sb);
		} else if (start <= rank & rank < end) {
			// If current node is inside the sought, call get() in both subtrees
			if (start < rank)
				left.get(start, rank, sb);
			sb.append(element);
			if (rank + 1 < end)
				right.get(0, end - rank - 1, sb);
		}
		if (rank < start) {
			// If current node is before the sought string, only call get() in
			// the right subtree.
			right.get(start - rank - 1, end - rank - 1, sb);
		}
	}

	/**
	 * find the position of first occurence of certain string in this tree
	 *
	 * @param s
	 *            string to search for
	 * @param found
	 *            information about already matched strings
	 * @return the index of the end of the found string
	 */
	public int find(String s, ArrayList<Integer> found) {
		// return the index of last char, if the string is found in the left
		// branch.
		int l = left.find(s, found);
		if (l != -1)
			return l;

		// updating the matching indexes in the array list, return the index of
		// last character.
		int i = 0;
		while (i < found.size()) {
			int index = found.get(i);
			if (s.charAt(index) == element) {
				found.set(i, index + 1);
				if (index + 1 == s.length())
					return rank;
				i++;
			} else {
				found.remove(i);
			}
		}
		// see if this can be the start of a match.
		if (element == s.charAt(0)) {
			found.add(1);
			if (s.length() == 1) {
				return rank;
			}
		}

		// go the the right branch if nothing is found yet.
		int r = right.find(s, found);
		if (r == -1)
			return -1;
		return rank + 1 + r;
	}

	/**
	 * 
	 * find the position of first occurence of certain string in this tree
	 *
	 * @param s
	 *            string to search for
	 * @param pos
	 *            the position to start searching
	 * @param found
	 *            information about already matched strings
	 * @return
	 */
	public int find(String s, int pos, ArrayList<Integer> found) {
		if (pos < rank) {
			int l = left.find(s, pos, found);
			if (l != -1)
				return l;
		}
		if (pos <= rank) {
			int i = 0;
			while (i < found.size()) {
				int index = found.get(i);
				if (s.charAt(index) == element) {
					found.set(i, index + 1);
					if (index + 1 == s.length())
						return rank;
					i++;
				} else {
					found.remove(i);
				}
			}
			if (element == s.charAt(0)) {
				found.add(1);
				if (s.length() == 1) {
					return rank;
				}
			}
			int r = right.find(s, found);
			if (r == -1)
				return -1;
			return rank + 1 + r;
		}
		return rank + 1 + right.find(s, pos - rank - 1, found);
	}

	/**
	 * add an element c into the end of this subtree
	 * 
	 * @param c
	 * @param a
	 * @return updated subtree root node
	 */
	public Node addEnd(char c, H a) {
		right = right.addEnd(c, a);
		if (a.edited) {
			// it will do nothing if it has been edited before.
		} else if (balance == Code.SAME) {
			// keep searching for unbalance.
			balance = Code.RIGHT;
		} else if (balance == Code.LEFT) {
			// height of subtree isn't changed, quit searching.
			a.edited = true;
			balance = Code.SAME;
		} else {
			// balance the tree
			a.edited = true;
			return singleLeftRotate(true, a);
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
		Code from;
		if (pos <= rank) {
			// If pos equals rank, it will just add c to the end of left
			// subtree.
			rank++;
			left = left.add(c, pos, a);
			from = Code.LEFT;
		} else {
			right = right.add(c, pos - rank - 1, a);
			from = Code.RIGHT;
		}
		if (a.edited) {
			// it will do nothing if it has been edited before.
		} else if (balance == Code.SAME) {
			// keep searching unbalanced node
			balance = from;
		} else if (balance != from) {
			// finish searching unbalanced node
			a.edited = true;
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
		a.edited = true;
		if (left.balance == Code.LEFT) {
			// do single right rotate
			return singleRightRotate(true, a);
		}
		// do double right rotate
		left = left.singleLeftRotate(false, a);
		// three cases for double rotationce, set balance codes for each case
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
		return singleRightRotate(false, a);
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
		a.edited = true;
		if (right.balance == Code.RIGHT) {
			// do single left rotate
			return singleLeftRotate(true, a);
		}
		// do double left rotate
		right = right.singleRightRotate(false, a);
		// three cases for double rotationce, set balance codes for each case
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
		return singleLeftRotate(false, a);
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
		Code from = null;
		if (rank == pos) {
			// found the node we want to delete
			a.deleted = element;
			if (left == NULL_NODE && right == NULL_NODE) {
				// leave node
				return NULL_NODE;
			} else if (left == NULL_NODE) {
				// right children only node
				return right;
			} else if (right == NULL_NODE) {
				// left children only node
				return left;
			}
			// both children node, go to delete the smallest node on the right
			// subtree and grab its value in a.glue by the way.
			from = Code.RIGHT;
			right = right.deleteSmallest(a);
			element = a.glue;
			// a.edited would only be false if this delection change the height
			// of
		} else if (pos < rank) {
			// delete node from left
			rank--;
			from = Code.LEFT;
			left = left.delete(pos, a);
		} else {
			// delete node from right
			from = Code.RIGHT;
			right = right.delete(pos - rank - 1, a);
		}

		if (a.edited) {
			// Tree is already balanced, so just go back.
			// a.eited would be tree only if the heght of subtree decreases
			// after deletion
		} else if (balance == Code.SAME) {
			// only tilt this subtree, the whole tree is still balanced.
			a.edited = true;
			balance = from.inverse();
		} else if (balance == from) {
			// make this subtree perfectly balanced.
			balance = Code.SAME;
		} else if (balance != from) {
			// this node is unbalanced, so we need to fix it!
			if (from == Code.LEFT) {
				return deleteRotateFromLeft(a);
			} else if (from == Code.RIGHT) {
				return deleteRotateFromRight(a);
			}
			throw new RuntimeException();
		}
		return this;
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
			// find the smallest element
			a.glue = element;
			return right;
		}
		rank--;
		left = left.deleteSmallest(a);
		// balance the tree
		if (a.edited) {
			// Tree is already balanced
		} else if (balance == Code.SAME) {
			a.edited = true;
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
		right = right.deleteBiggest(a);
		// balance the tree
		if (a.edited) {
			// Tree is already balanced
		} else if (balance == Code.SAME) {
			a.edited = true;
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
			return singleLeftRotate(true, a);
		} else if (right.balance == Code.SAME) {
			// do single rotate, but the height of this subtree is still the
			// same, so a.edited does not need to to be updated.
			a.edited = true;
			right.balance = Code.LEFT;
			return singleLeftRotate(false, a);
		} else {
			// do double rotate, set balance code according to cases
			right = right.singleRightRotate(false, a);
			// three cases for double rotationce, set balance codes for each
			// case
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
			return singleLeftRotate(false, a);
		}
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
			return singleRightRotate(true, a);
		} else if (left.balance == Code.SAME) {
			// do single rotate, but the height of this subtree is still the
			// same, so a.edited does not need to to be updated.
			a.edited = true;
			left.balance = Code.RIGHT;
			return singleRightRotate(false, a);
		}
		// do double rotate, set balance code according to cases
		left = left.singleLeftRotate(false, a);
		// three cases for double rotationce, set balance codes for each case
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
		return singleRightRotate(false, a);
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
		if (heightDiff == 0) {
			// when
			return new Node(a.glue, this, inserted, size(), Code.SAME);
		} else if (heightDiff == 1) {
			return new Node(a.glue, this, inserted, size(), Code.LEFT);
		} else {
			if (balance == Code.LEFT)
				right = right.concatRight(a, inserted, heightDiff - 2);
			else
				right = right.concatRight(a, inserted, heightDiff - 1);
		}
		if (a.edited) {
		} else if (balance == Code.SAME) {
			balance = Code.RIGHT;
		} else if (balance == Code.LEFT) {
			a.edited = true;
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
	public Node concatLeft(H a, Node inserted, int heightDiff, int insertSize) {
		if (heightDiff < 0) {
			throw new RuntimeException("" + heightDiff);
		}
		if (heightDiff == 0) {
			return new Node(a.glue, inserted, this, insertSize, Code.SAME);
		} else if (heightDiff == 1) {
			return new Node(a.glue, inserted, this, insertSize, Code.RIGHT);
		} else {
			rank += insertSize + 1;
			if (balance == Code.RIGHT)
				left = left.concatLeft(a, inserted, heightDiff - 2, insertSize);
			else
				left = left.concatLeft(a, inserted, heightDiff - 1, insertSize);
		}
		if (a.edited) {
		} else if (balance == Code.SAME) {
			balance = Code.LEFT;
		} else if (balance == Code.RIGHT) {
			a.edited = true;
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
		if (pos == rank || pos == rank + 1) {
			// basis case when we can cut this subtree besides the node
			Node l = left;
			spl.setRoot(right);
			if (pos == rank) {
				spl.setRoot(spl.getRoot().add(element, 0, b));
				updateHdiff(b);
			} else {
				l = left.addEnd(element, a);
				updateHdiff(a);
			}
			synHdiff(a, b);
			return l;
		}
		if (pos < rank) {
			Node l = left.split(pos, a, spl, b);
			b.hdiff += balance.hdiff();
			b.glue = element;
			if (b.hdiff >= 0) {
				spl.setRoot(right.concatLeft(b, spl.getRoot(), b.hdiff, spl
						.getRoot().size()));
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
			Node l = right.split(pos - rank - 1, a, spl, b);
			a.hdiff -= balance.hdiff();
			a.glue = element;
			if (a.hdiff >= 0) {
				l = left.concatRight(a, l, a.hdiff);
				updateHdiff(a);
			} else {
				l = l.concatLeft(a, left, -a.hdiff, left.size());
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
		if (a.edited)
			a.hdiff = 1;
		else
			a.hdiff = 0;
		a.edited = false;
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
	private Node singleLeftRotate(boolean modifyBalance, H a) {
		a.rotate++;
		if (modifyBalance) {
			balance = Code.SAME;
			right.balance = Code.SAME;
		}
		right.rank += rank + 1;
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
	private Node singleRightRotate(boolean modifyBalance, H a) {
		a.rotate++;
		if (modifyBalance) {
			balance = Code.SAME;
			left.balance = Code.SAME;
		}
		rank -= left.rank + 1;
		Node lf = left.right;
		left.right = this;
		Node l = left;
		left = lf;
		return l;
	}

	/**
	 * 
	 * Solve the basic condition of the tree.
	 *
	 * @author zhangq2. Created Apr 21, 2015.
	 */
	static class Null_Node extends Node {
		public Null_Node() {
			super('\n');
			left = this;
			right = this;
		}

		@Override
		public String toString() {
			return "";
		}

		@Override
		public Node constructFromTree() {
			return NULL_NODE;
		}

		@Override
		public String toDebugString() {
			return "";
		}

		@Override
		public int height() {
			return -1;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public char get(int pos) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void get(int start, int end, StringBuilder sb) {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public int find(String s, ArrayList<Integer> found) {
			return -1;
		}

		@Override
		public Node add(char c, int pos, EditTree.H edit) {
			if (pos > 0)
				throw new IndexOutOfBoundsException();
			return new Node(c);
		}

		@Override
		public Node addEnd(char c, EditTree.H edit) {
			return new Node(c);
		}

		@Override
		public Node delete(int pos, H a) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public Node deleteSmallest(H a) throws IndexOutOfBoundsException {
			throw new RuntimeException();
		}

		@Override
		public Node deleteBiggest(H a) throws IndexOutOfBoundsException {
			throw new RuntimeException();
		}

		@Override
		public Node concatRight(H a, Node inserted, int heightDiff) {
			throw new RuntimeException();
		}

		@Override
		public Node concatLeft(H a, Node inserted, int heightDiff,
				int insertSize) {
			throw new RuntimeException();
		}

		@Override
		public Node split(int pos, H a, EditTree spl, H b) {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void check() {
			if (left != NULL_NODE || right != NULL_NODE)
				throw new RuntimeException("NULL_NODE changed!");
		}

		@Override
		public int slowHeight() {
			return -1;
		}

		@Override
		public int slowSize() {
			return 0;
		}
	}

	/**
	 * check whether this node has correct rank, balance code, whether NULL_NODE
	 * stays the same.
	 *
	 */
	public void check() {
		int s = left.slowSize();
		if (rank != s)
			throw new RuntimeException("rank: " + s + " " + rank);
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
		return Math.max(left.slowHeight(), right.slowHeight()) + 1;
	}

	/**
	 * size method that will never be wrong
	 *
	 * @return size
	 */
	public int slowSize() {
		return left.slowSize() + 1 + right.slowSize();
	}

}