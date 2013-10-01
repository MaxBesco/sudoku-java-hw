package sudoku;

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

  boolean has(int i) {
    return (data & SudokuState.mask(i+1)) > 0;
  }

  int get() {
    return SudokuState.invMask(data);
  }
  
}
