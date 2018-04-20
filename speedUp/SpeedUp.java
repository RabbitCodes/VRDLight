package speedUp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jamesframework.core.problems.GenericProblem;
import org.jamesframework.core.problems.sol.RandomSolutionGenerator;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.algo.MetropolisSearch;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.algo.PipedLocalSearch;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.algo.SteepestDescent;
import org.jamesframework.core.search.algo.tabu.FullTabuMemory;
import org.jamesframework.core.search.algo.tabu.TabuMemory;
import org.jamesframework.core.search.algo.tabu.TabuSearch;
import org.jamesframework.core.search.algo.vns.VariableNeighbourhoodDescent;

import localSearchParameters.JamesMethod;
import localSearchParameters.LocalSearchParameters;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.instance.ObjectiveFunction;
import model.solution.CarSchedule;
import model.solution.DroneSchedule;
import model.solution.Solution;

public class SpeedUp {
  
  final Solution startSolution;
  
  final Car car;
  
  final int numberOfCarStepsBefore;
  
  final int numberOfCarStepsAfter;
  
  final Drone drone;
  
  final int numberOfDroneStepsBefore;
  
  final int numberOfDroneStepsAfter;
  
  final Collection<SpeedUpTypeOfMove> activeTypesOfMoves;
  
  final LocalSearchParameters parameters;
  
  private static final boolean printMoveCounter = false;
  
  public SpeedUp(
    Solution startSolution,
    Car car,
    int numberOfCarStepsBefore,
    int numberOfCarStepsAfter,
    Drone drone,
    int numberOfDroneStepsBefore,
    int numberOfDroneStepsAfter,
    LocalSearchParameters speedUpParameters) {
    super();
    this.startSolution = startSolution;
    this.car = car;
    this.numberOfCarStepsBefore = numberOfCarStepsBefore;
    this.numberOfCarStepsAfter = numberOfCarStepsAfter;
    this.drone = drone;
    this.numberOfDroneStepsBefore = numberOfDroneStepsBefore;
    this.numberOfDroneStepsAfter = numberOfDroneStepsAfter;
    this.parameters = speedUpParameters;
    this.activeTypesOfMoves =
      new ArrayList<SpeedUpTypeOfMove>(Arrays.asList(SpeedUpTypeOfMove.class.getEnumConstants()));
    this.checkInput();
  }
  
  public Solution getStartSolution() {
    return startSolution;
  }
  
  public Instance getInstance() {
    return startSolution.getInstance();
  }
  
  public Car getCar() {
    return car;
  }
  
  public Drone getDrone() {
    return drone;
  }
  
  public CarSchedule getStartCarSchedule() {
    return this.getStartSolution().getCarSchedule(this.getCar());
  }
  
