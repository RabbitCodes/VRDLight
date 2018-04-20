package speedUp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.solution.Solution;

public class SpeedUpData {
  
  final Solution startSolution;
  
  final Car car;
  
  final int numberOfCarStepsBefore;
  
  final int numberOfCarStepsAfter;
  
  final Drone drone;
  
  final int numberOfDroneStepsBefore;
  
  final int numberOfDroneStepsAfter;
  
  final Collection<SpeedUpTypeOfMove> activeTypesOfMoves;
  
  public SpeedUpData(
    Solution startSolution,
    Car car,
    int numberOfCarStepsBefore,
    int numberOfCarStepsAfter,
    Drone drone,
    int numberOfDroneStepsBefore,
    int numberOfDroneStepsAfter,
    Collection<SpeedUpTypeOfMove> activeTypesOfMoves) {
    super();
    this.startSolution = startSolution;
    this.car = car;
    this.numberOfCarStepsBefore = numberOfCarStepsBefore;
    this.numberOfCarStepsAfter = numberOfCarStepsAfter;
    this.drone = drone;
    this.numberOfDroneStepsBefore = numberOfDroneStepsBefore;
    this.numberOfDroneStepsAfter = numberOfDroneStepsAfter;
    this.activeTypesOfMoves = activeTypesOfMoves;
    
  }
  
  public Instance getInstance() {
    return startSolution.getInstance();
  }
  
  public Solution getStartSolution() {
    return startSolution;
  }
  
  public Car getCar() {
    return car;
  }
  
  public Drone getDrone() {
    return drone;
  }
  
  public int getNumberOfCarStepsBefore() {
    return numberOfCarStepsBefore;
  }
  
  public int getNumberOfCarStepsAfter() {
    return numberOfCarStepsAfter;
  }
  
  public int getNumberOfDroneStepsBefore() {
    return numberOfDroneStepsBefore;
  }
  
  public int getNumberOfDroneStepsAfter() {
    return numberOfDroneStepsAfter;
  }
  
  public Collection<SpeedUpTypeOfMove> getActiveTypesOfMoves() {
    return activeTypesOfMoves;
  }
  
  private final Map<SpeedUpTypeOfMove, Integer> moveCounter = initMoveCounter();
  
  private Map<SpeedUpTypeOfMove, Integer> getMoveCounter() {
    return moveCounter;
  }
  
  private Map<SpeedUpTypeOfMove, Integer> initMoveCounter() {
    Map<SpeedUpTypeOfMove, Integer> counter = new HashMap<SpeedUpTypeOfMove, Integer>();
    for (SpeedUpTypeOfMove type : SpeedUpTypeOfMove.values())
      counter.put(type, 0);
    return counter;
  }
  
  public void increaseMoveCount(SpeedUpTypeOfMove move) {
    this.getMoveCounter().put(move, this.getMoveCounter().get(move).intValue() + 1);
  }
  
  public int getMoveCount(SpeedUpTypeOfMove move) {
    return this.getMoveCounter().get(move).intValue();
  }
  
}
