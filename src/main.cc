#include "sane.hh"

void parseSudokuFile(const string &fileName) {
  cout << "parseSudokuFile: '" << fileName << "'...\n";
}

int main(int argc, char **argv) {
  vector<string> args;
  for(int i=1; i<argc; i++) {
    args.push_back(argv[i]);
  }

  for(auto &arg: args) {
    parseSudokuFile(arg);
  }
  printf("Finished.\n");
}

