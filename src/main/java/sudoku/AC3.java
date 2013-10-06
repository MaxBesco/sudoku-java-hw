/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * 
 */
public class AC3 implements Inference{
  @Override
  public boolean inferenceMethod(SudokuState state) throws InconsistencyException {
    // takes in a sudokustate, applies the ac3 inference method and returns
    // a boolean indicating whether or not the state was modified.
    boolean changed = false;
    Set<Pair> arcQueue = new HashSet<Pair>();
    // add every row pair
    for (int row = 0 ; row < 9 ; row++)
      for (int i = 0 ; i < 9 ; i++)
        for (int j = i+1 ; j < 9 ; j++)
          arcQueue.add(new Pair(state.get(row, i), state.get(row, j)));
    // add every col pair
    for (int col = 0 ; col < 9 ; col++)
      for (int i = 0 ; i < 9 ; i++)
        for (int j = i+1 ; j < 9 ; j++)
          arcQueue.add(new Pair(state.get(i, col), state.get(j, col)));
    // add every grid
    for (int gridx = 0 ; gridx < 3 ; gridx++)
      for (int gridy = 0 ; gridy < 3 ; gridy++)
        // loop over guts of grid to create constraints
        for (int comp1 = 0 ; comp1 < 9 ; comp1++)
          for (int comp2 = comp1 + 1 ; comp2 < 9 ; comp2++){
            int x1 = (comp1 % 3) + gridx;
            int y1 = (comp1 / 3) + gridy;
            int x2 = (comp2 % 3) + gridx;
            int y2 = (comp2 / 3) + gridy;
            arcQueue.add(new Pair(state.get(x1, y1), state.get(x2, y2)));
          }
    LinkedList<Pair> arcQ = new LinkedList<Pair>();
    for (Pair p : arcQueue)
      arcQ.add(p);
    while (!arcQ.isEmpty()) {
      Pair p = arcQ.removeFirst();
      if (revise(state, (SudokuCell) p.left, (SudokuCell) p.right))
        changed = true;
      else if (revise(state, (SudokuCell) p.right, (SudokuCell) p.left))
        changed = true;
      else arcQ.addLast(p);
    }
    return changed;
  }
  
  public boolean revise(SudokuState state, SudokuCell left, SudokuCell right) throws InconsistencyException{
    List<Integer> domainL = left.getDomain();
    List<Integer> domainR = right.getDomain();
    if (domainL.isEmpty() || domainR.isEmpty())
      throw new InconsistencyException();
    int target = domainL.get(0);
    if (domainL.size()==1 && domainR.contains(target)){
      System.out.println(String.format("Cell %d domain : %s, Cell %d domain : %s. Removed %d from cell %d"
              , left.index, domainL
              , right.index, domainR
              , target, right.index));
      right.remove(target);
      return true;
    } else return false;
  }
      
    
}