package sudoku;

import java.util.LinkedList;
import java.util.List;

public class SudokuCell {
  public static int ANY = 0x1ff;

  /** bit set of integers from [1,9] */
  int domain;
  /** cell number [0,81) **/
  int index;
  
  public static SudokuCell fromDefinition(int index, int value) {
    return new SudokuCell(index, (value == 0) ? ANY : mask(value));
  }
  
  private SudokuCell(int index, int possibilities) {
    this.index = index;
    this.domain = possibilities;
    assert((possibilities & ~ANY) == 0);
  }
  
  @Override
  public SudokuCell clone() {
    return new SudokuCell(this.index, this.domain);
  }

  public int count() {
    return Integer.bitCount(domain);
  }

  public boolean done() {
    return count() == 1;
  }

  public int x() {
    return index % 9;
  }

  public int y() {
    return index / 9;
  }
/**
 * @param i
 * @return true if we have not yet eliminated i as a possible value for the cell.
 **/
  public boolean inDomain(int i) {
    return (domain & mask(i)) > 0;
  }
  
  /** 
   * @return actual int (from 1-9) in cell
   */
  public int get() {
    assert(count()==1);
    return invMask(domain);
  }
  
  public List<Integer> getDomain(){
    List<Integer> retval = new LinkedList<Integer>();
    for (int i = 1 ; i <= 9 ; i++) {
      if (inDomain(i)) retval.add(i);
    }
    return retval;
  }
  
  public static int mask(int n) {
    assert (n >= 1 && n <= 9);
    return 1 << (n - 1);
  }
  
  public static int invMask(int mask) {
    return Integer.numberOfTrailingZeros(mask)+1;
  }

  public SudokuCell set(int i) {
    return new SudokuCell(this.index, mask(i));
  }
  
  public void remove(int i) {
    this.domain &= ~mask(i);
  }
  
  @Override
  public boolean equals(Object other) {
    assert(other instanceof SudokuCell);
    SudokuCell c = (SudokuCell) other;
    return c.index == index;
  }

  @Override
  public int hashCode() {
    return (new Integer(this.index)).hashCode();
  }
  
  @Override
  public String toString() {
    if(count() == 1) {
      return Integer.toString(invMask(this.domain));
    } else {
      return "?";
    }
  }
}
