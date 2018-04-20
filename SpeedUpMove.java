package speedUp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.jamesframework.core.search.neigh.Move;

import model.instance.Box;
import model.instance.Drone;
import model.instance.Instance;
import model.instance.Position;
import model.solution.CarStep;
import model.solution.DroneSchedule;
import model.solution.DroneStep;
import model.solution.Solution;

public class SpeedUpMove implements Move<SpeedUpSolution> {
  
  final SpeedUpTypeOfMove speedUpTypeOfMove;
  
  final int firstCarStepIndex;
  
  final int firstDroneStepIndex;
  
  Solution savedSolution; // just necessary for the complicated moves 7 and 8
  
  int lastCarStepIndex;
  
  int droneDepartsAfterCarStepIndex;
  
  int droneOvertakesBoxAfterCarStepIndex;
  
  int droneLandsAfterCarStepIndex;
  
  AbandonablePositionChecker abandonablePositionChecker;
  
  BoxInsertion boxInsertion;
  
  public SpeedUpMove(int firstStepForCar, int firstStepForDrone, SpeedUpTypeOfMove speedUpTypeOfMove) {
    super();
    this.firstCarStepIndex = firstStepForCar;
    this.firstDroneStepIndex = firstStepForDrone;
    this.speedUpTypeOfMove = speedUpTypeOfMove;
  }
  
  public int getFirstCarStepIndex() {
    return firstCarStepIndex;
  }
  
  public int getFirstDroneStepIndex() {
    return firstDroneStepIndex;
  }
  
  public SpeedUpTypeOfMove getSpeedUpTypeOfMove() {
    return speedUpTypeOfMove;
  }
  
  public void setAbandonablePositionChecker(AbandonablePositionChecker checker) {
    this.abandonablePositionChecker = checker;
  }
  
  public void setSavedSolution(final Solution solution) {
    this.savedSolution = solution.deepCopy();
  }
  
  private Solution getSavedSolution() {
    return this.savedSolution;
  }
  
  public void setLastCarStepIndex(int lastCarStepIndex) {
    this.lastCarStepIndex = lastCarStepIndex;
  }
  
  private int getLastCarStepIndex() {
    return this.lastCarStepIndex;
  }
  
  private int getDroneDepartsAfterCarStepIndex() {
    return droneDepartsAfterCarStepIndex;
  }
  
  public void setDroneDepartsAfterCarStepIndex(int droneDepartsAfterCarStepIndex) {
    this.droneDepartsAfterCarStepIndex = droneDepartsAfterCarStepIndex;
  }
  
  private int getDroneOvertakesBoxAfterCarStepIndex() {
    return droneOvertakesBoxAfterCarStepIndex;
  }
  
  public void setDroneOvertakesBoxAfterCarStepIndex(int droneOvertakesBoxAfterCarStepIndex) {
    this.droneOvertakesBoxAfterCarStepIndex = droneOvertakesBoxAfterCarStepIndex;
  }
  
  private int getDroneLandsAfterCarStepIndex() {
    return droneLandsAfterCarStepIndex;
  }
  
  public void setBoxInsertion(BoxInsertion boxInsertion) {
    this.boxInsertion = boxInsertion;
  }
  
  private BoxInsertion getBoxInsertion() {
    return this.boxInsertion;
  }
  
  public void setDroneLandsAfterCarStepIndex(int droneLandsAfterCarStepIndex) {
    this.droneLandsAfterCarStepIndex = droneLandsAfterCarStepIndex;
  }
  
  public String toString() {
    return this.getSpeedUpTypeOfMove()
      + " with First Step for Car "
      + String.valueOf(this.getFirstCarStepIndex())
      + " and First Step For Drone "
      + String.valueOf(this.getFirstDroneStepIndex());
  }
  
  private void applyType1(SpeedUpSolution solution) {
    ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    Position middlePosition = carSteps.get(this.getFirstCarStepIndex()).getEndPoint().deepCopy();
    Position endPosition = carSteps.get(this.getFirstCarStepIndex() + 1).getEndPoint().deepCopy();
    Box boxToOvertake = carSteps.get(this.getFirstCarStepIndex()).getDeliveredBoxes().iterator().next();
    
    for (Drone otherDrone : carSteps.get(this.getFirstCarStepIndex()).getDronesToTake())
      if (!otherDrone.equals(solution.getDrone())) {
        solution.getCurrentSolution().getDroneSchedule(otherDrone).getSteps().remove(
          solution.getCurrentSolution().getDroneSchedule(otherDrone).getCorrespondingStep(
            carSteps.get(this.getFirstCarStepIndex())));
      }
      
    // Drone
    droneSteps.set(
      this.getFirstDroneStepIndex(),
      new DroneStep(solution.getDrone(), middlePosition.deepCopy(), null, boxToOvertake));
    droneSteps
      .set(this.getFirstDroneStepIndex() + 1, new DroneStep(solution.getDrone(), endPosition.deepCopy(), null, null));
      
    // Car
    carSteps.set(
      this.getFirstCarStepIndex(),
      new CarStep(
        solution.getCar(),
        endPosition.deepCopy(),
        new HashSet<Drone>(carSteps.get(this.getFirstCarStepIndex()).getDronesToTake()),
        new HashSet<Box>(carSteps.get(this.getFirstCarStepIndex() + 1).getDeliveredBoxes())));
    carSteps.remove(this.getFirstCarStepIndex() + 1);
    carSteps.get(this.getFirstCarStepIndex()).getDronesToTake().remove(solution.getDrone());
    
  }
  
