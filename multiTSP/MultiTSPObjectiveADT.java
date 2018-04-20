package multiTSP;

import java.util.List;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;

public class MultiTSPObjectiveADT implements Objective<MultiTSPSolution, MultiTSPData>
{
    
    public Evaluation evaluate(MultiTSPSolution solution, MultiTSPData data) 
    {  
        double totalTimeToDeliverBoxes = 0; 
        for(int car=0; car<solution.getPositionsVisitedByEachCar().size(); car++)
        {
            List<Integer> carTour = solution.getPositionsVisitedByEachCar().get(car); 
    
            double time = 0; 
            
            double totalTimesToDeliverBoxesOfThisCar = 0; 
            
            if(carTour.size()>0)
            {
                  time =  data.getDistFromDepot(carTour.get(0)); 
                  totalTimesToDeliverBoxesOfThisCar= time*data.getNumBoxesAtPosition(carTour.get(0));
            }
            
            for(int i=1; i<carTour.size(); i++)
            {
                int fromCity = carTour.get(i-1);
                int toCity = carTour.get(i);
                
                time += data.getDistance(fromCity, toCity); 
              
                totalTimesToDeliverBoxesOfThisCar += time*data.getNumBoxesAtPosition(carTour.get(i)); 
            }
            
            totalTimeToDeliverBoxes = totalTimeToDeliverBoxes + totalTimesToDeliverBoxesOfThisCar; 
        }
        
        double averageTime = totalTimeToDeliverBoxes/(double) data.getNumBoxes();   
        
        return SimpleEvaluation.WITH_VALUE(averageTime);
    }
 

        
  
  
    
    public boolean isMinimizing() 
    {
        return true;
    }
}
