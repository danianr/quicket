package quicket;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.event.*;
import quicket.BoardNumbers;
import java.lang.Thread;
import quicket.MouseAdapter;

public class BoardTest {

   private static Board board = new Board(500, Color.black, Color.red, Color.white, Color.green);
   private static MouseAdapter mouse = new quicket.MouseAdapter(board);

   private static void createAndShowGUI() {
      JFrame frame = new JFrame("Quicket v3.0");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(board);
      board.addMouseListener(mouse);
      frame.pack();
      frame.setVisible(true);
   }

   public static void mouseClicked(MouseEvent e){
      return;   
   }

   public static void main(String[] args) {

      //Schedule a job for the event-dispatching thread:
      //creating and showing this application's GUI.

      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         } });
      try{
         Thread.sleep(10000);
      }catch (InterruptedException e){
      }
      System.exit(0);
   }
}