  private void applyType2(SpeedUpSolution solution) {
    ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    
    Position middlePosition = droneSteps.get(this.getFirstDroneStepIndex()).getEndPoint().deepCopy();
    Position endPosition = droneSteps.get(this.getFirstDroneStepIndex() + 1).getEndPoint().deepCopy();
    
    Collection<Box> overtakingBoxAsSet = new HashSet<Box>();
    overtakingBoxAsSet.add(droneSteps.get(this.getFirstDroneStepIndex()).getDeliveredBox());
    
    // OtherDrones
    for (Drone otherDrone : carSteps.get(this.getFirstCarStepIndex()).getDronesToTake()) {
      DroneSchedule droneSchedule = solution.getCurrentSolution().getDroneSchedule(otherDrone);
      final int indexOfFirstStepForThisDrone = droneSchedule.getNumberOfStepLeadingTo(endPosition);
      
      droneSchedule.getSteps().add(
        indexOfFirstStepForThisDrone,
        new DroneStep(otherDrone, middlePosition.deepCopy(), solution.getCar(), null));
    }
    
    // Car
    carSteps.add(
      this.getFirstCarStepIndex(),
      new CarStep(
        solution.getCar(),
        middlePosition.deepCopy(),
        new HashSet<Drone>(carSteps.get(this.getFirstCarStepIndex()).getDronesToTake()),
        overtakingBoxAsSet));
        
    carSteps.get(this.getFirstCarStepIndex()).getDronesToTake().add(solution.getDrone());
    carSteps.get(this.getFirstCarStepIndex() + 1).getDronesToTake().add(solution.getDrone());
    
    // Drone
    droneSteps.set(this.getFirstDroneStepIndex(), new DroneStep(
      solution.getDrone(),
      middlePosition,  // once without deep copy
      solution.getCar(),
      null));
    droneSteps.set(this.getFirstDroneStepIndex() + 1, new DroneStep(
      solution.getDrone(),
      endPosition,  // once without deep copy
      solution.getCar(),
      null));
      
  }
  
  private void applyType3(SpeedUpSolution solution) {
    ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    final int firstCarStepIndexWhereDroneIsBack = this.getLastCarStepIndex();
    
    droneSteps.get(this.getFirstDroneStepIndex() + 1).setEndPoint(
      carSteps.get(firstCarStepIndexWhereDroneIsBack).getEndPoint().deepCopy());
    droneSteps.remove(this.getFirstDroneStepIndex() + 2);
    
    carSteps.get(firstCarStepIndexWhereDroneIsBack).getDronesToTake().remove(solution.getDrone());
    
  }
  
  private void applyType4(SpeedUpSolution solution) {
    ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    final int lastCarStepIndexWithoutDrone = this.getLastCarStepIndex();
    
    droneSteps.get(this.getFirstDroneStepIndex() + 1).setEndPoint(
      carSteps.get(lastCarStepIndexWithoutDrone - 1).getEndPoint().deepCopy());
      
    droneSteps.add(
      this.getFirstDroneStepIndex() + 2,
      new DroneStep(
        solution.getDrone(),
        carSteps.get(lastCarStepIndexWithoutDrone).getEndPoint().deepCopy(),
        solution.getCar(),
        null));
        
    carSteps.get(lastCarStepIndexWithoutDrone).getDronesToTake().add(solution.getDrone());
  }
  
  private void applyType5(SpeedUpSolution solution) {
    ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    
    droneSteps.get(this.getFirstDroneStepIndex()).setEndPoint(
      droneSteps.get(this.getFirstDroneStepIndex() + 1).getEndPoint().deepCopy());
    droneSteps.get(this.getFirstDroneStepIndex()).setDeliveredBox(
      droneSteps.get(this.getFirstDroneStepIndex() + 1).getDeliveredBox());
    droneSteps.get(this.getFirstDroneStepIndex()).setCarToRideOn(null);
    droneSteps.remove(this.getFirstDroneStepIndex() + 1);
    
    carSteps.get(this.getFirstCarStepIndex()).getDronesToTake().remove(solution.getDrone());
    
  }
  
