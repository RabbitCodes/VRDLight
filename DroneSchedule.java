package model.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import model.instance.Box;
import model.instance.Drone;
import model.instance.Position;

public class DroneSchedule {
  
  final Drone drone;
  
  final ArrayList<DroneStep> steps;
  
  int readyForStepNumber;  // variable to use when going through the delivery
                           // plan (should have used a hashmap)
  
  double time; // variable to use when going through the delivery plan (should
               // have used a hashmap)
  
  public DroneSchedule(Drone drone, ArrayList<DroneStep> steps) {
    super();
    this.drone = drone;
    this.steps = steps;
    this.readyForStepNumber = 0;
    this.time = 0;
  }
  
  public ArrayList<DroneStep> getSteps() {
    return steps;
  }
  
  public Drone getDrone() {
    return drone;
  }
  
  public int getReadyForStepNumber() {
    return readyForStepNumber;
  }
  
  public void setReadyForStepNumber(int readyForStepNumber) {
    this.readyForStepNumber = readyForStepNumber;
  }
  
  public void increaseReadyForStepNumberby1() {
    this.setReadyForStepNumber(this.getReadyForStepNumber() + 1);
  }
  
  public double getTime() {
    return time;
  }
  
  public void setTime(double time) {
    this.time = time;
  }
  
  public void printDroneScheduleInConsole() {
    System.out.println("DRONE SCHEDULE OF " + this.getDrone().getID() + ":");
    int stepNumber = 0;
    for (DroneStep step : this.getSteps()) {
      System.out.println("(" + stepNumber + ") " + step.toString());
      stepNumber++;
    }
    System.out.println("");
  }
  
  public boolean equalsStepDeepCopy(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DroneSchedule other = (DroneSchedule) obj;
    if (drone == null) {
      if (other.drone != null)
        return false;
    } else if (!drone.equals(other.drone))
      return false;
      
    if (steps == null) {
      if (other.steps != null || steps.size() != other.getSteps().size())
        return false;
    } else {
      // check each step
      for (int step = 0; step < this.getSteps().size(); step++)
        if (this.getSteps().get(step).equals(other.getSteps().get(step)) == false)
          return false;
    }
    
    return true;
  }
  
  public Collection<Box> getByThisDroneDeliveredBoxes() {
    Collection<Box> deliveredBoxes = new HashSet<Box>();
    
    for (DroneStep step : this.getSteps()) {
      if (step.getDeliveredBox() != null) {
        deliveredBoxes.add(step.getDeliveredBox());
      }
    }
    
    return deliveredBoxes;
  }
  
  public int getCorrespondingStep(CarStep carStep) {
    for (int step = 0; step < this.getSteps().size(); step++)
      if (this.getSteps().get(step).getEndPoint().equals(carStep.getEndPoint())
        && carStep.getDronesToTake().contains(this.getDrone()))
        return step;
        
    return -1;
  }
  
  public int getNumberOfStep(DroneStep droneStep) {
    return this.getSteps().indexOf(droneStep);
  }
  
  public int getNumberOfStepLeadingTo(Position position) {
    for (int step = 0; step < this.getSteps().size(); step++)
      if (this.getSteps().get(step).getEndPoint().equals(position))
        return step;
        
    return -1;
  }
  
  public boolean checkSteps(
    Collection<Position> possibleEndPoints,
    final Position depot,
    boolean droneLoopsForbidden,
    final boolean droneChargingEdgeMandatory,
    final Solution solution) {
    
    if (this.getSteps().get(this.getSteps().size() - 1).getEndPoint().equals(depot) == false) {
      throw new RuntimeException("Tour of Drone " + this.getDrone().getID() + " doesn't end in depot");
    }
    
    Collection<Position> visitedPositions = new HashSet<Position>();
    
    for (DroneStep droneStep : this.getSteps()) {
      if (droneStep.getDeliveredBox() != null
        && droneStep.getDeliveredBox().getDestination().equals(droneStep.getEndPoint()) == false) {
        throw new RuntimeException(
          "Drone Step <" + droneStep.toString() + "> fails because box gets delivered at wrong destination.");
      }
      
      if (droneStep.getDrone().equals(this.getDrone()) == false) {
        throw new RuntimeException(
          "Tour of Drone " + this.getDrone().getID() + " contains drone step with wrong drone.");
      }
      
      if (possibleEndPoints.contains(droneStep.getEndPoint()) == false) {
        throw new RuntimeException(
          "Tour of Drone "
            + this.getDrone().getID()
            + " contains step with infeasible end point "
            + droneStep.getEndPoint().toString());
      }
      
      if (droneLoopsForbidden && visitedPositions.contains(droneStep.getEndPoint())) {
        throw new RuntimeException(
          "Drone " + this.getDrone().getID() + " visits " + droneStep.getEndPoint() + " twice.");
      }
      
      visitedPositions.add(droneStep.getEndPoint());
      
    }
    return true;
  }
  
}
