package sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jfoley
 */
public class NakedPairs implements Inference {

  @Override
  public boolean inferenceMethod(SudokuState state) throws InconsistencyException {
    boolean changed = false;
    SudokuCell block[] = new SudokuCell[9];

    // check columns
    for(int x=0; x<9; x++) {
      for(int y=0; y<9; y++) {
        block[y] = state.get(x,y);
      }
      if(findPairs(state, block)) return true;
    }
    
    // check rows
    for(int y=0; y<9; y++) {
      for(int x=0; x<9; x++) {
        block[x] = state.get(x,y);
      }
      if(findPairs(state, block)) return true;
    }
    
    // check super blocks
    for(int sx=0; sx<3; sx++) {
      for(int sy=0; sy<3; sy++) {
        // fill array and check
        for(int x=0; x<3; x++) {
          for(int y=0; y<3; y++) {
            block[x+y*3] = state.get(sx*3+x, sy*3+y);
          }
        }
        if(findPairs(state, block)) return true;
      }
    }
    
    return changed;
  }

  private boolean findPairs(SudokuState state, SudokuCell[] block) throws InconsistencyException {
    ArrayList<SudokuCell> pairs = new ArrayList<SudokuCell>();
    for(SudokuCell x : block) {
      if(x.done()) continue;
      if(x.count() == 2)
        pairs.add(x);
    }
    if(pairs.size() < 2) return false;
    
    for(int i=0; i<pairs.size(); i++) {
      SudokuCell a = pairs.get(i);
      for(int j=i+1; j<pairs.size(); j++) {
        SudokuCell b = pairs.get(j);
        
        if(a.domain == b.domain) {
          return eliminateUsingPair(state, block, a,b);
        }
      }
    }
    
    return false;
  }

  private boolean eliminateUsingPair(SudokuState state, SudokuCell block[], SudokuCell a, SudokuCell b) throws InconsistencyException {
    boolean changed = false;
    Set<Integer> dom = new HashSet<Integer>();
    for(int i=1; i<=9; i++) {
      if(a.inDomain(i) && b.inDomain(i)) {
        dom.add(i);
      }
    }
    
    for(int x : dom) {
      for(SudokuCell neighbor : block) {
        if(neighbor.index == a.index || neighbor.index == b.index)
          continue;
        if(neighbor.inDomain(x)) {
          if(neighbor.count() == 1)
            throw new InconsistencyException();
          neighbor.remove(x);
          changed = true;
        }
      }
    }
    return changed;
  }
  
}
