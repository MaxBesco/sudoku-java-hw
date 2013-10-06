package sudoku;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import sudoku.SudokuState.*;

public class Solver {
  public static abstract class SearchResult { }
  public static class Failure extends SearchResult { }
  public static class Success extends SearchResult {
    public final SudokuState state;
    public final int numGuesses;
    public Success(SudokuState state) {
      this(state, -1);
    }
    public Success(SudokuState state, int numGuesses) {
      this.numGuesses = numGuesses;
      this.state = state;
    }
  }
  
  public static SearchResult backtrackingSearch(SudokuState start, int guesses, Method getOpen) 
          throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    
    if(start.isComplete()) return new Success(start, guesses);
    
    // take a cell that's not done
    SudokuCell var = (SudokuCell) getOpen.invoke(start);
    
    List<Integer> domain = var.getDomain();
    
    for (Integer i : domain) {
      
      // calculate new board
      SudokuState next = start.set(var.index, i);
      if(!next.isConsistent()) continue;
      
      SearchResult sr = backtrackingSearch(next, guesses + domain.size() - 1, getOpen);
      if(sr instanceof Success) return sr;

    }
    return new Failure();
  }
  
  public static void main(String[] args) 
          throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    //int[] data = Tokenizer.tokenize("data/puz-001.txt");
    //Tokenizer.print(System.out, data);
    //SudokuState prob1 = SudokuState.fromDefinition(data);
    //prob1.print(System.out);
    Method selectOpenVariable = SudokuState.class.getMethod("selectOpenVariable");
    Method selectOpenMRV = SudokuState.class.getMethod("selectOpenMRV");
    Method ac3 = SudokuState.class.getMethod("ac3");
    //SearchResult sr1 = backtrackingSearch(prob1, 0, selectOpenVariable);
    //SearchResult sr2 = backtrackingSearch(prob1, 0, selectOpenMRV);
    System.out.println("Show the numbers of guesses made for each of the 16 instances in the above collection. Try both plain backtracking and backtacking with the MRV (minimum remaining values) heuristic. ");
    System.out.println("plain backtracking, mrv, ac-3");
    for (File file : (new File("data")).listFiles()){
      int[] data = Tokenizer.tokenize(file.getAbsolutePath());
      SudokuState prob = SudokuState.fromDefinition(data);
      SearchResult sr1 = backtrackingSearch(prob, 0, selectOpenVariable);
      SearchResult sr2 = backtrackingSearch(prob, 0, selectOpenMRV);
      SearchResult sr3 = backtrackingSearch(prob, 0, ac3);
      assert(sr1 instanceof Success);
      assert(sr2 instanceof Success);
      assert(sr3 instanceof Success);
      System.out.println(String.format("%d,%d,%d"
              , ((Success) sr1).numGuesses
              , ((Success) sr2).numGuesses
              , ((Success) sr3).numGuesses));
    }
  }
}
