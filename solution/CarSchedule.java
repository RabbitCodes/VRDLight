package model.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import model.instance.Box;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.instance.Position;

public class CarSchedule {
  
  final Car car;
  
  final ArrayList<CarStep> steps;
  
  int readyForStepNumber; // variable to use when going through the delivery
                          // plan (should have used a hashmap)
  
  double time; // variable to use when going through the delivery plan (should
               // have used a hashmap)
  
  public CarSchedule(Car car, ArrayList<CarStep> steps) {
    super();
    this.car = car;
    this.steps = steps;
    this.readyForStepNumber = 0;
    this.time = 0;
  }
  
  public Car getCar() {
    return car;
  }
  
  public ArrayList<CarStep> getSteps() {
    return steps;
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
  
  public CarSchedule deepCopy() {
    ArrayList<CarStep> newSteps = new ArrayList<CarStep>();
    for (CarStep oldCarStep : this.getSteps())
      newSteps.add(oldCarStep.deepCopy());
      
    return new CarSchedule(this.getCar(), newSteps);
  }
  
  public void printCarScheduleInConsole() {
    System.out.println("CAR SCHEDULE OF CAR " + this.getCar().getID() + ":");
    
    int stepNumber = 0;
    for (CarStep step : this.getSteps()) {
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
    CarSchedule other = (CarSchedule) obj;
    
    if (car == null) {
      if (other.car != null)
        return false;
    } else if (!car.equals(other.car))
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
    ;
    
    return true;
  }
  
  public Collection<Box> getByThisCarDeliveredBoxes() {
    Collection<Box> deliveredBoxes = new HashSet<Box>();
    
    for (CarStep step : this.getSteps()) {
      if (step.getDeliveredBoxes() != null && step.getDeliveredBoxes().size() > 0) {
        deliveredBoxes.addAll(step.getDeliveredBoxes());
      }
    }
    
    return deliveredBoxes;
  }
  
  public List<Position> getVisitedPositions() {
    List<Position> visitedPositions = new ArrayList<Position>();
    
    for (CarStep step : this.getSteps())
      visitedPositions.add(step.getEndPoint());
      
    return visitedPositions;
  }
  
  public List<Position> getVisitedPositions(int numberOfStepsIgnoringBefore, int numberOfStepsIgnoringAfter) {
    List<Position> visitedPositions = new ArrayList<Position>();
    
    for (int step = numberOfStepsIgnoringBefore; step < this.getSteps().size() - numberOfStepsIgnoringAfter; step++)
      visitedPositions.add(this.getSteps().get(step).getEndPoint());
      
    return visitedPositions;
  }
  
  public int getCorrespondingStep(DroneStep droneStep) {
    if (droneStep.getCarToRideOn().equals(this.getCar()))
      for (int step = 0; step < this.getSteps().size(); step++)
        if (this.getSteps().get(step).getEndPoint().equals(droneStep.getEndPoint())
          && this.getSteps().get(step).getDronesToTake().contains(droneStep.getDrone()))
          return step;
          
    return -1;
  }
  
  public int getNumberOfStep(CarStep carStep) {
    return this.getSteps().indexOf(carStep);
  }
  
  public int getNumberOfStepLeadingTo(Position position) {
    for (int step = 0; step < this.getSteps().size(); step++)
      if (this.getSteps().get(step).getEndPoint().equals(position))
        return step;
        
    return -1;
  }
  
  public List<Position> getObligatoryCarPositions(int afterThisStepIncluded, int beforeThisStepExcluded) {
    List<Position> positions = new ArrayList<Position>();
    for (int step = afterThisStepIncluded; step < beforeThisStepExcluded; step++) {
      if (step < this.getSteps().size() - 1) {
        Collection<Drone> dronesNow = this.getSteps().get(step).getDronesToTake();
        
        Collection<Drone> dronesNextStep = this.getSteps().get(step + 1).getDronesToTake();
        
        if (dronesNow.containsAll(dronesNextStep) == false || dronesNextStep.containsAll(dronesNow) == false)    // drone
        // loops
        // are
        // forbidden
        // here
        {
          positions.add(this.getSteps().get(step).getEndPoint());
        }
      }
    }
    
    return positions;
  }
  
  public List<Position> getObligatoryCarPositionsExceptDrone(
    int afterThisStepIncluded,
    int beforeThisStepExcluded,
    Drone drone) {
    List<Position> positions = new ArrayList<Position>();
    for (int step = afterThisStepIncluded; step < beforeThisStepExcluded; step++) {
      if (step < this.getSteps().size() - 1) {
        Collection<Drone> dronesNow = new HashSet<Drone>();
        if (this.getSteps().get(step).getDronesToTake() != null)
          dronesNow = new HashSet<Drone>(this.getSteps().get(step).getDronesToTake());
        dronesNow.remove(drone);
        
        Collection<Drone> dronesNextStep = new HashSet<Drone>(this.getSteps().get(step + 1).getDronesToTake());
        dronesNextStep.remove(drone);
        
        if (dronesNow.containsAll(dronesNextStep) == false || dronesNextStep.containsAll(dronesNow) == false) {
          positions.add(this.getSteps().get(step).getEndPoint());
        }
      }
    }
    
    return positions;
  }
  
  public Collection<Drone> getDronesHoppingOnAfterStep(int step) {
    if (step < 0 || step > this.getSteps().size() - 2)
      return new HashSet<Drone>();
      
    Collection<Drone> dronesBefore = this.getSteps().get(step).getDronesToTake();
    Collection<Drone> dronesAfter = this.getSteps().get(step + 1).getDronesToTake();
    Collection<Drone> dronesHoppingOn = new HashSet<Drone>();
    
    for (Drone drone : dronesAfter)
      if (dronesBefore.contains(drone) == false)
        dronesHoppingOn.add(drone);
        
    return dronesHoppingOn;
  }
  
  public HashMap<DroneSchedule, Integer> getDronesHoppingOnAfterStep(int step, Solution solution) {
    if (step < 0 || step > this.getSteps().size() - 2)
      return new HashMap<DroneSchedule, Integer>();
      
    final HashMap<DroneSchedule, Integer> dronesHoppingOn = new HashMap<DroneSchedule, Integer>();
    final CarStep carStepAfter = this.getSteps().get(step + 1);
    
    for (Drone drone : carStepAfter.getDronesToTake()) {
      final DroneSchedule droneSchedule = solution.getDroneSchedule(drone);
      final int correspondingStep = droneSchedule.getCorrespondingStep(carStepAfter);
      if (correspondingStep == -1)
        throw new RuntimeException("Something is wrong with this method.");
        
      if (correspondingStep != 0
        && droneSchedule.getSteps().get(correspondingStep - 1).getCarToRideOn() != this.getCar())
        dronesHoppingOn.put(droneSchedule, correspondingStep - 1);
    }
    
    return dronesHoppingOn;
  }
  
  public Collection<Drone> getDronesHoppingOffAfterStep(int step) {
    if (step < 0 || step > this.getSteps().size() - 2)
      return new HashSet<Drone>();
      
    Collection<Drone> dronesBefore = this.getSteps().get(step).getDronesToTake();
    Collection<Drone> dronesAfter = this.getSteps().get(step + 1).getDronesToTake();
    Collection<Drone> dronesHoppingOff = new HashSet<Drone>();
    
    for (Drone drone : dronesBefore)
      if (dronesAfter.contains(drone) == false)
        dronesHoppingOff.add(drone);
        
    return dronesHoppingOff;
  }
  
  public HashMap<DroneSchedule, Integer> getDronesHoppingOffAfterStep(int step, Solution solution) {
    if (step < 0 || step > this.getSteps().size() - 2)
      return new HashMap<DroneSchedule, Integer>();
      
    final HashMap<DroneSchedule, Integer> dronesHoppingOff = new HashMap<DroneSchedule, Integer>();
    final CarStep carStep = this.getSteps().get(step);
    
    for (Drone drone : carStep.getDronesToTake()) {
      final DroneSchedule droneSchedule = solution.getDroneSchedule(drone);
      final int correspondingStep = droneSchedule.getCorrespondingStep(carStep);
      if (correspondingStep == -1)
        throw new RuntimeException("Something is wrong with this method.");
        
      if (correspondingStep != droneSchedule.getSteps().size()
        && droneSchedule.getSteps().get(correspondingStep + 1).getCarToRideOn() != this.getCar())
        dronesHoppingOff.put(droneSchedule, correspondingStep);
    }
    
    return dronesHoppingOff;
  }
  
  public int getStepWhereEndPointIsForCarNearestTo(
    Position position,
    Instance instance,
    int numberOfCarStepsBefore,
    int numberOfCarStepsAfter) {
    int stepLeadingToCarNextPositionTo = -1;
    double distance = Double.MAX_VALUE;
    
    for (int step = numberOfCarStepsBefore; step < this.getSteps().size() - numberOfCarStepsAfter; step++) {
      if (instance.getCarDrivingTime(this.getSteps().get(step).getEndPoint(), position) < distance) {
        stepLeadingToCarNextPositionTo = step;
        distance = instance.getCarDrivingTime(this.getSteps().get(step).getEndPoint(), position);
      }
    }
    
    return stepLeadingToCarNextPositionTo;
  }
  
  public DroneSchedule constructScheduleOfADroneRidingThisCar_alsoChangesCarSchedule(Drone drone) {
    ArrayList<DroneStep> droneSteps = new ArrayList<DroneStep>();
    for (CarStep carStep : this.getSteps()) {
      droneSteps.add(new DroneStep(drone, carStep.getEndPoint(), this.getCar(), null));
      carStep.getDronesToTake().add(drone);
    }
    
    return new DroneSchedule(drone, droneSteps);
  }
  
  public int getNumberOfCorrespondingDroneStep(int indexOfCarStep, DroneSchedule droneSchedule) {
    if (indexOfCarStep < 0 || indexOfCarStep >= this.getSteps().size())
      return -1;
      
    CarStep carStep = this.getSteps().get(indexOfCarStep);
    if (carStep.getDronesToTake().contains(droneSchedule.getDrone()) == false)
      return -1;
      
    if (indexOfCarStep == 0)
      return 0;
      
    if (indexOfCarStep == this.getSteps().size() - 1)
      return droneSchedule.getSteps().size() - 1;
      
    for (int indexOfDroneStep = 1; indexOfDroneStep < droneSchedule.getSteps().size(); indexOfDroneStep++) {
      if (droneSchedule.getSteps().get(indexOfDroneStep).getCarToRideOn() != null
        && droneSchedule.getSteps().get(indexOfDroneStep).getCarToRideOn().equals(this.getCar())
        && droneSchedule.getSteps().get(indexOfDroneStep - 1).getEndPoint().equals(
          this.getSteps().get(indexOfCarStep - 1).getEndPoint())
        && droneSchedule
          .getSteps()
          .get(indexOfDroneStep)
          .getEndPoint()
          .equals(this.getSteps().get(indexOfCarStep).getEndPoint()))
        return indexOfDroneStep;
    }
    
    return -1;
  }
  
  public
    int
    getLastCarStepInARowWithoutDroneInteraction(final int firstCarStepWithoutDroneOfThisRow, final Drone drone) {
    int lastCarStepBeforeDroneReturns = firstCarStepWithoutDroneOfThisRow;
    while (lastCarStepBeforeDroneReturns + 1 < this.getSteps().size()
      && !this.getSteps().get(lastCarStepBeforeDroneReturns + 1).getDronesToTake().contains(drone)) {
      lastCarStepBeforeDroneReturns++;
    }
    
    return lastCarStepBeforeDroneReturns;
  }
  
  public int getLastCarStepInARowWithDroneRidingCar(final int firstCarStepWithDroneOfThisRow, final Drone drone) {
    int lastCarStepTogether = firstCarStepWithDroneOfThisRow;
    while (lastCarStepTogether + 1 < this.getSteps().size()
      && (this.getSteps().get(lastCarStepTogether + 1).getDronesToTake() != null
        && this.getSteps().get(lastCarStepTogether + 1).getDronesToTake().contains(drone))) {
      lastCarStepTogether++;
    }
    
    return lastCarStepTogether;
  }
  
  public boolean checkSteps(
    Collection<Position> possibleEndPoints,
    final Position depot,
    final boolean carLoopsForbidden,
    final boolean droneChargingEdgeMandatory) {
    
    if (this.getSteps().get(this.getSteps().size() - 1).getEndPoint().equals(depot) == false) {
      throw new RuntimeException("Tour of " + car.getID() + " doesn't end in depot");
    }
    
    Collection<Position> visitedPositions = new HashSet<Position>();
    
    for (CarStep carStep : this.getSteps()) {
      
      for (Box box : carStep.getDeliveredBoxes()) {
        if (box.getDestination().equals(carStep.getEndPoint()) == false) {
          throw new RuntimeException(
            "CarStep "
              + carStep.toString()
              + " fails because "
              + box.getID()
              + " gets delivered at wrong destination. ");
        }
      }
      
      if (!carStep.getCar().equals(this.getCar())) {
        throw new RuntimeException("Tour of " + car.getID() + " containts Step with wrong Car.");
      }
      
      if (!possibleEndPoints.contains(carStep.getEndPoint())) {
        throw new RuntimeException("Tour of " + car.getID() + " contains Step with infeasible EndPoint");
      }
      
      if (carLoopsForbidden
        && visitedPositions.contains(carStep.getEndPoint())
        && !carStep.getEndPoint().equals(depot)) {
        throw new RuntimeException("Car " + this.getCar().getID() + " visits " + carStep.getEndPoint() + " twice.");
      }
      
      visitedPositions.add(carStep.getEndPoint());
    }
    
    return true;
  }
  
}
