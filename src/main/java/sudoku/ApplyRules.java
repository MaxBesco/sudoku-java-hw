package sudoku;

/**
 *
 * @author jfoley
 */
public class ApplyRules implements Inference {

  @Override
  public boolean inferenceMethod(SudokuState state) throws InconsistencyException {
    boolean changed = false;
    SudokuCell block[] = new SudokuCell[9];

    // check columns
    for(int x=0; x<9; x++) {
      for(int y=0; y<9; y++) {
        block[y] = state.get(x,y);
      }
      changed |= makeDistinct(block);
    }
    
    // check rows
    for(int y=0; y<9; y++) {
      for(int x=0; x<9; x++) {
        block[x] = state.get(x,y);
      }
      changed |= makeDistinct(block);
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
        changed |= makeDistinct(block);
      }
    }
    
    return changed;
  }
  
  public boolean makeDistinct(SudokuCell[] blocks) throws InconsistencyException {
    boolean changed = false;
    assert(blocks.length == 9);
    
    // build up an array of the numbers present in this "block"
    boolean found[] = new boolean[9];
    // count the cells that are still vague
    int unfinished = 0;
    
    
    for(int i=0; i<9; i++) {
      if(blocks[i].done()) {
        int index = blocks[i].get() - 1;
        if(found[index])
          throw new InconsistencyException();
        found[index] = true;
      } else {
        unfinished++;
      }
    }
    
    // if there no domains we could possibly amend, return no change
    if(unfinished == 0) return false;
    
    // for each unfinished cell
    for(SudokuCell cell : blocks) {
      if(cell.done()) continue;
      
      // for each already placed value
      for(int i=0; i<9; i++) {
        if(!found[i]) continue;
        
        // eliminate it from the current cell's domain
        if(cell.inDomain(i+1)) {
          cell.remove(i+1);
          if(cell.count() == 0)
            throw new InconsistencyException();
          changed = true;
        }
      }
    }
    
    // return true if we changed a domain
    return changed;
  }
}
