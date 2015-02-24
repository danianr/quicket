package quicket;

import quicket.Board;
import quicket.Gamestate;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.event.*;
import quicket.Board;
import java.lang.Thread;
import quicket.MouseAdapter;

public class Quicket{

   private static Board board = new Board(500, Color.black, Color.red, Color.white, Color.green);
   private static Gamestate gamestate = new Gamestate();
   private static MouseAdapter mouse = new quicket.MouseAdapter(board, gamestate);


   private static void createAndShowGUI() {
      JFrame frame = new JFrame("Quicket v3.0");
      JTable scoreboard = new JTable(gamestate);
      frame.getContentPane().add(scoreboard, BorderLayout.EAST);
      frame.getContentPane().add(board, BorderLayout.WEST);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      board.addMouseListener(mouse);
      frame.pack();
      frame.setVisible(true);
   }

   public static void main(String[] args) {


      for (String name: args){
         gamestate.addPlayer(name);
      }
      
      gamestate.startGame();

      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         } });
      try{
         Thread.sleep(40000);
      }catch (InterruptedException e){
      }
      System.exit(0);
   }
}
