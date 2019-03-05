import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;

/**
* Board is the main class for the draught game. It contains the main framework
* and logic for the game. Squares can be added to the board.
*
* @author Gabriel Lee
*/
public class Board extends JFrame implements ActionListener {
  private int clickCount = 0;
  private Square moveFrom;
  private Square moveTo;
  private Square[][] squares = new Square[8][8];
  private int playerTurn = 1;
  private boolean validFrom = false;
  private boolean jumpStreak = false;
  private int graphicsVer = 1;
  private boolean defaultLayout = true;
  private boolean exitExport = false;
  private int[][] layoutInput = new int[8][8];

  public static void main(String[] args) {
    Board board = new Board(args);
  }

  private Board(String[] args) {
    //Arrays.fill(layoutInput, -1);
    handleArgs(args);
    updateTitleTurn();
    setSize(800, 800);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        if (exitExport) {
          try {
            BufferedWriter layoutOutputBW = new BufferedWriter(new FileWriter("export.csv"));
            String line = "";
            for (int i = 0; i < 8; i++) {
              for (int j = 0; j < 8; j++) {
                line += squares[i][j].getPiece() + ",";
              }
              line = line.substring(0, line.length() - 1);
              line += "\n";
            }
            layoutOutputBW.write(line);
            layoutOutputBW.close();
          } catch(IOException eClose) {
            eClose.printStackTrace();
          }
        }
        System.exit(0);
      }
    });
    setResizable(false);
    setVisible(true);

    JPanel panel = new JPanel();
    setContentPane(panel);

    GridLayout boardLayout = new GridLayout(8, 8);
    panel.setLayout(boardLayout);

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (graphicsVer == 1 && defaultLayout) {
          squares[i][j] = new Square(j, i);
        } else if (defaultLayout) {
          squares[i][j] = new Square(j, i, graphicsVer, -2);
        } else {
          squares[i][j] = new Square(j, i, graphicsVer, layoutInput[i][j]);
        }
        panel.add(squares[i][j]);
        squares[i][j].addActionListener(this);
      }
    }

    revalidate();
  }

  private void handleArgs(String[] args) {
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "-h":
        case "--help":
          System.out.println("Usage:");
          System.out.println("  java " + this.getClass().getName() + " [options]");
          System.out.println("Options:");
          System.out.println("  -h, --help            show this help text");
          System.out.println("  --graphics versionId  specify graphics");
          System.out.println("  --layout layout       specify layout file");
          System.out.println("  -e, --export          exports layout on close");
          System.exit(0);
          break;
        case "--graphics":
          if((i + 1) >= args.length || args[i + 1].charAt(0) == '-'){
            System.out.println("Error: graphics version id not specified");
            System.exit(0);
          }
          graphicsVer = Integer.parseInt(args[i + 1]);
          if (graphicsVer > 2 || graphicsVer < 1) {
            System.out.println("Error: graphics version id does not exist");
            System.exit(0);
          }
          break;
        case "--layout":
          defaultLayout = false;
          try {
            BufferedReader layoutInputBR = new BufferedReader(new FileReader(args[i + 1]));//args[i + 1]
            String line;
            String[] lineElements;
            for (int fileRow = 0; (line = layoutInputBR.readLine()) != null; fileRow++) {
              lineElements = line.split(",");
              for (int fileCol = 0; fileCol < lineElements.length; fileCol++) {
                layoutInput[fileRow][fileCol] = Integer.parseInt(lineElements[fileCol]);
              }
            }
          } catch(IOException eInput) {
            eInput.printStackTrace();
          }
          break;
        case "-e":
        case "--export":
          exitExport = true;
          break;
      }
    }
  }

  /**
  * Invoked when any square is clicked. Handles player's order, highlighting
  * posible targets, etc.
  *
  * @param e the ActionEvent passed from the ActionListener.
  */
  public void actionPerformed(ActionEvent e) {
    updateTitleTurn();
    if (clickCount == 0 && ((((Square) e.getSource()).getPiece() == playerTurn) || (((Square) e.getSource()).getPiece() - 2 == playerTurn))) {
      moveFrom = (Square) e.getSource();
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          if (moveFrom.canMoveTo(squares[i][j])) {
            if (!jumpStreak) {
              validFrom = true;
              squares[i][j].highlightSelect();
            }
          }
        }
      }
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          if (moveFrom.canJumpTo(squares[i][j])) {
            validFrom = true;
            squares[i][j].highlightSelect();
          }
        }
      }
      if (validFrom) {
        clickCount = 1;
      } else if (jumpStreak) {
        jumpStreak = false;
        playerTurn ^= 1;
      }
    } else {
      moveTo = (Square) e.getSource();
      clickCount = 0;
      validFrom = false;
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          squares[i][j].removeSelect();
        }
      }
      if (moveFrom != moveTo && (moveFrom.canMoveTo(moveTo) || moveFrom.canJumpTo(moveTo))) {
        moveFrom.moveTo(moveTo);
        if (Math.abs(moveFrom.getXPos() - moveTo.getXPos()) == 2) {
          int middleXPos = (moveFrom.getXPos() + moveTo.getXPos()) / 2;
          int middleYPos = (moveFrom.getYPos() + moveTo.getYPos()) / 2;
          squares[middleYPos][middleXPos].kill();
          for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
              squares[i][j].resetJumpStatus();
            }
          }
          jumpStreak = true;
          moveTo.doClick();
        } else {
          playerTurn ^= 1;
        }
      }
    }
    updateTitleTurn();
  }

  private void updateTitleTurn() {
    switch (playerTurn) {
      case 1:
        setTitle("Draughts: White's turn");
        break;
      case 0:
        setTitle("Draughts: Red's turn");
        break;
      default:
        System.out.println("Error: Unknown or unexpected playerTurn value.");
        break;
    }
  }
}
