package sudoku;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static sudoku.Inference.debug;

public class XWing implements Inference {
  
  public static class PairWithNum extends Pair{
    int matchedNum;
    public PairWithNum(SudokuCell l, SudokuCell r, int num) {
      super(l, r);
      this.matchedNum = num;
    }
  }

  @Override
  public boolean inferenceMethod(SudokuState state) throws InconsistencyException {
    
    for (int i = 1 ; i <= 9 ; i ++){
      
      List<PairWithNum> allMatchingPairs = new LinkedList<PairWithNum>();
      
      for (int row = 0 ; row < 9 ; row++)
        allMatchingPairs.addAll(findMatchingPairs(getCellsContainingNumber(getCellsAtRow(state, row), i)));
      
      if (debug) {
        for (PairWithNum pair : allMatchingPairs)
           System.out.println(String.format("%d:%s, %d:%s \t%d", 
                   pair.left.index, pair.left.domain
                   , pair.right.index, pair.right.index
                   , pair.matchedNum
                   ));
      }
      
      if (allMatchingPairs.isEmpty())
        continue;
      
      for (PairWithNum pair : allMatchingPairs) {
        
        PairWithNum otherPair = getMatchedRow(state, pair);
        if (otherPair==null) continue;
        
        if (debug){
           System.out.println(String.format("%d:%s, %d:%s, %d:%s, %d:%s \t%d", 
                   pair.left.index, pair.left
                   , pair.right.index, pair.right
                   , otherPair.left.index, otherPair.left
                   , otherPair.right.index, otherPair.right
                   , pair.matchedNum
                   ));
          }
        
          boolean changed = false;
          for (SudokuCell cell : getCellsContainingNumber(getCellsAtCol(state, pair.left.x()), pair.matchedNum)) {
            if (cell.index==pair.left.index || cell.index==otherPair.left.index)
              continue;
            if (cell.count()<2){
              if (debug) System.out.println(cell.index);
              throw new InconsistencyException();
            }
            cell.remove(pair.matchedNum);
            if (debug)
              System.out.println(String.format("Removed %d from cell %d. New domain is: %s"
                      , pair.matchedNum, cell.index, cell));
            changed = true;
          }
          for (SudokuCell cell : getCellsContainingNumber(getCellsAtCol(state, pair.right.x()), pair.matchedNum)){
            if (cell.index==pair.right.index || cell.index==otherPair.right.index)
              continue;
            if (cell.count()<2){
              if (debug) System.out.println(cell.index);
              throw new InconsistencyException();
            }
            cell.remove(pair.matchedNum);
            if (debug)
              System.out.println(String.format("Removed %d from cell %d. New domain is: %s"
                      , pair.matchedNum, cell.index, cell));
            changed = true;
          }
          if (changed) return true;
      }
     
    }
    if (debug) System.out.println("no elimination");
    return false;
  }
  
  public PairWithNum getMatchedCol(SudokuState state, PairWithNum pair) {
    for (int col = 0 ; col < 9 ; col++){
      if (col==pair.left.x())
        continue;
      else {
        List<SudokuCell> cells = getCellsContainingNumber(getCellsAtCol(state, col), pair.matchedNum);
        assert(false);
      }
    }
    return null;
  }
  
  public PairWithNum getMatchedRow(SudokuState state, PairWithNum pair) {
    for (int row = 0 ; row < 9 ; row++) {
      if (row==pair.left.y())
        continue;
      else {
        List<SudokuCell> cells = getCellsContainingNumber(getCellsAtRow(state, row), pair.matchedNum);
        if (cells.size()!=2)
          continue;
        SudokuCell a = cells.get(0);
        SudokuCell b = cells.get(1);
        assert(a!=pair.left);
        assert(a.x() < b.x());
        assert(pair.left.x() < pair.right.x());
        if (a.x()==pair.left.x() && b.x()==pair.right.x())
          return new PairWithNum(a, b, pair.matchedNum);
      }
    } 
    return null;
  }
  
  public List<PairWithNum> findMatchingPairs(List<SudokuCell> cellsAtRowOrCol) {
    List<PairWithNum> retval = new LinkedList<PairWithNum>();
    for (int i = 1 ; i <= 9 ; i++) {
      List<SudokuCell> cells = getCellsContainingNumber(cellsAtRowOrCol, i);
      if (cells.size()==2)
        retval.add(new PairWithNum(cells.get(0), cells.get(1), i));
    }
    return retval;
  }
  
  
  public List<SudokuCell> getCellsContainingNumber(List<SudokuCell> cells, int num) {
    List<SudokuCell> retval = new LinkedList<SudokuCell>();
    for (SudokuCell cell : cells)
      if (cell.inDomain(num))
        retval.add(cell);
    return retval;
  }
  
  public List<SudokuCell> getCellsAtRow(SudokuState state, int row) {
    SudokuCell[] entries = new SudokuCell[9];
    for (int col = 0 ; col < 9 ; col++)
      entries[col] = state.get(col, row);
    return Arrays.asList(entries);
  }
  
  public List<SudokuCell> getCellsAtCol(SudokuState state, int col) {
    SudokuCell[] entries = new SudokuCell[9];
    for (int row = 0 ; row < 9 ; row++)
      entries[row] = state.get(col, row);
    return Arrays.asList(entries);
  }

  public static void main(String[] args) throws InconsistencyException {
    SudokuState st = SudokuState.fromDefinition(new int[] {
      1,0,0,0,0,0,5,6,9,
      4,9,2,0,5,6,1,0,8,
      0,5,6,1,0,9,2,4,0,
      0,0,9,6,4,0,8,0,1,
      0,6,4,0,1,0,0,0,0,
      2,1,8,0,3,5,6,0,4,
      0,4,0,5,0,0,0,1,6,
      9,0,5,0,6,1,4,0,2,
      6,2,1,0,0,0,0,0,5
    });
    
    Inference rules = new AC3();
    rules.inferenceMethod(st);
    st.print(System.out);
    
    (new XWing()).inferenceMethod(st);

    st.print(System.out);
  }
}