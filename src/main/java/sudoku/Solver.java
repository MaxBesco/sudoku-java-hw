package sudoku;

import sudoku.SudokuState.*;

public class Solver {
  public static abstract class SearchResult {
    public SearchResult() {
      this.done = false;
    }
    public boolean done = false;
  }
  public static class Failure extends SearchResult { }
  public static class Success extends SearchResult {
    public final SudokuState state;
    public Success(SudokuState state) {
      this.state = state;
      this.done = true;
    }
  }
  
  public static SearchResult backtrackingSearch(SudokuState start) {
    if(start.isComplete()) return new Success(start);
    
    SudokuCell var = start.selectOpenVariable();
        
    for(int i=0; i<9; i++) {
      if(!var.has(i)) continue;
      
      SudokuState next = start.set(var.index, i);
      if(!next.isConsistent()) continue;
      
      SearchResult sr = backtrackingSearch(next);
      if(sr.done) return sr;
    }
    
    return new Failure();
  }
  
  public static void main(String[] args) {
    int[] data = Tokenizer.tokenize("data/puz-001.txt");
    Tokenizer.print(System.out, data);
    SudokuState prob1 = SudokuState.fromDefinition(data);
    prob1.print(System.out);
    SearchResult sr = backtrackingSearch(prob1);
    assert(sr.done);
    ((Success) sr).state.print(System.out);
  }
}
