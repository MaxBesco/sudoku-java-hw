/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * 
 */
public class XWing implements Inference {
  
  public static class PairWithNum extends Pair {
    int matchedNum;
    public PairWithNum(SudokuCell l, SudokuCell r, int num) {
      super(l, r);
      this.matchedNum = num;
    }
  }
  
  private Integer num = -1;
  
  @Override
  public boolean inferenceMethod(SudokuState state) {
    boolean changed = false;
    List<PairWithNum> exclusiveRows = getAllExclusiveRowEntries(state);
    System.out.println(exclusiveRows.size());
    for (int i = 0 ; i < exclusiveRows.size() ; i++) {
      PairWithNum p = exclusiveRows.get(i);
      int matchedNum = p.matchedNum;
      SudokuCell a = p.left;
      SudokuCell b = p.right;
      if (!a.inDomain(matchedNum) || !b.inDomain(matchedNum) || a.count()==1 || b.count()==1) 
        continue;
      List<PairWithNum> potentialMatches = getAllExclusiveRowEntries(state);
      for (int j = 0 ; j < potentialMatches.size() ; j++ ) {
        PairWithNum pp = potentialMatches.get(j);
        if (!pp.equals(p) && pp.matchedNum == matchedNum && pp.left.inDomain(matchedNum) && pp.right.inDomain(matchedNum)
                && pp.left.count() > 1 && pp.right.count() > 1) {
          // delete matchedNum from all of the other cells in their shared columns
          System.out.println(String.format("XWing : (%d, %d) %s, (%d, %d) %s, (%d, %d) %s, (%d, %d) %s\t%d"
                  , a.x(), a.y(), Integer.toBinaryString(a.domain)
                  , b.x(), b.y(), Integer.toBinaryString(b.domain)
                  , pp.left.x(), pp.left.y(), Integer.toBinaryString(pp.left.domain)
                  , pp.right.x(), pp.right.y(), Integer.toBinaryString(pp.right.domain)
                  , matchedNum
                  ));
          int col1 = a.y();
          int col2 = b.y();
          for (int r = 0 ; r < 9 ; r++) 
            if (r!=a.x() && r!=pp.left.x()) {
              SudokuCell c = state.get(r, col1);
              SudokuCell d = state.get(r, col2);
              if (c.count() > 1) 
                if (c.inDomain(matchedNum)) {
                  System.out.println(String.format("(%d,%d) %s, (%d,%d) %s, (%d,%d) %s\t%d"
                          , a.x(), a.y(), Integer.toBinaryString(a.domain)
                          , b.x(), b.y(), Integer.toBinaryString(b.domain)
                          , c.x(), c.y(), Integer.toBinaryString(c.domain)
                          , matchedNum));
                  c.remove(matchedNum);
                  changed = true;
                }
              if (d.count() > 1) 
                if (d.inDomain(matchedNum)) {
                  System.out.println(String.format("(%d,%d) %s, (%d,%d) %s, (%d,%d) %s\t%d"
                          , a.x(), a.y(), Integer.toBinaryString(a.domain)
                          , b.x(), b.y(), Integer.toBinaryString(b.domain)
                          , d.x(), d.y(), Integer.toBinaryString(d.domain)
                          , matchedNum));
                  d.remove(matchedNum);
                  changed = true;
                }
            }
        }
      }
    }
    /*
    for (PairWithNum p : getAllExclusiveColEntries(state)) {
      int matchedNum = p.matchedNum;
      SudokuCell a = p.left;
      SudokuCell b = p.right;
      for (PairWithNum pp : getAllExclusiveRowEntries(state)) {
        if (!pp.equals(p) && pp.matchedNum == matchedNum) {
          // delete matchedNum from all of the other cells in their shared columns
          int row1 = a.x();
          int row2 = b.x();
          for (int c = 0 ; c < 9 ; c++) {
            if (c!=a.y() && c!=pp.left.y()) {
              if (!changed)
                changed = state.get(row1, c).inDomain(matchedNum) || state.get(row2, c).inDomain(matchedNum);
              state.get(row2, c).remove(matchedNum);
              state.get(row2, c).remove(matchedNum);
            }
          }
        }
      }
    }
    * */
    state.print(System.out);
    return changed;
  }
  
  public List<PairWithNum> getAllExclusiveRowEntries(SudokuState state) {
    List<PairWithNum> matchedPairs = new LinkedList<PairWithNum>();
    for (int row = 0 ; row < 9 ; row++) {
      for (int col = 0 ; col < 8 ; col++) {
        SudokuCell cell1 = state.get(row, col);
        for (Integer i : cell1.getDomain()) {
          int numMatches = 0;
          SudokuCell cell2 = null;
          for (int matchedCol = col+1 ; matchedCol < 9 ; matchedCol++) {
            cell2 = state.get(row, matchedCol);
            if (cell2.inDomain(i)) 
              numMatches++;
          }
          if (numMatches==1)
            matchedPairs.add(new PairWithNum(cell1, cell2, i));
        }
      }
    }
    return matchedPairs;
  }

    public List<PairWithNum> getAllExclusiveColEntries(SudokuState state) {
    List<PairWithNum> matchedPairs = new LinkedList<PairWithNum>();
    for (int col = 0 ; col < 9 ; col++) {
      for (int row = 0 ; row < 8 ; row++) {
        SudokuCell cell1 = state.get(row, col);
        for (Integer i : cell1.getDomain()) {
          int numMatches = 0;
          SudokuCell cell2 = null;
          for (int matchedRow = row+1 ; matchedRow < 9 ; matchedRow++) {
            cell2 = state.get(matchedRow, col);
            if (cell2.inDomain(i)) 
              numMatches++;
          }
          if (numMatches==1)
            matchedPairs.add(new PairWithNum(cell1, cell2, i));
        }
      }
    }
    return matchedPairs;
  }
  
}
