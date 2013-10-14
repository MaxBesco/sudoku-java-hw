package sudoku;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
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
  
  static int guesses;
  
  public static SearchResult backtrackingSearch(SudokuState start, boolean first, Method getOpen, Inference[] inferenceMethods) 
          throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InconsistencyException {
    if(first) { // reset guesses
      guesses = 0;
    }
    //apply inference methods
    try {
      if (inferenceMethods.length!=0) {
        while (true) {
          //start.print(System.out);
          boolean changed = false;
          for (int j = 0 ; j < inferenceMethods.length ; j++)
            changed = changed || inferenceMethods[j].inferenceMethod(start);
          if (!changed) break;
        }
        //System.out.println("Success!");
      }
    } catch (InconsistencyException e) {
      return new Failure();
    }
    
    if(start.isComplete()) {
      int myGuesses = guesses;
      guesses = 0;
      return new Success(start, myGuesses);
    }
    
    // take the next cell
    SudokuCell var = (SudokuCell) getOpen.invoke(start);
    
    // in MRV an empty domain has no legal moves, so we backtrack quicker
    List<Integer> domain = start.legalMoves(var.index);
    if(domain.isEmpty())
      return new Failure();
    
    guesses += domain.size() - 1;
    assert(domain.size() <= 9 && domain.size() > 0);
    for (Integer i : domain) {    
      // calculate new board
      SudokuState next = start.set(var.index, i);
      if(!next.isConsistent()) continue;
      
      SearchResult sr = backtrackingSearch(next, false, getOpen, inferenceMethods);
      if(sr instanceof Success) return sr;
    }
    
    return new Failure();
  }
  
  public static void main(String[] args) 
          throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InconsistencyException {
    Method selectOpenVariable = SudokuState.class.getMethod("selectOpenVariable");
    Method selectOpenMRV = SudokuState.class.getMethod("selectOpenMRV");
    Inference[] justAC3 = {new AC3()};
    Inference[] allInference = {new AC3(), new XWing()};
    System.out.println("Show the numbers of guesses made for each of the 16 instances in the above collection. Try both plain backtracking and backtacking with the MRV (minimum remaining values) heuristic. ");
    System.out.println("plain backtracking, mrv, ac-3-plain, ac3-mrv, allinf-plain, allinf-mrv");
    
    List<File> testFiles = Arrays.asList((new File("data")).listFiles());
    Collections.sort(testFiles);
    
    Inference[] justRules = {};
    
    for (File file : testFiles){
      int[] data = Tokenizer.tokenize(file.getAbsolutePath());
      SudokuState prob = SudokuState.fromDefinition(data);
      SearchResult sr1 = backtrackingSearch(prob, true, selectOpenVariable, justRules);
      SearchResult sr2 = backtrackingSearch(prob, true, selectOpenMRV, justRules);
      SearchResult sr3 = backtrackingSearch(prob, true, selectOpenVariable, justAC3);
      SearchResult sr4 = backtrackingSearch(prob, true, selectOpenMRV, justAC3);
      SearchResult sr5 = backtrackingSearch(prob, true, selectOpenVariable, allInference);
      SearchResult sr6 = backtrackingSearch(prob, true, selectOpenMRV, allInference);
      assert(sr1 instanceof Success);
      assert(sr2 instanceof Success);
      assert(sr3 instanceof Success);
      assert(sr4 instanceof Success);
      assert(sr5 instanceof Success);
      assert(sr6 instanceof Success);
      System.out.println(String.format("%s\t%d,%d,%d,%d,%d,%d"
              , file.getName()
              , ((Success) sr1).numGuesses
              , ((Success) sr2).numGuesses
              , ((Success) sr3).numGuesses
              , ((Success) sr4).numGuesses
              , ((Success) sr5).numGuesses
              , ((Success) sr6).numGuesses
              ));
    }
  }
}
