package multiTSP;

import java.util.List;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;



public class MultiTSPObjectiveCT implements Objective<MultiTSPSolution, MultiTSPData>
{

  public Evaluation evaluate(MultiTSPSolution solution, MultiTSPData data) 
  {  
     double feierabend = 0; 
     for(int car=0; car<solution.getPositionsVisitedByEachCar().size(); car++)
     {
      // compute sum of travel distances
      List<Integer> cities = solution.getPositionsVisitedByEachCar().get(car); 
      double totalDistance = data.getDistFromDepot(cities.get(0)); 
      for(int i=0; i<cities.size()-1; i++)
      {         
          totalDistance += data.getDistance(cities.get(i), cities.get(i+1));
      }
      totalDistance += data.getDistFromDepot(cities.get(cities.size()-1)); 
      
      if(totalDistance > feierabend)
      {
        feierabend=totalDistance; 
      }
     }
     
      return SimpleEvaluation.WITH_VALUE(feierabend);
  }
  
  
  
  public boolean isMinimizing() 
  {
      return true;
  }

}
  
  