  private void applyType6(SpeedUpSolution solution) {
    ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    
    droneSteps.add(
      this.getFirstDroneStepIndex(),
      new DroneStep(
        solution.getDrone(),
        carSteps.get(this.getFirstCarStepIndex()).getEndPoint().deepCopy(),
        solution.getCar(),
        null));
        
    carSteps.get(this.getFirstCarStepIndex()).getDronesToTake().add(solution.getDrone());
  }
  
  private void applyType7(SpeedUpSolution solution) {
    final ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    final ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    
    final Integer droneDepartsAfterDroneStepIndex;
    if (this.getDroneDepartsAfterCarStepIndex() == -1)
      droneDepartsAfterDroneStepIndex = -1; // right from depot
    else if (this.getDroneDepartsAfterCarStepIndex() == this.getFirstCarStepIndex() - 1)
      droneDepartsAfterDroneStepIndex = this.getFirstDroneStepIndex() - 1;
    else
      droneDepartsAfterDroneStepIndex =
        solution.getCurrentSolution().getDroneSchedule(solution.getDrone()).getCorrespondingStep(
          carSteps.get(this.getDroneDepartsAfterCarStepIndex()));
          
    final Position overtakingPosition =
      carSteps.get(this.getDroneOvertakesBoxAfterCarStepIndex()).getEndPoint().deepCopy();
    final Position landingPosition = carSteps.get(this.getDroneLandsAfterCarStepIndex()).getEndPoint().deepCopy();
    
    final Box boxToOvertake =
      carSteps.get(this.getDroneOvertakesBoxAfterCarStepIndex()).getDeliveredBoxes().iterator().next();
      
    // Other Drones on the car
    
    for (Drone otherDrone : carSteps.get(this.getDroneOvertakesBoxAfterCarStepIndex()).getDronesToTake())
      if (!otherDrone.equals(solution.getDrone())) {
        solution.getCurrentSolution().getDroneSchedule(otherDrone).getSteps().remove(
          solution.getCurrentSolution().getDroneSchedule(otherDrone).getCorrespondingStep(
            carSteps.get(this.getDroneOvertakesBoxAfterCarStepIndex())));
      }
      
    // Drone
    droneSteps.set(
      droneDepartsAfterDroneStepIndex + 1,
      new DroneStep(solution.getDrone(), overtakingPosition.deepCopy(), null, boxToOvertake));
    droneSteps.set(
      droneDepartsAfterDroneStepIndex + 2,
      new DroneStep(solution.getDrone(), landingPosition.deepCopy(), null, null));
      
    for (int deleteDroneStep = 0; deleteDroneStep < this.getDroneLandsAfterCarStepIndex()
      - this.getDroneDepartsAfterCarStepIndex()
      - 2; deleteDroneStep++)
      droneSteps.remove(droneDepartsAfterDroneStepIndex + 3);
      
    // Car
    for (int droneRemove = this.getDroneDepartsAfterCarStepIndex() + 1; droneRemove <= this
      .getDroneLandsAfterCarStepIndex(); droneRemove++)
      carSteps.get(droneRemove).getDronesToTake().remove(solution.getDrone());
      
    carSteps.remove(this.getDroneOvertakesBoxAfterCarStepIndex());
    
    return;
  }
  
  public void applyType8(SpeedUpSolution solution) {
    final ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
    final ArrayList<DroneStep> droneSteps = solution.getDroneSchedule().getSteps();
    
    // Change Drone Schedule, add Drone to CarSchedule
    droneSteps.remove(this.getFirstDroneStepIndex());
    droneSteps.remove(this.getFirstDroneStepIndex());
    
    for (int index = 0; index <= this.getLastCarStepIndex() - this.getFirstCarStepIndex(); index++) {
      carSteps.get(this.getFirstCarStepIndex() + index).getDronesToTake().add(solution.getDrone());
      droneSteps.add(
        this.getFirstDroneStepIndex() + index,
        new DroneStep(
          solution.getDrone(),
          carSteps.get(this.getFirstCarStepIndex() + index).getEndPoint().deepCopy(),
          solution.getCar(),
          null));
    }
    
    // Let car deliver the box
    this.getBoxInsertion().insert();
    
  }
  
