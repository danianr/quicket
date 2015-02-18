package quicket;
import quicket.Slice;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Board extends JPanel{

   private List<Slice> slices;
   private static final int ordering[] = { 20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5 };
   private static final double doubleThickness = 0.035;
   private static final double tripleThickness = 0.035;
   private static final double triplePosition  = 0.85;
   private static final double fontPadding = 45;
   private final double diameter;
   private Area doubleRing;
   private Area tripleRing;
   private Area bull;
   private Area doubleBull;
   private Color dark;
   private Color light;
   private Color lightComplementary;
   private Color darkComplementary;
   private AffineTransform shift;

   public Board(double diameter, Color dark, Color darkComplemetary, Color light, Color lightComplementary){
      this.slices = new ArrayList<Slice>(20);
      this.diameter = diameter;
      this.shift = new AffineTransform(1, 0, 0, 1, fontPadding, fontPadding);
      for (int i: ordering){
          this.slices.add(new Slice(i, diameter));
      }
      this.dark = Color.black;
      this.darkComplementary = Color.red;
      this.light = Color.white;
      this.lightComplementary = Color.green;


      Ellipse2D doubleRingOuter = new Ellipse2D.Double(0, 0, diameter, diameter);
      Ellipse2D doubleRingInner = new Ellipse2D.Double(0, 0, (1 - doubleThickness) * diameter,
					                     (1 - doubleThickness) * diameter);

      doubleRingInner.setFrameFromDiagonal(doubleThickness * diameter, doubleThickness * diameter,
              			   (1 - doubleThickness) * diameter, (1 - doubleThickness) * diameter);

      this.doubleRing = new Area(doubleRingOuter);
      this.doubleRing.exclusiveOr(new Area(doubleRingInner));

      Ellipse2D tripleRingOuter = new Ellipse2D.Double(triplePosition * diameter, triplePosition * diameter,
                                                       triplePosition * diameter, triplePosition * diameter);
      tripleRingOuter.setFrameFromDiagonal((1 - triplePosition) * diameter, (1-triplePosition) * diameter,
                                           triplePosition * diameter, triplePosition * diameter);

      Ellipse2D tripleRingInner = new Ellipse2D.Double(0, 0, (triplePosition - tripleThickness) * diameter, 
                                                       (triplePosition - tripleThickness) * diameter);
      tripleRingInner.setFrameFromDiagonal( (1 - triplePosition + tripleThickness) * diameter,
                                            (1 - triplePosition + tripleThickness) * diameter,
					    (triplePosition - tripleThickness) * diameter, 
					    (triplePosition - tripleThickness) * diameter); 

      this.tripleRing = new Area(tripleRingOuter);
      this.tripleRing.exclusiveOr(new Area(tripleRingInner));

      Ellipse2D bullRingOuter   = new Ellipse2D.Double(0.45 * diameter, 0.45 * diameter, 0.1 * diameter, 0.1 * diameter);
      Ellipse2D bullRingInner   = new Ellipse2D.Double(0.475 * diameter, 0.475 * diameter,  0.05 * diameter, 0.05 * diameter);
      this.doubleBull = new Area(bullRingInner);
      this.bull = new Area(bullRingOuter);
      this.bull.exclusiveOr(this.doubleBull);
   }

   public Dimension getPreferredSize() {
      return new Dimension((int) (this.diameter + 2 * shift.getTranslateX()), 
                           (int) (this.diameter + 2 * shift.getTranslateY()));
   }


   public boolean isBull(double x, double y) throws NoninvertibleTransformException {
      Point2D src = new Point2D.Double(x, y);
      return this.isBull(src);
   }

   public boolean isBull(Point2D src) throws NoninvertibleTransformException {
      Point2D dst = new Point2D.Double();
      shift.inverseTransform(src, dst);
      return (this.bull.contains(dst) || this.doubleBull.contains(dst) );
   }

   public boolean isDoubleRing(double x, double y) throws NoninvertibleTransformException { 
      Point2D src = new Point2D.Double(x, y);
      Point2D dst = new Point2D.Double();
      shift.inverseTransform(src, dst);
      return (this.doubleRing.contains(dst) || this.doubleBull.contains(dst) );
   }

   public boolean isTripleRing(double x, double y) throws NoninvertibleTransformException {
      Point2D src = new Point2D.Double(x, y);
      Point2D dst = new Point2D.Double();
      shift.inverseTransform(src, dst);
      return this.tripleRing.contains(dst);
   }

   public int getNumber(double x, double y) throws NoninvertibleTransformException {
      Point2D src = new Point2D.Double(x, y);
      Point2D dst = new Point2D.Double();
      shift.inverseTransform(src, dst);
      if ( isBull(src)) return 25;
      for (Slice sl: slices){
          if ( sl.contains(dst) ) return sl.getNumber();
      }
      return 0;
   }

   public void paint(Graphics g){
       Graphics2D g2 = (Graphics2D) g;
       AffineTransform at = g2.getTransform();
       AffineTransform st = new AffineTransform(at);        //shifted transform
       st.concatenate(shift);
       g2.setBackground(Color.black);
       g2.clearRect(0, 0, (int) (diameter + 2*shift.getTranslateX()), (int) (diameter + 2*shift.getTranslateY()) );
       g2.setTransform(st);
       Area specialRing = new Area(this.tripleRing);
       specialRing.add(this.doubleRing);

       for (Slice sl: this.slices){
          g2.setColor( sl.isDark()? this.dark : this.light);
          g2.fill(sl);
	  Area specials = new Area(sl);
	  specials.intersect(specialRing);
          g2.setColor( sl.isDark()? this.darkComplementary : this.lightComplementary);
          g2.fill(specials);
       }
       g2.setColor(this.darkComplementary);
       g2.fill(this.bull);
       g2.setColor(this.dark);
       g2.fill(this.doubleBull);
       g2.setColor(Color.white);

       Font font = new Font(Font.DIALOG, Font.PLAIN, 24);
       g2.setFont(font);
       FontMetrics fm = g2.getFontMetrics();


       g2.drawString("20", (float) diameter /2 - fm.charWidth('2'), (float) 0.0);
       AffineTransform rt = new AffineTransform(st);
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);

       g2.drawString("1", (float) (diameter /2 - fm.charWidth('1') / 2), (float) 0.0);
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("18", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("4", (float) (diameter /2 - fm.charWidth('4') / 2), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("13", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("6", (float) (diameter /2 - fm.charWidth('6') / 2), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("10", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("15", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("2", (float) (diameter /2 - fm.charWidth('2') / 2), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("17", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("3", (float) (diameter /2 - fm.charWidth('3') / 2), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("19", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("7", (float) (diameter /2 - fm.charWidth('7') / 2), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("16", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("8", (float) (diameter /2 - fm.charWidth('8') / 2), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("11", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("14", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("9", (float) (diameter /2 - fm.charWidth('9') / 2), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("12", (float) diameter /2 - fm.charWidth('1'), (float) 0.0);
       
       rt.rotate(Math.PI / 10.0, diameter / 2, diameter / 2);
       g2.setTransform(rt);
       g2.drawString("5", (float) (diameter /2 - fm.charWidth('5') / 2), (float) 0.0);
       
   }

}
