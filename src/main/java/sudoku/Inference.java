package sudoku;

/**
 *
 * 
 */
public interface Inference {
    public static class Pair{
      public SudokuCell left;
      public SudokuCell right;
      public Pair(SudokuCell l, SudokuCell r){
        this.left = l;
        this.right = r;
      }
      @Override
      public int hashCode(){
        return (new Integer(left.index)).hashCode() ^ (new Integer(right.index)).hashCode();
      }
      @Override
      public boolean equals(Object op){
        Pair p = (Pair) op;
        return p.left.index==left.index && p.right.index==right.index;
      }
    }
    public boolean inferenceMethod(SudokuState state) throws InconsistencyException;
}

class InconsistencyException extends Exception {
  public InconsistencyException() {
    super();
  }
}
