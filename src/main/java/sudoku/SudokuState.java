package sudoku;

import java.io.PrintStream;
import java.util.Arrays;

public class SudokuState {
  private int[] cells;
  private SudokuState(int[] input) {
    this.cells = input;
  }
  
  public static SudokuState fromDefinition(int[] input) {
    int[] masks = new int[81];
    for(int i=0; i<81; i++) {
      int val = input[i];
      masks[i] = (val == 0) ? ANY : mask(val);
    }
    return new SudokuState(masks);
  }

  public boolean isComplete() {
    for (int i = 0; i < 81; i++) {
      if (count(i) != 1) {
        return false;
      }
    }
    return true;
  }

  public SudokuCell selectOpenVariable() {
    assert(!isComplete());
    int min = -1;
    int minLeft = 10;
    for(int i=0; i<81; i++) {
      int left = count(i);
      if(left == 1) {
        continue;
      }
      if(left < minLeft) {
        minLeft = left;
        min = i;
      }
    }
        
    assert(min != -1);
    assert(minLeft <= 9 && minLeft >= 2);
    
    return new SudokuCell(min, cells[min]);
  }

  /**
   * Create a new SudokuState by playing i at index.
   */
  public SudokuState set(int index, int i) {
    int[] newState = Arrays.copyOf(cells, cells.length);
    newState[index] = mask(i+1);
    return new SudokuState(newState);
  }
  public static int ANY = 0x1ff;
  
  public int count(int index) {
    return Integer.bitCount(cells[index] & ANY);
  }

  public static int mask(int n) {
    assert (n >= 1 && n <= 9);
    return 1 << (n - 1);
  }
  
  public static int invMask(int mask) {
    return Integer.numberOfTrailingZeros(mask)+1;
  }

  public SudokuCell get(int x, int y) {
    assert(x >= 0 && x < 9 && y >= 0 && y < 9);
    int index = x + y*9;
    return new SudokuCell(index, cells[index]);
  }
  
  
  private boolean distinct(SudokuCell[] row) {
    boolean found[] = new boolean[9];
    for(int i=0; i<9; i++) {
      if(row[i].count() == 1) {
        int index = row[i].get()-1;
        if(found[index]) {
          return false; // double thing in this set
        }
        found[index] = true;
        row[i] = null; // eliminate this from future checks
      }
    }
    
    for(SudokuCell c : row) {
      if(c == null) continue;
      //TODO check uniqueness properly of plausible results.
    }
    
    return true;
  }
  
  boolean isConsistent() {
    SudokuCell block[] = new SudokuCell[9];
    
    // check columns
    for(int x=0; x<9; x++) {
      for(int y=0; y<9; y++) {
        block[y] = get(x,y);
      }
      if(!distinct(block)) return false;
    }
    
    // check rows
    for(int y=0; y<9; y++) {
      for(int x=0; x<9; x++) {
        block[x] = get(x,y);
      }
      if(!distinct(block)) return false;
    }
    
    // check super blocks
    for(int sx=0; sx<3; sx++) {
      for(int sy=0; sy<3; sy++) {
        // fill array and check
        for(int x=0; x<3; x++) {
          for(int y=0; y<3; y++) {
            block[x+y*3] = get(sx*3+x, sy*3+y);
          }
        }
        if(!distinct(block)) return false;
      }
    }
    
    return true;
  }
  
  public static void main(String[] args) {
    
    SudokuState st = fromDefinition(new int[] {
      1,0,0,0,0,0,0,0,0,
      0,1,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0,
      0,0,0,0,0,0,0,0,0
    });
    
    assert(!st.isConsistent());
    
    
  }

  void print(PrintStream out) {
    for (int i = 0; i < cells.length; i++) {
      out.print((i % 9 == 0) ? '\n' : ' ');
      if(count(i) == 1) {
        out.print(invMask(cells[i]));
      } else {
        out.print("?"+cells[i]);
      }
    }
    out.println();
  }
}
