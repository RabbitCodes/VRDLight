package model.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import model.instance.Box;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.instance.Position;

public class Solution {
  
  final Instance instance;
  
  final Collection<CarSchedule> carSchedules;
  
  final Collection<DroneSchedule> droneSchedules;
  
  public Solution(Instance instance, Collection<CarSchedule> carSchedules, Collection<DroneSchedule> droneSchedules) {
    super();
    this.instance = instance;
    this.carSchedules = carSchedules;
    this.droneSchedules = droneSchedules;
  }
  
  public Collection<CarSchedule> getCarSchedules() {
    return carSchedules;
  }
  
  public Collection<DroneSchedule> getDroneSchedules() {
    return droneSchedules;
  }
  
  public Instance getInstance() {
    return instance;
  }
  
  public CarSchedule getCarSchedule(Car car) {
    for (CarSchedule carSchedule : this.getCarSchedules()) {
      if (carSchedule.getCar().equals(car))
        return carSchedule;
    }
    
    return null;
  }
  
  public DroneSchedule getDroneSchedule(Drone drone) {
    for (DroneSchedule droneSchedule : this.getDroneSchedules()) {
      if (droneSchedule.getDrone().equals(drone))
        return droneSchedule;
    }
    
    return null;
  }
  
  public void printSolutionInConsole() {
    System.out.println("INFORMATION ABOUT THE SOLUTION! ");
    for (Car car : this.getInstance().getCarsSortedByID()) {
      this.getCarSchedule(car).printCarScheduleInConsole();
    }
    for (Drone drone : this.getInstance().getDronesSortedByID()) {
      this.getDroneSchedule(drone).printDroneScheduleInConsole();
    }
  }
  
  public Solution deepCopy() {
    Collection<CarSchedule> newCarSchedules = new HashSet<CarSchedule>();
    for (CarSchedule oldCarSchedule : this.getCarSchedules()) {
      
      ArrayList<CarStep> newSteps = new ArrayList<CarStep>();
      for (CarStep oldCarStep : oldCarSchedule.getSteps())
        newSteps.add(oldCarStep.deepCopy());
        
      newCarSchedules.add(new CarSchedule(oldCarSchedule.getCar(), newSteps));
    }
    
    Collection<DroneSchedule> newDroneSchedules = new HashSet<DroneSchedule>();
    for (DroneSchedule oldDroneSchedule : this.getDroneSchedules()) {
      
      ArrayList<DroneStep> newSteps = new ArrayList<DroneStep>();
      for (DroneStep oldDroneStep : oldDroneSchedule.getSteps())
        newSteps.add(oldDroneStep.deepCopy());
        
      newDroneSchedules.add(new DroneSchedule(oldDroneSchedule.getDrone(), newSteps));
    }
    
    return new Solution(this.getInstance(), newCarSchedules, newDroneSchedules);
  }
  
  public Collection<Box> getByCarsAndDronesDeliveredBoxes() {
    Collection<Box> boxes = new HashSet<Box>();
    for (CarSchedule carSchedule : this.getCarSchedules())
      boxes.addAll(carSchedule.getByThisCarDeliveredBoxes());
      
    for (DroneSchedule droneSchedule : this.getDroneSchedules())
      boxes.addAll(droneSchedule.getByThisDroneDeliveredBoxes());
      
    return boxes;
  }
  
  public DroneSchedule getDroneScheduleThatDeliversBox(Box box) {
    for (DroneSchedule droneSchedule : this.getDroneSchedules()) {
      if (droneSchedule.getByThisDroneDeliveredBoxes().contains(box))
        return droneSchedule;
    }
    
    return null;
  }
  
  private boolean checkVehicleScheduleBijection() {
    
    for (Car car : this.getInstance().getCars()) {
      if (this.getCarSchedule(car) == null) {
        throw new RuntimeException("Car " + car.getID() + " has no schedule.");
      }
    }
    
    if (this.getCarSchedules().size() > this.getInstance().getCars().size()) {
      throw new RuntimeException("Number of CarSchedules does not equal number of cars.");
    }
    
    for (Drone drone : this.getInstance().getDrones()) {
      if (this.getDroneSchedule(drone) == null) {
        throw new RuntimeException("Drone " + drone.getID() + " has no schedule.");
      }
    }
    
    if (this.getDroneSchedules().size() != this.getInstance().getDrones().size()) {
      throw new RuntimeException("Number of DroneSchedules does not equal number of drones.");
    }
    return true;
  }
  