  public DroneSchedule getStartDroneSchedule() {
    return this.getStartSolution().getDroneSchedule(this.getDrone());
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
  
  public LocalSearchParameters getParameters() {
    return parameters;
  }
  
  private Collection<SpeedUpTypeOfMove> getActiveTypesOfMoves() {
    return activeTypesOfMoves;
  }
  
  public void setActiveTypesOfMovesForRandomMoves_IfYouKnowWhatYouReDoing(int[] activeTypesOfMovesAsArray) {
    
    for (int move = 0; move < activeTypesOfMovesAsArray.length; move++)
      if (activeTypesOfMovesAsArray[move] <= 0 || activeTypesOfMovesAsArray[move] > 10)
        throw new RuntimeException(
          "In SpeedUp, move type "
            + activeTypesOfMovesAsArray[move]
            + " was set active, which is not a valid type of move");
            
    this.getActiveTypesOfMoves().clear();
    
    for (Integer move : activeTypesOfMovesAsArray)
      this.getActiveTypesOfMoves().add(SpeedUpTypeOfMove.values()[move]);
  }
  
  private boolean checkInput() {
    
    if (this.getNumberOfCarStepsBefore() < 0
      || this.getNumberOfCarStepsBefore() >= this.getStartCarSchedule().getSteps().size()
      || this.getNumberOfCarStepsAfter() < 0
      || this.getNumberOfCarStepsAfter() >= this.getStartCarSchedule().getSteps().size()
      || this.getNumberOfCarStepsBefore() + this.getNumberOfCarStepsAfter() >= this
        .getStartCarSchedule()
        .getSteps()
        .size()
      || this.getNumberOfDroneStepsBefore() < 0
      || this.getNumberOfDroneStepsBefore() >= this.getStartDroneSchedule().getSteps().size()
      || this.getNumberOfDroneStepsAfter() < 0
      || this.getNumberOfDroneStepsAfter() >= this.getStartDroneSchedule().getSteps().size()
      || this.getNumberOfDroneStepsBefore() + this.getNumberOfDroneStepsAfter() >= this
        .getStartDroneSchedule()
        .getSteps()
        .size()) {
      throw new RuntimeException("SpeedUp Input: Something is very wrong with first or lasts steps.");
    }
    
    if ((this.getNumberOfCarStepsBefore() == 0 && this.getNumberOfDroneStepsBefore() != 0)
      || (this.getNumberOfCarStepsBefore() != 0 && this.getNumberOfDroneStepsBefore() == 0)) {
      throw new RuntimeException("Speed Up Input: Just Car or Drone starts at the Depot, not both.");
    }
    
    else if (this.getNumberOfCarStepsBefore() > 0 && this.getNumberOfDroneStepsBefore() > 0) {
      
      if (this.getStartCarSchedule().getSteps().get(this.getNumberOfCarStepsBefore() - 1).getDronesToTake().contains(
        this.getStartDroneSchedule().getDrone()) == false
        || this.getStartDroneSchedule().getSteps().get(this.getNumberOfDroneStepsBefore() - 1).getCarToRideOn().equals(
          this.getStartCarSchedule().getCar()) == false) {
        throw new RuntimeException("Speed Up Input: Car and Drone didn't meet before beginning.");
        
      }
      
      if (this.getStartCarSchedule().getSteps().get(this.getNumberOfCarStepsBefore() - 1).getEndPoint().equals(
        this.getStartDroneSchedule().getSteps().get(this.getNumberOfDroneStepsBefore() - 1).getEndPoint()) == false) {
        throw new RuntimeException("Speed Up Input: Car and Drone don't start at the same Point.");
      }
    }
    
    if ((this.getNumberOfCarStepsAfter() == 0 && this.getNumberOfDroneStepsAfter() != 0)
      || (this.getNumberOfCarStepsAfter() != 0 && this.getNumberOfDroneStepsAfter() == 0)) {
      throw new RuntimeException("Speed Up Input: Either drone or car end at depot, but not both. ");
    }
    
    else if (this.getNumberOfCarStepsAfter() > 0 && this.getNumberOfDroneStepsAfter() > 0) {
      if (this
        .getStartCarSchedule()
        .getSteps()
        .get(this.getStartCarSchedule().getSteps().size() - this.getNumberOfCarStepsAfter())
        .getDronesToTake()
        .contains(this.getStartDroneSchedule().getDrone()) == false
        || this
          .getStartDroneSchedule()
          .getSteps()
          .get(this.getStartDroneSchedule().getSteps().size() - this.getNumberOfDroneStepsAfter())
          .getCarToRideOn()
          .equals(this.getStartCarSchedule().getCar()) == false) {
        throw new RuntimeException("Speed Up Input: Drone is supposed to ride on Car after SpeedUp.");
      }
    }
    
    for (int droneStep = this.getNumberOfDroneStepsBefore(); droneStep < this.getStartDroneSchedule().getSteps().size()
      - this.getNumberOfDroneStepsAfter(); droneStep++)
      if (this.getStartDroneSchedule().getSteps().get(droneStep).getCarToRideOn() != null
        && this.getStartDroneSchedule().getSteps().get(droneStep).getCarToRideOn() != this
          .getStartCarSchedule()
          .getCar()) {
        throw new RuntimeException("Speed Up Input: Drone rides another car in SpeedUp-Area.");
      }
      
    return true;
  }
  
  public void printInputInConsole() {
    System.out.println("SPEEDUP INPUT: ");
    System.out.println(
      this.getNumberOfCarStepsBefore()
        + " steps fixed before, "
        + this.getNumberOfCarStepsAfter()
        + " steps fixed after.");
    this.getStartCarSchedule().printCarScheduleInConsole();
    System.out.println(
      this.getNumberOfDroneStepsBefore()
        + " steps fixed before, "
        + this.getNumberOfDroneStepsBefore()
        + " steps fixed after.");
    this.getStartDroneSchedule().printDroneScheduleInConsole();
  }
  
  public Solution doLocalSearch() {
    
    SpeedUpData data =
      new SpeedUpData(
        this.getStartSolution(),
        this.getCar(),
        this.getNumberOfCarStepsBefore(),
        this.getNumberOfCarStepsAfter(),
        this.getDrone(),
        this.getNumberOfDroneStepsBefore(),
        this.getNumberOfDroneStepsAfter(),
        this.getActiveTypesOfMoves());
        
    RandomSolutionGenerator<SpeedUpSolution, SpeedUpData> randomSolutionGenerator =
      (generatorRandom, generatorData) -> {
        // This is not random!
        return new SpeedUpSolution(generatorData.getStartSolution().deepCopy(), generatorData);
      };
      
    GenericProblem<SpeedUpSolution, SpeedUpData> problem =
      new GenericProblem<SpeedUpSolution, SpeedUpData>(
        data,
        this.getInstance().getInstanceParameters().getObjectiveFunction().equals(ObjectiveFunction.COMPLETION_TIME)
          ? new SpeedUpObjectiveCT()
          : new SpeedUpObjectiveADT(),
        randomSolutionGenerator);
        
    SpeedUpSolution finalSolution = null;
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.RANDOM_DESCENT)) {
      
      LocalSearch<SpeedUpSolution> randomDescent = new RandomDescent<>(problem, new SpeedUpNeighbourhood());
      
      randomDescent.addStopCriterion(this.getParameters().getStopCriterion());
      
      randomDescent.start();
      randomDescent.dispose();
      
      finalSolution = randomDescent.getBestSolution();
      
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.STEEPEST_DESCENT)) {
      
      LocalSearch<SpeedUpSolution> steepestDescent = new SteepestDescent<>(problem, new SpeedUpNeighbourhood());
      
      steepestDescent.addStopCriterion(this.getParameters().getStopCriterion());
      
      steepestDescent.start();
      steepestDescent.dispose();
      
      finalSolution = steepestDescent.getBestSolution();
      
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.TABU)) {
      
      TabuMemory<SpeedUpSolution> memory =
        new FullTabuMemory<SpeedUpSolution>(this.getParameters().getTabuMemorySize());
      LocalSearch<SpeedUpSolution> tabu = new TabuSearch<SpeedUpSolution>(problem, new SpeedUpNeighbourhood(), memory);
      
      tabu.addStopCriterion(this.getParameters().getStopCriterion());
      
      tabu.start();
      tabu.dispose();
      
      finalSolution = tabu.getBestSolution();
      
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.METROPOLIS)) {
      
      LocalSearch<SpeedUpSolution> metropolis =
        new MetropolisSearch<>(problem, new SpeedUpNeighbourhood(), this.getParameters().getMaxTemperature());
        
      metropolis.addStopCriterion(this.getParameters().getStopCriterion());
      
      metropolis.start();
      metropolis.dispose();
      
      finalSolution = metropolis.getBestSolution();
      
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.PARALLEL_TEMPERING)) {
      LocalSearch<SpeedUpSolution> parallelTempering;
      
      parallelTempering =
        new ParallelTempering<>(
          problem,
          new SpeedUpNeighbourhood(),
          this.getParameters().getNumReplicasForParallelTempering(),
          this.getParameters().getMinTemperature(),
          this.getParameters().getMaxTemperature());
          
      parallelTempering.addStopCriterion(this.getParameters().getStopCriterion());
      
      parallelTempering.start();
      parallelTempering.dispose();
      
      finalSolution = parallelTempering.getBestSolution();
      
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.VAR_NEIGH)) {
      ArrayList<SpeedUpNeighbourhood> list = new ArrayList<SpeedUpNeighbourhood>();
      for (int neigh = 0; neigh < this.getParameters().getNumVariableNeighbourhoods(); neigh++)
        list.add(new SpeedUpNeighbourhood());
      LocalSearch<SpeedUpSolution> variableNeighbourhood = new VariableNeighbourhoodDescent<>(problem, list);
      variableNeighbourhood.addStopCriterion(this.getParameters().getStopCriterion());
      
      variableNeighbourhood.start();
      variableNeighbourhood.dispose();
      
      finalSolution = variableNeighbourhood.getBestSolution();
      
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.PIPED_LOCAL_SEARCH)) {
      
      ArrayList<LocalSearch<SpeedUpSolution>> listOfPipedSearches = new ArrayList<LocalSearch<SpeedUpSolution>>();
      for (LocalSearchParameters pipe : this.getParameters().getPipes()) {
        switch (pipe.getJamesMethod()) {
          case RANDOM_DESCENT: {
            LocalSearch<SpeedUpSolution> randomDescent = new RandomDescent<>(problem, new SpeedUpNeighbourhood());
            randomDescent.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(randomDescent);
            break;
          }
          case STEEPEST_DESCENT: {
            LocalSearch<SpeedUpSolution> steepestDescent = new SteepestDescent<>(problem, new SpeedUpNeighbourhood());
            steepestDescent.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(steepestDescent);
            break;
          }
          case TABU: {
            TabuMemory<SpeedUpSolution> memory = new FullTabuMemory<SpeedUpSolution>(pipe.getTabuMemorySize());
            LocalSearch<SpeedUpSolution> tabu = new TabuSearch<>(problem, new SpeedUpNeighbourhood(), memory);
            tabu.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(tabu);
            break;
          }
          case METROPOLIS: {
            LocalSearch<SpeedUpSolution> metropolis =
              new MetropolisSearch<>(problem, new SpeedUpNeighbourhood(), pipe.getMaxTemperature());
            metropolis.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(metropolis);
            break;
          }
          case PARALLEL_TEMPERING: {
            LocalSearch<SpeedUpSolution> parallelTempering =
              new ParallelTempering<>(
                problem,
                new SpeedUpNeighbourhood(),
                pipe.getNumReplicasForParallelTempering(),
                pipe.getMinTemperature(),
                pipe.getMaxTemperature());
            parallelTempering.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(parallelTempering);
            break;
          }
          case VAR_NEIGH: {
            ArrayList<SpeedUpNeighbourhood> listNeigh = new ArrayList<SpeedUpNeighbourhood>();
            for (int neigh = 0; neigh < pipe.getNumVariableNeighbourhoods(); neigh++)
              listNeigh.add(new SpeedUpNeighbourhood());
            LocalSearch<SpeedUpSolution> variableNeighbourhood = new VariableNeighbourhoodDescent<>(problem, listNeigh);
            variableNeighbourhood.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(variableNeighbourhood);
            break;
          }
          case PIPED_LOCAL_SEARCH: {
            throw new RuntimeException("You really want a Piped Local Search in a Piped Local Search? That's stupid.");
          }
          default:
            break;
        }
        
      }
      LocalSearch<SpeedUpSolution> pipedLocalSearch = new PipedLocalSearch<>(problem, listOfPipedSearches);
      pipedLocalSearch.addStopCriterion(this.getParameters().getStopCriterion());
      
      pipedLocalSearch.start();
      pipedLocalSearch.dispose();
      
      finalSolution = pipedLocalSearch.getBestSolution();
      
    }
    
    if (printMoveCounter)
      this.printMoveCounter(finalSolution.getData());
      
    Solution solution = finalSolution.getCurrentSolution();
    solution.check();
    
    return solution;
  }
  
  private void printMoveCounter(SpeedUpData data) {
    System.out.println("");
    System.out.println("Move counter:");
    for (SpeedUpTypeOfMove type : SpeedUpTypeOfMove.values())
      System.out.println(type.toString() + ": " + data.getMoveCount(type) + " tried");
  }
  
}
