package multiTSP;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jamesframework.core.problems.sol.Solution;




public class MultiTSPSolution extends Solution {


  private List<List<Integer>> positionsVisitedByEachCar;

  public MultiTSPSolution(List<List<Integer>> positionsVisitedByEachCar){
      this.positionsVisitedByEachCar = positionsVisitedByEachCar;
  }

  public List<List<Integer>> getPositionsVisitedByEachCar(){
      return positionsVisitedByEachCar;
  }
  
  public int getNumberOfPositions()
  {
      int numPositions = 0; 
      for(List<Integer> tour : this.getPositionsVisitedByEachCar())
      {
        numPositions = numPositions + tour.size(); 
      }
      
      return numPositions; 
  }


  public void swapPositions(int i, int j){

      for(List<Integer> carTour : this.getPositionsVisitedByEachCar())
      {
        for(int step=0; step<carTour.size(); step++)
        {
          swapSomething: 
          {
              if(carTour.get(step).intValue()==i)
              {
                carTour.set(step, Integer.valueOf(j)); 
                break swapSomething; 
              }
              
              if(carTour.get(step).intValue()==j) 
              {
                carTour.set(step, Integer.valueOf(i));
                break swapSomething; 
              }
          }
        }
      }
  
  }
  
  public void relocateBox(int fromCar, int oldBoxNr, int toCar, int newBoxNr)
  {
     if(fromCar!=toCar)
     {
         List<Integer> fromCarTour = this.getPositionsVisitedByEachCar().get(fromCar); 
         List<Integer> toCarTour = this.getPositionsVisitedByEachCar().get(toCar); 
         
         final Integer box = fromCarTour.get(oldBoxNr).intValue(); 
         toCarTour.add(newBoxNr, box.intValue());
         fromCarTour.remove(oldBoxNr); 
     }
     else
     {
         List<Integer> carTour = this.getPositionsVisitedByEachCar().get(fromCar); 
         final int box = carTour.get(oldBoxNr); 
         carTour.remove(oldBoxNr); 
         carTour.add(newBoxNr, box);
        
     }
  }

  @Override
  public MultiTSPSolution copy() {
 
     return new MultiTSPSolution(new ArrayList<List<Integer>>(positionsVisitedByEachCar));
  }

  @Override
  public boolean equals(Object obj) {
      if (obj == null) {
          return false;
      }
      if (getClass() != obj.getClass()) {
          return false;
      }
      final MultiTSPSolution other = (MultiTSPSolution) obj;
      return Objects.equals(this.positionsVisitedByEachCar, other.positionsVisitedByEachCar);
  }

  @Override
  public int hashCode() {
      return Objects.hashCode(positionsVisitedByEachCar);
  }

  
  public void printSolutionInConsole()
  {
      System.out.println("MultiTSPSolution:"); 
      for(int car=0; car<this.getPositionsVisitedByEachCar().size(); car++)
      {
         System.out.print("Car"+car+" :"); 
         for(Integer box : this.getPositionsVisitedByEachCar().get(car))
         {
           System.out.print(" "+box);
         }
         System.out.println(); 
      }
      System.out.println(); 
  }
  
  public boolean check()
  {     
    int[] positions = new int[this.getNumberOfPositions()]; 
    for(int pos = 0; pos<positions.length; pos++)
      positions[pos]=0; 
    
    for(int car=0; car<this.getPositionsVisitedByEachCar().size(); car++)
    {
       for(Integer box : this.getPositionsVisitedByEachCar().get(car))
       {
          positions[box]++; 
       }
    }
    
    for(int pos = 0; pos<positions.length; pos++)
      if(positions[pos]!=1) 
        return false; 
    
    return true; 
  }
    
}
