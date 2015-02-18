package quicket;
import java.awt.*;
import java.awt.geom.*;
import java.lang.Math;

public class Slice implements Shape{

    private int number;
    private GeneralPath gp;
    
    public Slice(int n, double diameter){
       this.number = n;
       double centerAngle = findCenterAngle(n);
       double r = diameter / 2.0;

       // Pad the radius values by 20% to ensure all points are without
       // the outer circle, the resultant shape will look like an arrow
       double ax =  r * Math.sin(centerAngle - Math.PI / 20.0) + r;
       double ay =  r * Math.cos(centerAngle - Math.PI / 20.0) + r;
       Point2D a = new Point2D.Double(ax, ay);
     
       double bx =  r * Math.sin(centerAngle + Math.PI / 20.0) + r;
       double by =  r * Math.cos(centerAngle + Math.PI / 20.0) + r; 
       Point2D b = new Point2D.Double(bx, by);
       
       double cx =  r * Math.sin(centerAngle) + r;
       double cy =  r * Math.cos(centerAngle) + r; 
       Point2D c = new Point2D.Double(cx, cy);

       Arc2D arc = new Arc2D.Double(Arc2D.OPEN);
       arc.setArcByTangent(a, c, b, r);
       this.gp = new GeneralPath(arc);
       gp.lineTo(bx, by);
       gp.lineTo(r, r);
       gp.lineTo(ax, ay);
       gp.closePath();
    }


    private double findCenterAngle(int n){
       double extent = Math.PI / 10.0;

       if (n == 20) return Math.PI;
       if (n ==  1) return Math.PI - extent;
       if (n == 18) return Math.PI - 2 * extent;
       if (n ==  4) return Math.PI - 3 * extent;
       if (n == 13) return Math.PI - 4 * extent;
       if (n ==  6) return Math.PI / 2;
       if (n == 10) return 4 * extent;
       if (n == 15) return 3 * extent;
       if (n ==  2) return 2 * extent;
       if (n == 17) return extent;
       if (n ==  3) return 0;
       if (n == 19) return 2 * Math.PI - extent;
       if (n ==  7) return 2 * Math.PI - 2 * extent;
       if (n == 16) return 2 * Math.PI - 3 * extent;
       if (n ==  8) return 2 * Math.PI - 4 * extent;
       if (n == 11) return 3 * Math.PI / 2;
       if (n == 14) return Math.PI + 4 * extent;
       if (n ==  9) return Math.PI + 3 * extent;
       if (n == 12) return Math.PI + 2 * extent;
       if (n ==  5) return Math.PI + extent;

       return -1;
    }

    public int getNumber(){
       return this.number;
    }

    public boolean isDark(){
       if (this.number == 20) return true;
       if (this.number == 18) return true;
       if (this.number == 13) return true;
       if (this.number == 10) return true;
       if (this.number ==  2) return true;
       if (this.number ==  3) return true;
       if (this.number ==  7) return true;
       if (this.number ==  8) return true;
       if (this.number == 14) return true;
       if (this.number == 12) return true;
       return false;
    }

    public boolean contains(double x, double y){
        return gp.contains(x, y);
    }

    public boolean contains(double x, double y, double w, double h){
        return gp.contains(x, y, w, h);
    }

    public boolean contains(Point2D p){
        return gp.contains(p);
    }

    public boolean contains(Rectangle2D r){
        return gp.contains(r);
    }

    public Rectangle getBounds(){
        return gp.getBounds();
    }

    public Rectangle2D getBounds2D(){
        return gp.getBounds2D();
    }

    public PathIterator getPathIterator(AffineTransform at){
        return gp.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness){
        return gp.getPathIterator(at, flatness);
    }

    public boolean intersects(double x, double y, double w, double h){
        return gp.intersects(x, y, w, h);
    }

    public boolean intersects(Rectangle2D r){
        return gp.intersects(r);
    }
}
