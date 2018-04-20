package speedUp;

import java.util.Collection;
import java.util.HashSet;

import model.instance.Box;
import model.instance.Drone;
import model.instance.Position;
import model.solution.CarSchedule;
import model.solution.CarStep;
import model.solution.DroneSchedule;
import model.solution.DroneStep;
import model.solution.Solution;

public class BoxInsertion {
  
  final Solution solution;
  
  final CarSchedule carSchedule;
  
  final int fixedCarStepsBefore;
  
  final int fixedCarStepsAfter;
  
  final Box box;
  
  final int insertDuringStep;
  
  public BoxInsertion(
    Solution solution,
    CarSchedule carSchedule,
    int fixedCarStepsBefore,
    int fixedCarStepsAfter,
    Box box) {
    super();
    this.solution = solution;
    this.carSchedule = carSchedule;
    this.fixedCarStepsBefore = fixedCarStepsBefore;
    this.fixedCarStepsAfter = fixedCarStepsAfter;
    this.box = box;
    this.insertDuringStep = this.initInsertDuringStep();
  }
  
  private Solution getSolution() {
    return solution;
  }
  
  private CarSchedule getCarSchedule() {
    return carSchedule;
  }
  
  public Box getBox() {
    return box;
  }
  
  private Collection<Box> getBoxAsSet() {
    Collection<Box> boxAsSet = new HashSet<Box>();
    boxAsSet.add(this.getBox());
    return boxAsSet;
  }
  
  private int getInsertDuringStep() {
    return insertDuringStep;
  }
  
  private int getFixedCarStepsBefore() {
    return fixedCarStepsBefore;
  }
  
  private int getFixedCarStepsAfter() {
    return fixedCarStepsAfter;
  }
  
  private int initInsertDuringStep() {
    
    double bestAdditionalTimeForBoxDelivery = Double.MAX_VALUE;
    int currentBestStep = -1;
    Position startPosition =
      (this.getFixedCarStepsBefore() > 0)
        ? this.getCarSchedule().getSteps().get(this.getFixedCarStepsBefore() - 1).getEndPoint()
        : this.getSolution().getInstance().getInstanceParameters().getDepot();
        
    for (int carIndex = this.getFixedCarStepsBefore(); carIndex < this.getCarSchedule().getSteps().size()
      - this.getFixedCarStepsAfter(); carIndex++) {
      final double additionalTimeForBoxDelivery =
        this.getSolution().getInstance().getCarDrivingTime(startPosition, this.getBox().getDestination())
          + this.getSolution().getInstance().getCarDrivingTime(
            this.getBox().getDestination(),
            this.getCarSchedule().getSteps().get(carIndex).getEndPoint())
          - this.getSolution().getInstance().getCarDrivingTime(
            startPosition,
            this.getCarSchedule().getSteps().get(carIndex).getEndPoint());
            
      if (additionalTimeForBoxDelivery < bestAdditionalTimeForBoxDelivery) {
        currentBestStep = carIndex;
        bestAdditionalTimeForBoxDelivery = additionalTimeForBoxDelivery;
      }
    }
    
    return currentBestStep;
    
  }
  
  public void insert() {
    this.getCarSchedule().getSteps().add(
      this.getInsertDuringStep(),
      new CarStep(
        this.getCarSchedule().getCar(),
        this.getBox().getDestination().deepCopy(),
        new HashSet<Drone>(this.getCarSchedule().getSteps().get(this.getInsertDuringStep()).getDronesToTake()),
        this.getBoxAsSet()
        
    ));
    
    final Position carsNextPosition =
      this.getCarSchedule().getSteps().get(this.getInsertDuringStep() + 1).getEndPoint();
      
    for (Drone drone : this.getCarSchedule().getSteps().get(this.getInsertDuringStep()).getDronesToTake()) {
      final DroneSchedule droneSchedule = this.getSolution().getDroneSchedule(drone);
      final int droneStepLeadingToCarNextPositionTo = droneSchedule.getNumberOfStepLeadingTo(carsNextPosition);
      
      droneSchedule.getSteps().add(
        droneStepLeadingToCarNextPositionTo,
        new DroneStep(drone, this.getBox().getDestination().deepCopy(), this.getCarSchedule().getCar(), null));
    }
  }
  
}
