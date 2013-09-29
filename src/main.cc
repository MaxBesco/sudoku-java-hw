#include "sane.hh"
#include <cassert>

class SudokuCell {
  public:
    explicit SudokuCell() {
      possible = 0x1ff;
    }
    explicit SudokuCell(int specific) {
      allow(specific);
    }
    int left() const {
      int count = 0;
      int tmp = possible;

      for(int i=0; i<9; i++) {
        count += tmp & 1;
        tmp >>= 1;
      }
      return count;
    }
    void exclude(int n) {
      possible &= ~mask(n);
    }
    bool has(int n) const { return possible & mask(n); }
    bool done() const {
      return (possible & 0x1ff) == 0;
    }
  private:
    bool inDomain(int x) const {
      return x > 0 && x <= 9;
    }
    u16 mask(int n) const { 
      assert(inDomain(n));
      return 1 << (n-1);
    }
    void allow(int n) { 
      possible |= mask(n);
    }
    u16 possible;
  public:
    static void Test() {
      SudokuCell cell;
      assert(cell.left() == 9);
      cell.exclude(6);
      assert(cell.left() == 8);
      assert(!cell.has(6));
    }
};

vector<int> readTokens(const char * const fileName) {
  vector<int> tokens;

  // read tokens
  ifstream fp;
  fp.open(fileName);
  while(fp && tokens.size() < 81) {
    int next = fp.get();

    if(next == EOF) break;
    if(isspace(next)) continue;

    if(isdigit(next)) {
      tokens.push_back(next - '0');
    } else if(next == '-') {
      tokens.push_back(-1);
    } else {
      cerr << "Found unexpected character '" << char(next)
        << "' in file '" << fileName << "!";
      exit(-1);
    }
  }

  if(tokens.size() != 81) {
    cerr << "Only found " << tokens.size() << " cells in file '" << fileName << "!";
    exit(-1);
  }
  
  return tokens;
}

void parseSudokuFile(const char * const fileName) {
  cout << "parseSudokuFile: '" << fileName << "'...\n";

  vector<int> tokens = readTokens(fileName);

  // print tokens
  for(int i=0; i<tokens.size(); i++) {
    if(i % 9 == 0) {
      cout << '\n';
    } else {
      cout << ' ';
    }
    
    int cell = tokens[i];
    if(cell == -1) {
      cout << ".";
    } else {
      cout << cell;
    }
  }
  cout << '\n';

}

class SudokuState;
struct SudokuCellRef {
  SudokuCellRef(SudokuState &parent, int idx) : state(parent), pos(idx) { }
  SudokuCell& get() { return state.get(pos); }
  bool has(int n) { return get().has(n); }
  
  SudokuState &state;
  int pos;
};


class SudokuState {
  public:
    SudokuState() { }
    SudokuState(vector<int> cells) {
      for(int i=0; i<81; i++) {
        int cell = cells[i];

        if(cell == -1) {
          data[i] = SudokuCell();
        } else {
          data[i] = SudokuCell(cell);
        }
      }
    }
    SudokuState(const SudokuState &other) {
      for(int i=0; i<81; i++) {
        data[i] = other.data[i];
      }
    }

    bool isComplete() {
      return true;
    }

    SudokuCellRef getUnfinished() { 
      for(int i=0; i<81; i++) {
        if(!data[i].done()) {
          return SudokuCellRef(*this, i);;
        }
      }
      assert(false);
      return SudokuCellRef(*this, -1);
    }

    SudokuCell& get(int idx) { 
      assert(idx > 0 && idx < 81);
      return data[idx];
    }
  private:
    SudokuCell data[81];
};


class BTRSearchResult {
  public:
    static BTRSearchResult Success(SudokuState goal) { return BTRSearchResult(true, goal); }
    static BTRSearchResult Failure() { return BTRSearchResult(false, SudokuState()); }
    
    bool success;
    SudokuState state;
  private:
    BTRSearchResult(bool win, SudokuState goal) {
      success = win;
      state = goal;
    }
};

BTRSearchResult btrsearch(SudokuState state) {
  if(state.isComplete()) {
    return BTRSearchResult::Success(state);
  }
  SudokuCellRef var = state.getUnfinished();
  for(int value = 1; value <= 9; value++) {
    if(!var.has(value)) continue;

    BTRSearchResult result = btrsearch(state.use(var, value));
    if(result.success) {
      return result;
    }
  }
  return BTRSearchResult::Failure();
}

int main(int argc, char **argv) {
  SudokuCell::Test();

  for(int i=1; i<argc; i++) {
    parseSudokuFile(argv[i]);
  }
  printf("Finished.\n");
}

