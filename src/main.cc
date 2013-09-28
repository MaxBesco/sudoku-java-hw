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

int main(int argc, char **argv) {
  SudokuCell::Test();

  for(int i=1; i<argc; i++) {
    parseSudokuFile(argv[i]);
  }
  printf("Finished.\n");
}

