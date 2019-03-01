import java.awt.*;
import java.awt.event.*;
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
  private Square[] squares = new Square[64];
  private int playerTurn = 1;
  private boolean validFrom = false;
  private boolean jumpStreak = false;
  private int graphicsVer = 1;

  public static void main(String[] args) {
    Board board = new Board(args);
  }

  private Board(String[] args) {
    handleArgs(args);
    updateTitleTurn();
    setSize(800, 800);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    setVisible(true);

    JPanel panel = new JPanel();
    setContentPane(panel);

    GridLayout boardLayout = new GridLayout(8, 8);
    panel.setLayout(boardLayout);

    for (int i = 0; i < 64; i++) {
      if (graphicsVer == 1) {
        squares[i] = new Square(i % 8, i / 8 % 8);
      } else {
        squares[i] = new Square(i % 8, i / 8 % 8, graphicsVer, -1);
      }
      panel.add(squares[i]);
      squares[i].addActionListener(this);
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
          System.out.println("  --layout layout[]     specify starting layout");
          System.exit(0);
          break;
        case "--graphics":
          if((i + 1) >= args.length){
            System.out.println("Error: graphics version id not specified");
            System.exit(0);
          }
          graphicsVer = Integer.parseInt(args[i + 1]);
          if (graphicsVer > 2 || graphicsVer < 1) {
            System.out.println("Error: graphics version id does not exist");
            System.exit(0);
          }
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
    if (clickCount == 0 && ((Square) e.getSource()).getPiece() == playerTurn) {
      moveFrom = (Square) e.getSource();
      for (int i = 0; i < 64; i++) {
        if (moveFrom.canMoveTo(squares[i])) {
          validFrom = true;
          squares[i].highlightSelect();
        }
      }
      for (int i = 0; i < 64; i++) {
        if (moveFrom.canJumpTo(squares[i])) {
          validFrom = true;
          squares[i].highlightSelect();
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
      for (int i = 0; i < 64; i++) {
        squares[i].removeSelect();
      }
      if (moveFrom != moveTo && (moveFrom.canMoveTo(moveTo) || moveFrom.canJumpTo(moveTo))) {
        moveFrom.moveTo(moveTo);
        //                      System.out.println(moveFrom.getXPos() + ", " + moveTo.getXPos());
        if (Math.abs(moveFrom.getXPos() - moveTo.getXPos()) == 2) {
          int middleXPos = (moveFrom.getXPos() + moveTo.getXPos()) / 2;
          int middleYPos = (moveFrom.getYPos() + moveTo.getYPos()) / 2;
          squares[middleXPos + middleYPos * 8].kill();
          for (int i = 0; i < 64; i++) {
            squares[i].resetJumpStatus();
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
