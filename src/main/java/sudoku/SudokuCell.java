package sudoku;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jfoley
 */
public class SudokuCell {
  public static int ANY = 0x1ff;

  /** bit set of integers from [1,9] */
  int data;
  /** cell number [0,81) **/
  int index;
  
  public static SudokuCell fromDefinition(int index, int value) {
    return new SudokuCell(index, (value == 0) ? ANY : mask(value));
  }
  
  private SudokuCell(int index, int possibilities) {
    this.index = index;
    this.data = possibilities;
    assert((possibilities & ~ANY) == 0);
  }

  public int[] domain() {
    int[] vals = new int[9];
    int j = 0;
    for (int i = 0; i < vals.length; i++) {
      if ((data & mask(i)) > 0) {
        vals[j] = i;
        j++;
      }
    }
    return vals;
  }

  public int count() {
    return Integer.bitCount(data);
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
    return (data & mask(i)) > 0;
  }
  
  /** 
   * @return actual int (from 1-9) in cell
   */
  public int get() {
    assert(count()==1);
    return invMask(data);
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
    this.data &= ~mask(i);
  }
  
  @Override
  public String toString() {
    if(count() == 1) {
      return Integer.toString(invMask(this.data));
    } else {
      return "?0b"+Integer.toString(this.data,2);
    }
  }
}
