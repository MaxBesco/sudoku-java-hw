/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

/**
 *
 * 
 */
public interface Inference {
    public static class Pair{
      public Object left;
      public Object right;
      public Pair(Object l, Object r){
        this.left = l;
        this.right = r;
      }
    }
    public boolean inferenceMethod(SudokuState state) throws InconsistencyException;
}

class InconsistencyException extends Exception {
  public InconsistencyException() {
    super();
  }
}
