#include "sane.hh"
#include <cassert>


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

  for(int i=1; i<argc; i++) {
    parseSudokuFile(argv[i]);
  }
  printf("Finished.\n");
}