  private void applyType9(SpeedUpSolution solution) {
    
    final CarStep firstCarStep = solution.getCarSchedule().getSteps().get(this.getFirstCarStepIndex());
    final DroneStep firstDroneStep = solution.getDroneSchedule().getSteps().get(this.getFirstDroneStepIndex());
    
    final Box boxOfDrone = firstDroneStep.getDeliveredBox();
    final Box boxOfCar = firstCarStep.getDeliveredBoxes().iterator().next();
    
    for (Drone otherDrone : firstCarStep.getDronesToTake()) {
      final int numberOfStepLeadingToOldPosition =
        solution.getCurrentSolution().getDroneSchedule(otherDrone).getNumberOfStepLeadingTo(boxOfCar.getDestination());
        
      solution
        .getCurrentSolution()
        .getDroneSchedule(otherDrone)
        .getSteps()
        .get(numberOfStepLeadingToOldPosition)
        .setEndPoint(boxOfDrone.getDestination().deepCopy());
    }
    
    firstCarStep.setEndPoint(boxOfDrone.getDestination().deepCopy());
    firstCarStep.getDeliveredBoxes().clear();
    firstCarStep.getDeliveredBoxes().add(boxOfDrone);
    
    firstDroneStep.setEndPoint(boxOfCar.getDestination().deepCopy());
    firstDroneStep.setDeliveredBox(boxOfCar);
    
  }
  
  public Position getClosestPositionByFlyingTo(
    final Position target,
    final Collection<Position> possibleClosestPositions,
    Instance instance) {
    Position closestPosition = null;
    double distanceToClosestPosition = Double.MAX_VALUE;
    for (final Position position : possibleClosestPositions) {
      final double flyingTime = instance.getDroneFlyingTime(position, target);
      if (flyingTime < distanceToClosestPosition) {
        closestPosition = position;
        distanceToClosestPosition = flyingTime;
      }
    }
    
    return closestPosition;
  }
  
  public void apply(SpeedUpSolution solution) {
    
    switch (this.getSpeedUpTypeOfMove()) {
      case TYPE_0:
        break;
      case TYPE_1:
        this.applyType1(solution);
        break;
      case TYPE_2:
        this.applyType2(solution);
        break;
      case TYPE_3:
        this.applyType3(solution);
        break;
      case TYPE_4:
        this.applyType4(solution);
        break;
      case TYPE_5:
        this.applyType5(solution);
        break;
      case TYPE_6:
        this.applyType6(solution);
        break;
      case TYPE_7:
        this.applyType7(solution);
        break;
      case TYPE_8:
        this.applyType8(solution);
        break;
      case TYPE_9:
        this.applyType9(solution);
        break;
      default:
        break;
    }
    
    try {
      solution.getCurrentSolution().check();
    } catch (RuntimeException e) {
      System.out.println(this.toString());
      // this.getSavedSolution().printSolutionInConsole();
      solution.getCurrentSolution().printSolutionInConsole();
      solution.getCurrentSolution().check();
    }
    
  }
  
  public void undo(SpeedUpSolution solution) {
    
    switch (this.getSpeedUpTypeOfMove()) {
      case TYPE_0:
        break;
        
      case TYPE_1:
        this.applyType2(solution);
        break;
        
      case TYPE_2:
        this.applyType1(solution);
        break;
        
      case TYPE_3:
        this.applyType4(solution);
        break;
        
      case TYPE_4:
        this.applyType3(solution);
        break;
        
      case TYPE_5:
        this.applyType6(solution);
        break;
        
      case TYPE_6:
        this.applyType5(solution);
        break;
      case TYPE_7:
        solution.setCurrentSolution(this.getSavedSolution());
        this.setAbandonablePositionChecker(
          new AbandonablePositionChecker(
            solution.getCurrentSolution(),
            solution.getCarSchedule(),
            this.getDroneOvertakesBoxAfterCarStepIndex()));
        break;
      case TYPE_8:
        solution.setCurrentSolution(this.getSavedSolution());
        this.setBoxInsertion(
          new BoxInsertion(
            solution.getCurrentSolution(),
            solution.getCarSchedule(),
            solution.getNumberOfCarStepsBefore(),
            solution.getNumberOfCarStepsAfter(),
            solution
              .getCurrentSolution()
              .getDroneSchedule(solution.getDrone())
              .getSteps()
              .get(this.getFirstDroneStepIndex())
              .getDeliveredBox()));
        break;
      case TYPE_9:
        this.applyType9(solution);
        
        break;
        
      default:
        break;
    }
    
    try {
      solution.getCurrentSolution().check();
    } catch (RuntimeException e) {
      System.out.println(this.toString());
      solution.getCurrentSolution().printSolutionInConsole();
      solution.getCurrentSolution().check();
    }
  }
  
}
