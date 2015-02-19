package quicket;


public class Dart{

   public enum CricketNumber {
       MISS       (0), FIFTEEN   (15), SIXTEEN   (16), SEVETEEN  (17),
       EIGHTEEN  (18), NINETEEN  (19), TWENTY    (20), BULL      (25);

       private final int points;

       CricketNumber(int points){
           this.points = points;
       }

       private int singlePoints() { return points; }
       private int doublePoints() { return 2 * points; }
       private int triplePoints() { return (points == 25)? 0 : 3 * points; }
   }

   public enum Modifier { NONE, DOUBLE, TRIPLE }

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

   public int getPoints() {
      switch (modifier){
          case NONE:   return number.singlePoints();
          case DOUBLE: return number.doublePoints();
          case TRIPLE: return number.triplePoints();
      }
      return 0;
   }

   public int getMarks() { return marks; }

   public int getCompetetiveMarks() { return competetiveMarks; }
}
