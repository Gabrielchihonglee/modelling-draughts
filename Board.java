import javax.swing.*;
import java.awt.*;

public class Board {
  public static void main(String[] args) {
    JFrame frame = new JFrame("Draughts");
    frame.setSize(800, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);

    JPanel panel = new JPanel();
    frame.setContentPane(panel);

    GridLayout boardLayout = new GridLayout(8, 8); // creating a 8 by 8 grid as the board
    panel.setLayout(boardLayout);

    Square squares[] = new Square[64];

    for (int i = 0; i < 64; i++) {
      squares[i] = new Square(i % 8, i / 8 % 8);
      squares[i].addSquareButton(panel);
    }

    frame.revalidate();
  }
}
