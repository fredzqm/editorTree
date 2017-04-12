package editortrees;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link editortrees.EditTree#concatenate(EditTree)}
 * 
 */
public class EditTreeExtraTest {

	@Test
	public void testInsert() {
		EditTree t1 = new EditTree("abcdef");
		
		t1.insert(3, "123");
		
		assertEquals("abc123def", t1.toString());
		t1.check();
	}
	
	@Test
	public void testInsertEnd() {
		EditTree t1 = new EditTree("abcdef");
		
		t1.insert(6, "123");
		
		assertEquals("abcdef123", t1.toString());
		t1.check();
	}
	
	@Test
	public void testSubsequence() {
		EditTree t1 = new EditTree("abcdef");
		
		EditTree t2 = t1.subSequence(3, 5);
		
		assertEquals("abcdef", t1.toString());
		t1.check();
		assertEquals("de", t2.toString());
		t2.check();
	}
}