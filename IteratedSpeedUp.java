package speedUp;

import localSearchParameters.LocalSearchParameters;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.solution.DroneSchedule;
import model.solution.DroneStep;
import model.solution.Solution;

public class IteratedSpeedUp {
  
  final Instance instance;
  
  final LocalSearchParameters speedUpParameters;
  
  final Solution solutionNoDrones;
  
  public IteratedSpeedUp(Instance instance, LocalSearchParameters speedUpParameters, Solution solutionNoDrones) {
    super();
    this.instance = instance;
    this.speedUpParameters = speedUpParameters;
    this.solutionNoDrones = solutionNoDrones;
    this.checkInput();
  }
  
  public Instance getInstance() {
    return instance;
  }
  
  public LocalSearchParameters getSpeedUpParameters() {
    return speedUpParameters;
  }
  
  public Solution getSolutionNoDrones() {
    return solutionNoDrones;
  }
  
  private boolean checkInput() {
    for (DroneSchedule droneSchedule : this.getSolutionNoDrones().getDroneSchedules())
      for (DroneStep droneStep : droneSchedule.getSteps())
        if (droneStep.getCarToRideOn() == null || droneStep.getDeliveredBox() != null)
          throw new RuntimeException(
            "Iterated Speed Up: Invalid input, drone "
              + droneStep.getDrone()
              + " is deliviering a box and/or not "
              + "always on a car.");
              
    return true;
  }
  
  public Solution compute() {
    
    System.out.print("ITERATED SPEED UP FOR EACH DRONE...");
    
    Solution solution = this.getSolutionNoDrones().deepCopy();
    
    for (Drone drone : this.getInstance().getDronesSortedByID()) {
      Car car = solution.getDroneSchedule(drone).getSteps().get(0).getCarToRideOn();
      
      SpeedUp speedUp = new SpeedUp(solution, car, 0, 0, drone, 0, 0, this.getSpeedUpParameters());
      
      solution = speedUp.doLocalSearch();
      
      System.out.print(" " + drone.getID());
    }
    
    solution.check();
    
    System.out.println(". Done. [" + solution.evaluate() + "]");
    
    return solution;
  }
  
}
