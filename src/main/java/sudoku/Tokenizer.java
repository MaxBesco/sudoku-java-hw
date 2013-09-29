package sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

class Tokenizer {

  public static class TokenizeError extends RuntimeException {

    public TokenizeError(String msg) {
      super(msg);
    }

    public TokenizeError(String msg, Throwable cause) {
      super(msg, cause);
    }
  }

  public static int[] tokenize(String filePath) throws TokenizeError {
    int output[] = new int[81];
    try {
      BufferedReader fp = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

      int numRead = 0;
      while (numRead < 81) {
        int next = fp.read();
        if (next < 0) break;
        if (Character.isWhitespace(next)) continue;

        if (Character.isDigit(next)) {
          output[numRead] = Character.digit(next, 10);
          numRead++;
        } else if (next == '-') {
          numRead++;
        } else {
          throw new TokenizeError(filePath + ": Unexpected character " + (char) next + "!");
        }
      }

      if (numRead != 81) {
        throw new TokenizeError(filePath + ": Bad input length!");
      }

    } catch (FileNotFoundException ex) {
      throw new TokenizeError("Sudoku File not found: " + filePath, ex);
    } catch (IOException ex) {
      throw new TokenizeError("Unexpected IOError: ", ex);
    }

    return output;
  }

  public static void print(PrintStream out, int[] data) {
    for (int i = 0; i < data.length; i++) {
      out.print((i % 9 == 0) ? '\n' : ' ');
      int cell = data[i];
      if (cell == 0) {
        out.print('.');
      } else {
        out.print(cell);
      }
    }
    out.println();
  }
  
  public static void main(String[] args) {
    for (File fp : (new File("data/")).listFiles()) {
      System.out.println(fp.getName());
      int[] data = tokenize(fp.getPath());
      print(System.out, data);
    }

    System.out.println("/Tokenizer.main");
  }
}
