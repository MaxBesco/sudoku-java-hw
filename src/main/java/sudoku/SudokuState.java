package sudoku;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;

public class SudokuState {
  private SudokuCell[] cells;
  public static final int TOTAL_CELLS = 81;
  
  private SudokuState(SudokuCell[] input) {
    this.cells = input;
  }
  
  public static SudokuState fromDefinition(int[] input) {
    SudokuCell[] cells = new SudokuCell[TOTAL_CELLS];
    for(int i=0; i<TOTAL_CELLS; i++) {
      cells[i] = SudokuCell.fromDefinition(i, input[i]);
    }
    return new SudokuState(cells);
  }

  public boolean isComplete() {
    for (int i = 0; i < 81; i++) {
      if (count(i) != 1) {
        return false;
      }
    }
    return true;
  }
  
  public void eliminateIllegal(){
    
  }
  
  public SudokuCell ac3(){
    return selectOpenMRV();
  }
  
  public SudokuCell selectOpenMRV(){
    // num remaining values = {2,...,9}
    LinkedList[] remainingVals = new LinkedList[8];
    for (int i = 0 ; i < remainingVals.length ; i++)
      remainingVals[i] = new LinkedList<Integer>();
    for (int i=0; i<TOTAL_CELLS; i++) {
      int left = count(i);
      if (left==1)
        continue;
      else remainingVals[left-2].add(i);
    }
    for (int i = 0 ; i < remainingVals.length ; i ++)
      if (remainingVals[i].size() > 0) {
        int cellId = (Integer) remainingVals[i].get(0);
        return cells[cellId];
      }
    throw new RuntimeException("did not find any open values");
  }

  public SudokuCell selectOpenVariable() {
    assert(!isComplete());
    int min = -1;
    int minLeft = 10;
    // loop over total cells, find out who is open
    for(int i=0; i<TOTAL_CELLS; i++) {
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
    
    return cells[min];
  }

  /**
   * Create a new SudokuState by playing i at index.
   */
  public SudokuState set(int index, int i) {
    SudokuCell[] newState = Arrays.copyOf(cells, cells.length);
    newState[index] = newState[index].set(i);
    return new SudokuState(newState);
  }
  
  public int count(int index) {
    return cells[index].count();
  }


  public SudokuCell get(int x, int y) {
    assert(x >= 0 && x < 9 && y >= 0 && y < 9);
    return cells[x + y*9];
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
      out.print(cells[i]);
    }
    out.println();
  }
}