  private boolean checkIfEachBoxGetsDelivered() {
    int numberOfDeliveredBoxes = 0;
    
    Collection<Box> deliveredBoxes = new HashSet<Box>();
    for (CarSchedule carSchedule : this.getCarSchedules()) {
      deliveredBoxes.addAll(carSchedule.getByThisCarDeliveredBoxes());
      numberOfDeliveredBoxes = numberOfDeliveredBoxes + carSchedule.getByThisCarDeliveredBoxes().size();
    }
    
    for (DroneSchedule droneSchedule : this.getDroneSchedules()) {
      deliveredBoxes.addAll(droneSchedule.getByThisDroneDeliveredBoxes());
      numberOfDeliveredBoxes = numberOfDeliveredBoxes + droneSchedule.getByThisDroneDeliveredBoxes().size();
    }
    
    if (deliveredBoxes.equals(this.getInstance().getBoxes()) == false
      || this.getInstance().getBoxes().size() != numberOfDeliveredBoxes) {
      throw new RuntimeException("Not all boxes get delivered.");
    }
    
    return true;
  }
  
  private boolean checkFlyingTimeAndDeadLocks() {
    
    // set all vehicles to the start
    for (CarSchedule carSchedule : this.getCarSchedules()) {
      carSchedule.setReadyForStepNumber(0);
    }
    for (DroneSchedule droneSchedule : this.getDroneSchedules()) {
      droneSchedule.setReadyForStepNumber(0);
      droneSchedule.setTime(0); // current flying time, is set to 0 when charged
    }
    
    boolean someoneMovedInOuterWhile = true;
    Collection<Position> byCarsVisitedPositions = new HashSet<Position>();
    
    while (someoneMovedInOuterWhile == true) {
      someoneMovedInOuterWhile = false;
      
      for (CarSchedule carSchedule : this.getCarSchedules()) {
        // Step alone
        while (carSchedule.getReadyForStepNumber() < carSchedule.getSteps().size()
          && (carSchedule.getSteps().get(carSchedule.getReadyForStepNumber()).getDronesToTake() == null
            || carSchedule.getSteps().get(carSchedule.getReadyForStepNumber()).getDronesToTake().isEmpty() == true)) {
          final Position newPosition = carSchedule.getSteps().get(carSchedule.getReadyForStepNumber()).getEndPoint();
          
          if (newPosition.equals(this.getInstance().getInstanceParameters().getDepot())
            && carSchedule.getSteps().size() == carSchedule.getReadyForStepNumber() - 1)
            throw new RuntimeException(
              "Car " + carSchedule.getCar().getID() + " is at the depot before finishing its tour.");
              
          if (!newPosition.equals(this.getInstance().getInstanceParameters().getDepot())) {
            if (byCarsVisitedPositions.contains(newPosition))
              throw new RuntimeException(" Position " + newPosition.toString() + " is visited twice by a car.");
              
            byCarsVisitedPositions.add(newPosition.deepCopy());
          }
          
          carSchedule.increaseReadyForStepNumberby1();
          
          someoneMovedInOuterWhile = true;
        }
        
        // Step with at least one drone
        boolean someoneMovedInInnerWhile = true;
        while (carSchedule.getReadyForStepNumber() < carSchedule.getSteps().size()
          && someoneMovedInInnerWhile == true) {
          someoneMovedInInnerWhile = false;
          boolean allDronesCarNeedsAreHere = true;
          final CarStep nextStep = carSchedule.getSteps().get(carSchedule.getReadyForStepNumber());
          
          if (nextStep.getDronesToTake() != null && nextStep.getDronesToTake().isEmpty() == false) {
            for (Drone droneCarWaitsFor : nextStep.getDronesToTake()) {
              DroneSchedule scheduleOfDroneCarWaitsFor = this.getDroneSchedule(droneCarWaitsFor);
              if (scheduleOfDroneCarWaitsFor.getReadyForStepNumber() >= scheduleOfDroneCarWaitsFor.getSteps().size()
                || scheduleOfDroneCarWaitsFor
                  .getSteps()
                  .get(scheduleOfDroneCarWaitsFor.getReadyForStepNumber())
                  .getCarToRideOn() == null
                || scheduleOfDroneCarWaitsFor
                  .getSteps()
                  .get(scheduleOfDroneCarWaitsFor.getReadyForStepNumber())
                  .getCarToRideOn()
                  .equals(carSchedule.getCar()) == false
                || scheduleOfDroneCarWaitsFor
                  .getSteps()
                  .get(scheduleOfDroneCarWaitsFor.getReadyForStepNumber())
                  .getEndPoint()
                  .equals(nextStep.getEndPoint()) == false
                || (scheduleOfDroneCarWaitsFor.getReadyForStepNumber() > 0
                  && carSchedule.getReadyForStepNumber() > 0
                  && scheduleOfDroneCarWaitsFor
                    .getSteps()
                    .get(scheduleOfDroneCarWaitsFor.getReadyForStepNumber() - 1)
                    .getEndPoint()
                    .equals(
                      carSchedule.getSteps().get(carSchedule.getReadyForStepNumber() - 1).getEndPoint()) == false)) {
                allDronesCarNeedsAreHere = false;
              }
            }
            
            if (allDronesCarNeedsAreHere == true) {
              final Position newPosition =
                carSchedule.getSteps().get(carSchedule.getReadyForStepNumber()).getEndPoint();
              if (newPosition.equals(this.getInstance().getInstanceParameters().getDepot())
                && carSchedule.getSteps().size() == carSchedule.getReadyForStepNumber() - 1)
                throw new RuntimeException(
                  "Car " + carSchedule.getCar().getID() + " is at the depot before finishing its tour.");
                  
              if (!newPosition.equals(this.getInstance().getInstanceParameters().getDepot())) {
                if (byCarsVisitedPositions.contains(newPosition)) {
                  throw new RuntimeException(
                    " Position "
                      + newPosition.toString()
                      + " is visited more than once by cars, amongst others by "
                      + carSchedule.getCar().getID()
                      + ".");
                }
                
                byCarsVisitedPositions.add(newPosition.deepCopy());
              }
              
              carSchedule.increaseReadyForStepNumberby1();
              
              for (Drone droneCarWaitsFor : nextStep.getDronesToTake()) {
                this.getDroneSchedule(droneCarWaitsFor).increaseReadyForStepNumberby1();
                this.getDroneSchedule(droneCarWaitsFor).setTime(0);
              }
              
              someoneMovedInOuterWhile = true;
            } else {
              someoneMovedInInnerWhile = false;
            }
          }
        }
      }
      
      for (DroneSchedule droneSchedule : this.getDroneSchedules()) {
        
        // droneStepsAlone
        while (droneSchedule.getReadyForStepNumber() < droneSchedule.getSteps().size()
          && droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber()).getCarToRideOn() == null) {
          
          final Position oldPosition =
            (droneSchedule.getReadyForStepNumber() == 0)
              ? this.getInstance().getInstanceParameters().getDepot()
              : droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber() - 1).getEndPoint();
          final Position newPosition =
            droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber()).getEndPoint();
            
          droneSchedule
            .setTime(droneSchedule.getTime() + this.getInstance().getDroneFlyingTime(oldPosition, newPosition));
            
          if (droneSchedule.getTime() > droneSchedule.getDrone().getReachOfDrone()) {
            throw new RuntimeException(
              "Drone "
                + droneSchedule.getDrone().getID()
                + " exceeds flying time before reaching "
                + oldPosition.toString()
                + ": flew "
                + +droneSchedule.getTime()
                + " , only allowed "
                + droneSchedule.getDrone().getReachOfDrone());
                
          }
          
          droneSchedule.increaseReadyForStepNumberby1();
          someoneMovedInOuterWhile = true;
        }
      }
    }
    
    // analyzing done, everyone drove as far as possible
    
    String errorMessage = "";
    for (CarSchedule carSchedule : this.getCarSchedules()) {
      if (carSchedule.getReadyForStepNumber() < carSchedule.getSteps().size()) {
        
        String message = "";
        String missingDrones = "";
        for (final Drone potentialMissingDrone : carSchedule
          .getSteps()
          .get(carSchedule.getReadyForStepNumber())
          .getDronesToTake()) {
          if (this.getDroneSchedule(potentialMissingDrone).getCorrespondingStep(
            carSchedule.getSteps().get(carSchedule.getReadyForStepNumber())) != this
              .getDroneSchedule(potentialMissingDrone)
              .getReadyForStepNumber())
            missingDrones = missingDrones + potentialMissingDrone.getID() + " ";
        }
        
        if (missingDrones.length() > 0)
          message = " for drones to take " + missingDrones.trim();
          
        final Position carPosition =
          (carSchedule.getReadyForStepNumber() > 0)
            ? carSchedule.getSteps().get(carSchedule.getReadyForStepNumber() - 1).getEndPoint()
            : this.getInstance().getInstanceParameters().getDepot();
            
        errorMessage =
          errorMessage
            + "\r\n   -  Car "
            + carSchedule.getCar().getID()
            + " still waiting"
            + message
            + " at "
            + carPosition.toString()
            + " to execute step ("
            + carSchedule.getReadyForStepNumber()
            + ").";
            
      }
    }
    
    for (DroneSchedule droneSchedule : this.getDroneSchedules()) {
      if (droneSchedule.getReadyForStepNumber() < droneSchedule.getSteps().size()) {
        
        String missingCar = "";
        if (droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber()).getCarToRideOn() != null)
          missingCar =
            "its ride on "
              + droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber()).getCarToRideOn().getID();
              
        final Position dronePosition =
          (droneSchedule.getReadyForStepNumber() > 0)
            ? droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber() - 1).getEndPoint()
            : this.getInstance().getInstanceParameters().getDepot();
            
        errorMessage =
          errorMessage
            + "\r\n   -  Drone "
            + droneSchedule.getDrone().getID()
            + " still waiting for "
            + missingCar
            + " at "
            + dronePosition.toString()
            + " to execute step ("
            + droneSchedule.getReadyForStepNumber()
            + ").";
            
      }
    }
    
    if (errorMessage.length() > 0)
      throw new RuntimeException(errorMessage);
      
    for (CarSchedule carSchedule : this.getCarSchedules())
      carSchedule.setReadyForStepNumber(0);
    for (DroneSchedule droneSchedule : this.getDroneSchedules())
      droneSchedule.setReadyForStepNumber(0);
      
    return true;
  }
  
  public boolean check() {
    this.checkVehicleScheduleBijection();
    
    this.checkIfEachBoxGetsDelivered();
    
    Collection<Position> possibleEndPoints = this.getInstance().getAllDestinations();
    possibleEndPoints.add(this.getInstance().getInstanceParameters().getDepot());
    
    for (CarSchedule carSchedule : this.getCarSchedules()) {
      
      carSchedule.checkSteps(
        possibleEndPoints,
        this.getInstance().getInstanceParameters().getDepot(),
        true,  // car loops forbidden
        true   // drone charging edge is mandatory
      );
    }
    
    for (DroneSchedule droneSchedule : this.getDroneSchedules()) {
      droneSchedule.checkSteps(
        possibleEndPoints,
        this.getInstance().getInstanceParameters().getDepot(),
        true,     // drone loops forbidden
        true,     // drone charging edge is mandatory
        this);
    }
    
    this.checkFlyingTimeAndDeadLocks();
    
    return true;
  }
  
  public double evaluate() {
    switch (this.getInstance().getInstanceParameters().getObjectiveFunction()) {
      case AVERAGE_DELIVERY_TIME:
        return new SolutionAnalysis(this).getAverageTimeToDeliverBox();
      case COMPLETION_TIME:
        return new SolutionAnalysis(this).getCompletionTime();
      default:
        throw new RuntimeException(
          "Objective Function " + this.getInstance().getInstanceParameters().getObjectiveFunction() + " unknown.");
    }
  }
  
}
