package speedUp;

import java.util.HashMap;

import model.instance.Position;
import model.solution.CarSchedule;
import model.solution.DroneSchedule;
import model.solution.Solution;

public class AbandonablePositionChecker {
  

  
  final Solution solution; 
  final CarSchedule carSchedule; 
  final Integer carIndexLeadingToAbandonablePosition; 
  final Position toBeAbandonedPosition; 


  final private Boolean isAbandoningPossible; 



  //Drones and their last step before abandonable position 
  final HashMap<DroneSchedule,Integer> dronesHoppingOn; 
  final HashMap<DroneSchedule,Integer> dronesHoppingOff; 

  

  
  public AbandonablePositionChecker(
    Solution solution,
    CarSchedule carSchedule,
    Integer carIndexLeadingToAbandonablePosition) {
    super();
    this.solution = solution;
    this.carSchedule = carSchedule;
    this.carIndexLeadingToAbandonablePosition = carIndexLeadingToAbandonablePosition;
    this.toBeAbandonedPosition = this.getCarSchedule().getSteps().get(this.getCarIndexLeadingToAbandonablePosition()).getEndPoint(); 
    
    if(carIndexLeadingToAbandonablePosition<0||carIndexLeadingToAbandonablePosition>carSchedule.getSteps().size()-2)
      throw new RuntimeException("This index can't ever lead to a abandonable position, please adjust algorithm."); 

    this.dronesHoppingOn = new HashMap<DroneSchedule,Integer>(); 
    this.dronesHoppingOff = new HashMap<DroneSchedule,Integer>(); 


    
    
    this.isAbandoningPossible = this.initPossibility();  
    
  }


  private Solution getSolution() {
    return solution;
  }
  
  private CarSchedule getCarSchedule() {
    return carSchedule;
  }
  
  public Integer getCarIndexLeadingToAbandonablePosition() {
    return carIndexLeadingToAbandonablePosition;
  }
  
  private Position getToBeAbandonedPosition() {
    return toBeAbandonedPosition;
  }
  
  public boolean isAbandoningPossible() {
    return isAbandoningPossible;
  }
  

  
  private HashMap<DroneSchedule, Integer> getDronesHoppingOn() {
    return dronesHoppingOn;
  }
  
  private HashMap<DroneSchedule, Integer> getDronesHoppingOff() {
    return dronesHoppingOff;
  }
  


    private boolean initPossibility()
    {
 
        if(this.getCarIndexLeadingToAbandonablePosition() == this.getCarSchedule().getSteps().size()-1
            || this.getToBeAbandonedPosition().equals(this.getSolution().getInstance().getInstanceParameters().getDepot()))
        {     
              return false; 
        }
        
        this.getDronesHoppingOn().putAll(this.getCarSchedule().getDronesHoppingOnAfterStep(this.getCarIndexLeadingToAbandonablePosition(), this.getSolution())); 
        this.getDronesHoppingOff().putAll(this.getCarSchedule().getDronesHoppingOffAfterStep(this.getCarIndexLeadingToAbandonablePosition(), this.getSolution())); 

        if(this.getDronesHoppingOn().isEmpty()
          && this.getDronesHoppingOff().isEmpty())
        {       
              return true;               
        }
                        
        
            return false; 

    }
        
        
       
  
  
  


  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

}
