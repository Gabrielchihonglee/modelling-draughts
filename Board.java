import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;

/**
* Board is the main class for the draught game. It contains the main framework
* and logic for the game. Squares can be added to the board. Extends JFrame and
* implements ActionListener.
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
  private int redPieces = -1;
  private int whitePieces = -1;

  public static void main(String[] args) {
    Board board = new Board(args);
  }

  private Board(String[] args) {
    handleArgs(args);
    updateTitleTurn();
    setSize(800, 800);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        // export layout to file if the option was selected
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
        if (graphicsVer == 1 && defaultLayout) { // everything default
          squares[i][j] = new Square(j, i); // kept for assignment requirements fulfillment purpose
        } else if (defaultLayout) {
          squares[i][j] = new Square(j, i, graphicsVer, -2);
        } else {
          squares[i][j] = new Square(j, i, graphicsVer, layoutInput[i][j]);
        }
        panel.add(squares[i][j]);
        squares[i][j].addActionListener(this);
      }
    }

    revalidate(); // update frame to show newly added squares
  }

  /**
  * Handles command line interface arguments
  *
  * @param args an array of arguments
  */
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
            BufferedReader layoutInputBR = new BufferedReader(new FileReader(args[i + 1])); // read file specified in next argument
            String line;
            String[] lineElements;
            for (int fileRow = 0; (line = layoutInputBR.readLine()) != null; fileRow++) {
              lineElements = line.split(","); // expects comma seperated file format
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
    if (clickCount == 0 && ((((Square) e.getSource()).getPiece() == playerTurn) || (((Square) e.getSource()).getPiece() - 2 == playerTurn))) { // first click and matches player turn
      moveFrom = (Square) e.getSource();
      // goes over all squares to check if it's a valid moving target
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          if (moveFrom.canMoveTo(squares[i][j])) {
            if (!jumpStreak) { // player shouldn't be allowed to move after jumping
              validFrom = true;
              squares[i][j].highlightSelect();
            }
          }
        }
      }
      // goes over all squares to check if it's a valid jumping target
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
      } else if (jumpStreak) { // not valid from (can't jump anymore) and is in jump streak
        jumpStreak = false;
        playerTurn ^= 1;
      }
    } else { // second click
      updatePiecesCount();
      if (redPieces == 0) {
        endWin("White");
      }
      if (whitePieces == 0) {
        endWin("Red");
      }
      moveTo = (Square) e.getSource();
      clickCount = 0;
      validFrom = false;
      // clear all highlights
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          squares[i][j].removeSelect();
        }
      }
      if (moveFrom != moveTo && (moveFrom.canMoveTo(moveTo) || moveFrom.canJumpTo(moveTo))) {
        moveFrom.moveTo(moveTo);
        // handle jumps
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
          // triggers a click event after jumping to check if it can jump again
          moveTo.doClick();
        } else {
          playerTurn ^= 1;
        }
      }
    }
    updateTitleTurn();
  }

  /**
  * Updates frame title to show whose turn is it.
  */
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

  /**
  * Updates the count number of pieces of each player.
  */
  private void updatePiecesCount() {
    redPieces = 0;
    whitePieces = 0;
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (squares[i][j].getPiece() == 0 || squares[i][j].getPiece() == 2) {
          redPieces++;
        }
        if (squares[i][j].getPiece() == 1 || squares[i][j].getPiece() == 3) {
          whitePieces++;
        }
      }
    }
  }

  /**
  * Displays a cool ASCII art and announces end game.
  *
  * @param player string of player identity
  */
  private void endWin(String player) {
    System.out.println("\n\n        ,....,");
    System.out.println("      ,::::::<");
    System.out.println("     ,::/^\\'``.");
    System.out.println("    ,::/, `   e`.");
    System.out.println("   ,::; |        '.");
    System.out.println("   ,::|  \\___,-.  c)");
    System.out.println("   ;::|     \\   '-'");
    System.out.println("   ;::|      \\ ");
    System.out.println("   ;::|   _.=`\\ ");
    System.out.println("   `;:|.=` _.=`\\ ");
    System.out.println("     '|_.=`   __\\ ");
    System.out.println("     `\\_..==`` /");
    System.out.println("      .'.___.-'.");
    System.out.println("     /          \\ ");
    System.out.println("    ('--......--') ");
    System.out.println("    /'--......--'\\ ");
    System.out.println("    `''--......--''`");
    System.out.println("Game ended: " + player + " won!\n\n");
    System.exit(0);
  }
}
