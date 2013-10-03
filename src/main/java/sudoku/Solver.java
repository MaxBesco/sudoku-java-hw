package sudoku;

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
  
  public static SearchResult backtrackingSearch(SudokuState start) {
    if(start.isComplete()) return new Success(start);
    
    // take a cell that's not done
    SudokuCell var = start.selectOpenVariable();
    
    // for all possibilities
    for(int i=0; i<9; i++) {
      // that are still possible
      if(!var.inDomain(i)) continue;
      
      // calculate new board
      SudokuState next = start.set(var.index, i);
      if(!next.isConsistent()) continue;
      
      SearchResult sr = backtrackingSearch(next);
      if(sr instanceof Success) return sr;
    }
    
    return new Failure();
  }
  
  public static SearchResult backtrackingSearch(SudokuState start, int guesses) {
    
    if(start.isComplete()) return new Success(start);
    
    // take a cell that's not done
    SudokuCell var = start.selectOpenVariable();
    
    List<Integer> domain = var.getDomain();
    
    for (Integer i : domain) {
      
      // calculate new board
      SudokuState next = start.set(var.index, i);
      if(!next.isConsistent()) continue;
      
      SearchResult sr = backtrackingSearch(next, guesses + domain.size() - 1);
      if(sr instanceof Success) return sr;

    }
    return new Failure();
  }
  
  public static void main(String[] args) {
    int[] data = Tokenizer.tokenize("data/puz-001.txt");
    Tokenizer.print(System.out, data);
    SudokuState prob1 = SudokuState.fromDefinition(data);
    prob1.print(System.out);
    SearchResult sr = backtrackingSearch(prob1);
    assert(sr instanceof Success);
    ((Success) sr).state.print(System.out);
  }
}
