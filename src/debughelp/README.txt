Use of this package is completely optional. It will take you some time to integrate it into your code. 
But it could be _very_ worth your time. See the image in this folder for an example of the 
output it produces.

1. Move the two java files in this package into the editortrees package, and uncomment them.
 
2. Add the required fields and methods to EditTree:
	-slowHeight() and slowSize() are directly from the BST assignment, 
	 and do NOT depend on rank or balance code, which you are probably trying to debug using this. 
	 Since slowHeight() and slowSize() are just for debugging, they may be O(n).
	 They may call recursive helpers in the Node class as usual.
	
	-A DisplayableBinaryTree field called display.

	-A show() method that you can call anywhere (unit tests, or main) to show your tree. 
	It will initialize the display field the first time it is called.
	
	public void show() {
		if (this.display == null) {
			this.display = new DisplayableBinaryTree(this, 960, 1080, true);
		} else {
			this.display.show(true);
		}
	}

	- A close method (optional)
	/**
	 * closes the tree window
	 * still keeps all the data
	 * and you can still reshow the tree with the show method
	 */
	public void close() {
		if (this.display != null) {
			this.display.close();
		}
	}

3. Add the required fields and methods to Node:
	- A DisplayableNodeWrapper object as a field, 
	-getDisplayableNodePart() that . Each node has a DisplayableNodeWrapper object as a field, 
	 that you create in the Node constructor. 
	
	hasLeft(), hasRight(), hasParent(), getRank(), getBalance(), 
	getBalance().toString(), and getElement()
 

4. Call the show() method on your tree, like:
	t.show();
	
	You'll probably want to pause the program after showing it, so that the display 
	doesn't close. Maybe add an infinite loop, like: while (true) {}
		
	