package sudoku;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jfoley
 */
public class SudokuCell {
  int data;
  int index;

  public SudokuCell(int index, int possibilities) {
    this.index = index;
    this.data = possibilities;
    assert((possibilities & ~SudokuState.ANY) == 0);
  }

  public int[] domain() {
    int[] vals = new int[9];
    int j = 0;
    for (int i = 0; i < vals.length; i++) {
      if ((data & SudokuState.mask(i)) > 0) {
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
  boolean inDomain(int i) {
    return (data & SudokuState.mask(i+1)) > 0;
  }
  
  /** 
   * @return actual int (from 0-9) in cell
   */
  int get() {
    assert(count()==1);
    return SudokuState.invMask(data);
  }
  
  List<Integer> getDomain(){
    List<Integer> retval = new LinkedList<Integer>();
    for (int i = 0 ; i < 9 ; i++) {
      if (inDomain(i))
        retval.add(i);
    }
    return retval;
  }
  
}
