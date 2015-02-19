package quicket;


public class Dart{

   public enum CricketNumber {
       MISS       (0), FIFTEEN   (15), SIXTEEN   (16), SEVENTEEN (17),
       EIGHTEEN  (18), NINETEEN  (19), TWENTY    (20), BULL      (25);

       private final int points;

       CricketNumber(int points){
           this.points = points;
       }

       public int getPointValue() { return points; }
   }

   public enum Modifier {
       NONE (1), DOUBLE (2),  TRIPLE (3);

       private final int multiplier;

       Modifier(int multiplier){
          this.multiplier = multiplier;
       }

       public int getMultiplier(){
          return multiplier;
       }
   }

   private final CricketNumber number;
   private final Modifier modifier;
   private final int marks;
   private final int competetiveMarks;

   public Dart(CricketNumber number, Modifier modifier, int marks, int competetiveMarks){
        this.number  = number;
	this.modifier = modifier;
	this.marks   = marks;
	this.competetiveMarks = competetiveMarks;
   }

   public CricketNumber getNumber() { return number; }

   public Modifier getModifier() { return modifier; }

   public int getMarks() { return marks; }

   public int getCompetetiveMarks() { return competetiveMarks; }
}
