/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

/**
 * Looks ahead one step and sees if any possibilities in the domain lead to a contradiction
 * Apparently this has a name it is called "shaving"
 * 
 */
public class BoundedLookahead implements Inference {
  
  @Override
  public boolean inferenceMethod(SudokuState state) throws InconsistencyException {
    // picks a guess and sees if any of those guesses will lead to a contradiction
    // if it does, remove that guess from the domain and return true
    for (SudokuCell cell : state.cells)
      if (cell.count() > 1) {
        for (Integer i : cell.getDomain()) {
            SudokuState copy = state.set(cell.index, i);
            try{
              (new AC3()).inferenceMethod(copy);
            } catch (InconsistencyException ie){
              cell.remove(i);
              return true;
            }
            if (!copy.isConsistent()) {
              cell.remove(i);
              return true;
            }
          }
      }
    return false;
  }

}
