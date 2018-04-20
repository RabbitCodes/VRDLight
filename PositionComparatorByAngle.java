package multiTSP;

import java.util.Comparator;

import model.instance.Position;

public class PositionComparatorByAngle implements Comparator<Position> 
{
      boolean mathematicalOrientation; 
      
      public PositionComparatorByAngle(boolean mathematicalOrientation)
      {
           this.mathematicalOrientation = mathematicalOrientation; 
      }
      
      public PositionComparatorByAngle()
      {
           this.mathematicalOrientation = true; 
      }
      
      public boolean isMathematicalOrientation()
      {
            return mathematicalOrientation; 
      }
      
      
      public int compare(Position p1, Position p2) 
      {
          final int angleCompareValue =  Double.compare(
            Math.atan2(p1.getX(),p1.getY()),
            Math.atan2(p2.getX(),p2.getY())); 
          
          if(this.isMathematicalOrientation()==true)
          {
              if (angleCompareValue != 0)
                return - angleCompareValue;  
              else
                return Double.compare(p1.getEuclideanDistanceToDepot(), p2.getEuclideanDistanceToDepot()); 
            
          }
          else 
          {
            if (angleCompareValue != 0)
              return angleCompareValue;  
            else
              return Double.compare(p1.getEuclideanDistanceToDepot(), p2.getEuclideanDistanceToDepot()); 
          }
        }
}

