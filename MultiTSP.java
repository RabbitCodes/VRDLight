package multiTSP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jamesframework.core.problems.GenericProblem;
import org.jamesframework.core.problems.Problem;
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
import model.instance.Box;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.instance.ObjectiveFunction;
import model.instance.Position;
import model.solution.CarSchedule;
import model.solution.CarStep;
import model.solution.DroneSchedule;
import model.solution.Solution;

public class MultiTSP {
  
  final Instance instance;
  
  final LocalSearchParameters parameters;
  
  final Map<Car, Integer> carLoadSizes;
  
  public MultiTSP(Instance instance, LocalSearchParameters parameters) {
    super();
    this.instance = instance;
    this.parameters = parameters;
    this.carLoadSizes = this.initCarLoadSizes();
  }
  
  public Instance getInstance() {
    return instance;
  }
  
  public LocalSearchParameters getParameters() {
    return parameters;
  }
  
  public Map<Car, Integer> getCarLoadSizes() {
    return carLoadSizes;
  }
  
  private Map<Car, Integer> initCarLoadSizes() {
    final Map<Car, Integer> carLoadSizes = new HashMap<Car, Integer>();
    
    final int numBoxes = this.getInstance().getBoxes().size();
    final int fairShare = numBoxes / this.getInstance().getCars().size();
    int leftoverBoxes = numBoxes - this.getInstance().getCars().size() * fairShare;
    
    for (Car car : this.getInstance().getCars()) {
      carLoadSizes.put(car, fairShare + ((leftoverBoxes > 0) ? 1 : 0));
      leftoverBoxes = leftoverBoxes - 1;
      
    }
    
    return carLoadSizes;
  }
  
  public Solution solveMultiTSP()
  {
    System.out.print("MULTI TSP...");
    Collection<CarSchedule> unsortedCarSchedules = this.getCarSchedules(); 
    ArrayList<CarSchedule> carSchedules = new ArrayList<CarSchedule>(unsortedCarSchedules);
    Collections.sort(carSchedules, new Comparator<CarSchedule>(){

      @Override
      public int compare(CarSchedule o1, CarSchedule o2) {
        
        return o1.getCar().getID().compareTo(o2.getCar().getID()); //this sorting is just for clarity
      }});
    int carSchedule = 0; 
    
    Collection<DroneSchedule> droneSchedules = new HashSet<DroneSchedule>(); 
    for(Drone drone: this.getInstance().getDronesSortedByID()) //this sorting is just for clarity
    {                                   
          CarSchedule speedUpThisCarSchedule = carSchedules.get(carSchedule); 
          droneSchedules.add(speedUpThisCarSchedule.constructScheduleOfADroneRidingThisCar_alsoChangesCarSchedule(drone));                        
           
          carSchedule++; 
          if(carSchedule==carSchedules.size())
          {
                carSchedule = 0; 
          }                           
    } 
    
    Solution solution = new Solution(this.getInstance(), unsortedCarSchedules, droneSchedules); 
    solution.check();
    System.out.println(" Done. [" + solution.evaluate() + "]");
    
    return solution; 
  }
  
  public Collection<CarSchedule> getCarSchedules() {
    
    final HashMap<Position, Collection<Box>> positionMultiplicities = new HashMap<Position, Collection<Box>>();
    
    for (Box box : this.getInstance().getBoxes()) {
      listBox: {
        for (Position listedPosition : positionMultiplicities.keySet())
          if (listedPosition.equals(box.getDestination())) {
            positionMultiplicities.get(box.getDestination()).add(box);
            break listBox;
          }
          
        Collection<Box> boxAsCollection = new HashSet<Box>();
        boxAsCollection.add(box);
        positionMultiplicities.put(box.getDestination(), boxAsCollection);
      }
    }
    
    final ArrayList<Position> positionsSortedByAngle = new ArrayList<Position>(positionMultiplicities.keySet());
    positionsSortedByAngle.sort(new PositionComparatorByAngle());
    
    double[][] dist = new double[positionsSortedByAngle.size()][positionsSortedByAngle.size()];
    double[] distFromDepot = new double[positionsSortedByAngle.size()];
    for (int i = 0; i < positionsSortedByAngle.size(); i++) {
      distFromDepot[i] =
        this.getInstance().getCarDrivingTime(
          this.getInstance().getInstanceParameters().getDepot(),
          positionsSortedByAngle.get(i));
          
      for (int j = 0; j < positionsSortedByAngle.size(); j++)
        dist[i][j] = this.getInstance().getCarDrivingTime(positionsSortedByAngle.get(i), positionsSortedByAngle.get(j));
    }
    
    final Car[] somehowSortedCars = new Car[this.getInstance().getCars().size()];
    int carNumber = 0;
    for (Car car : this.getInstance().getCars()) {
      somehowSortedCars[carNumber] = car;
      carNumber++;
    }
    
    MultiTSPData data =
      new MultiTSPData(
        dist,
        distFromDepot,
        positionsSortedByAngle.toArray(new Position[positionsSortedByAngle.size()]),
        positionMultiplicities,
        this.getInstance().getBoxes().size(),
        somehowSortedCars);
        
    RandomSolutionGenerator<MultiTSPSolution, MultiTSPData> rsg = (random, dataToGenerate) -> {
      
      final List<Integer> sortedPositions = new ArrayList<Integer>();
      for (int position = 0; position < dataToGenerate.getNumPositions(); position++)
        sortedPositions.add(position);
      
      List<List<Integer>> randomSolution = new ArrayList<List<Integer>>();
      
      for (int car = 0; car < this.getInstance().getCars().size(); car++) {
        List<Integer> tour = new ArrayList<Integer>();
        int numBoxesOnThisTour = 0;
        
        while (sortedPositions.size() > 0
          && numBoxesOnThisTour + dataToGenerate.getNumBoxesAtPosition(sortedPositions.get(0)) < this
            .getCarLoadSizes()
            .get(dataToGenerate.getCars()[car])) {
          tour.add(sortedPositions.get(0));
          numBoxesOnThisTour = numBoxesOnThisTour + dataToGenerate.getNumBoxesAtPosition(sortedPositions.get(0));
          sortedPositions.remove(0);
        }
        
        if (sortedPositions.size() > 0) {
          tour.add(sortedPositions.get(0));
          numBoxesOnThisTour = numBoxesOnThisTour + dataToGenerate.getNumBoxesAtPosition(sortedPositions.get(0));
          sortedPositions.remove(0);
        }
        
        randomSolution.add(tour);
      }
      
      MultiTSPSolution solution = new MultiTSPSolution(randomSolution);
      
      return solution;
    };
    
    final GenericProblem<MultiTSPSolution, MultiTSPData> problem;
    
    if (this.getInstance().getInstanceParameters().getObjectiveFunction().equals(ObjectiveFunction.COMPLETION_TIME)) {
      MultiTSPObjectiveCT objCT = new MultiTSPObjectiveCT();
      problem = new GenericProblem<MultiTSPSolution, MultiTSPData>(data, objCT, rsg);
    } else {
      MultiTSPObjectiveADT objADT = new MultiTSPObjectiveADT();
      problem = new GenericProblem<MultiTSPSolution, MultiTSPData>(data, objADT, rsg);
    }
    
    final List<List<Integer>> finalSolution = this.executeMultiTSPdependingOnMethod(problem);
    
    Collection<CarSchedule> carSchedules = new HashSet<CarSchedule>();
    for (int car = 0; car < somehowSortedCars.length; car++) {
      List<Integer> tour = finalSolution.get(car);
      
      ArrayList<CarStep> steps = new ArrayList<CarStep>();
      for (Integer positionNumber : tour) {
        final Position position = positionsSortedByAngle.get(positionNumber);
        steps.add(new CarStep(somehowSortedCars[car], position, null, positionMultiplicities.get(position)));
      }
      
      steps.add(
        new CarStep(
          somehowSortedCars[car],
          this.getInstance().getInstanceParameters().getDepot(),
          null,
          new HashSet<Box>()));
          
      carSchedules.add(new CarSchedule(somehowSortedCars[car], steps));
    }
    
    return carSchedules;
  }
  
  private List<List<Integer>> executeMultiTSPdependingOnMethod(Problem<MultiTSPSolution> problem) {
    List<List<Integer>> finalSolution = new ArrayList<List<Integer>>();
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.RANDOM_DESCENT)) {
      LocalSearch<MultiTSPSolution> randomDescent = new RandomDescent<>(problem, new MultiTSPNeighbourhood());
      
      randomDescent.addStopCriterion(this.getParameters().getStopCriterion());
      randomDescent.start();
      randomDescent.dispose();
      
      finalSolution = randomDescent.getBestSolution().getPositionsVisitedByEachCar();
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.STEEPEST_DESCENT)) {
      LocalSearch<MultiTSPSolution> steepestDescent = new RandomDescent<>(problem, new MultiTSPNeighbourhood());
      
      steepestDescent.addStopCriterion(this.getParameters().getStopCriterion());
      steepestDescent.start();
      steepestDescent.dispose();
      
      finalSolution = steepestDescent.getBestSolution().getPositionsVisitedByEachCar();
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.TABU)) {
      TabuMemory<MultiTSPSolution> memory =
        new FullTabuMemory<MultiTSPSolution>(this.getParameters().getTabuMemorySize());
      LocalSearch<MultiTSPSolution> tabu = new TabuSearch<>(problem, new MultiTSPNeighbourhood(), memory);
      
      tabu.addStopCriterion(this.getParameters().getStopCriterion());
      tabu.start();
      tabu.dispose();
      
      finalSolution = tabu.getBestSolution().getPositionsVisitedByEachCar();
      
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.METROPOLIS)) {
      LocalSearch<MultiTSPSolution> metropolis =
        new MetropolisSearch<>(problem, new MultiTSPNeighbourhood(), this.getParameters().getMaxTemperature());
        
      metropolis.addStopCriterion(this.getParameters().getStopCriterion());
      metropolis.start();
      metropolis.dispose();
      
      finalSolution = metropolis.getBestSolution().getPositionsVisitedByEachCar();
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.PARALLEL_TEMPERING)) {
      
      LocalSearch<MultiTSPSolution> parallelTempering =
        new ParallelTempering<>(
          problem,
          new MultiTSPNeighbourhood(),
          this.getParameters().getNumReplicasForParallelTempering(),
          this.getParameters().getMinTemperature(),
          this.getParameters().getMaxTemperature());
      parallelTempering.addStopCriterion(this.getParameters().getStopCriterion());
      
      parallelTempering.start();
      parallelTempering.dispose();
      
      finalSolution = parallelTempering.getBestSolution().getPositionsVisitedByEachCar();
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.VAR_NEIGH)) {
      
      ArrayList<MultiTSPNeighbourhood> list = new ArrayList<MultiTSPNeighbourhood>();
      for (int neigh = 0; neigh < this.getParameters().getNumVariableNeighbourhoods(); neigh++)
        list.add(new MultiTSPNeighbourhood());
      LocalSearch<MultiTSPSolution> variableNeighbourhood = new VariableNeighbourhoodDescent<>(problem, list);
      variableNeighbourhood.addStopCriterion(this.getParameters().getStopCriterion());
      
      variableNeighbourhood.start();
      variableNeighbourhood.dispose();
      
      finalSolution = variableNeighbourhood.getBestSolution().getPositionsVisitedByEachCar();
    }
    
    if (this.getParameters().getJamesMethod().equals(JamesMethod.PIPED_LOCAL_SEARCH)) {
      
      ArrayList<LocalSearch<MultiTSPSolution>> listOfPipedSearches = new ArrayList<LocalSearch<MultiTSPSolution>>();
      for (LocalSearchParameters pipe : this.getParameters().getPipes()) {
        switch (pipe.getJamesMethod()) {
          case RANDOM_DESCENT: {
            LocalSearch<MultiTSPSolution> randomDescent = new RandomDescent<>(problem, new MultiTSPNeighbourhood());
            randomDescent.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(randomDescent);
            break;
          }
          case STEEPEST_DESCENT: {
            LocalSearch<MultiTSPSolution> steepestDescent = new SteepestDescent<>(problem, new MultiTSPNeighbourhood());
            steepestDescent.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(steepestDescent);
            break;
          }
          case TABU: {
            TabuMemory<MultiTSPSolution> memory = new FullTabuMemory<MultiTSPSolution>(pipe.getTabuMemorySize());
            LocalSearch<MultiTSPSolution> tabu = new TabuSearch<>(problem, new MultiTSPNeighbourhood(), memory);
            tabu.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(tabu);
            break;
          }
          case METROPOLIS: {
            LocalSearch<MultiTSPSolution> metropolis =
              new MetropolisSearch<>(problem, new MultiTSPNeighbourhood(), pipe.getMaxTemperature());
            metropolis.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(metropolis);
            break;
          }
          case PARALLEL_TEMPERING: {
            LocalSearch<MultiTSPSolution> parallelTempering =
              new ParallelTempering<>(
                problem,
                new MultiTSPNeighbourhood(),
                pipe.getNumReplicasForParallelTempering(),
                pipe.getMinTemperature(),
                pipe.getMaxTemperature());
            parallelTempering.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(parallelTempering);
            break;
          }
          case VAR_NEIGH: {
            ArrayList<MultiTSPNeighbourhood> listNeigh = new ArrayList<MultiTSPNeighbourhood>();
            for (int neigh = 0; neigh < pipe.getNumVariableNeighbourhoods(); neigh++)
              listNeigh.add(new MultiTSPNeighbourhood());
            LocalSearch<MultiTSPSolution> variableNeighbourhood =
              new VariableNeighbourhoodDescent<>(problem, listNeigh);
            variableNeighbourhood.addStopCriterion(pipe.getStopCriterion());
            listOfPipedSearches.add(variableNeighbourhood);
            break;
          }
          case PIPED_LOCAL_SEARCH: {
            System.out.println(
              "You really want a Piped Local Search in a Piped Local Search? That's stupid. I'll leave it out.");
            break;
          }
          default:
            break;
        }
        
      }
      LocalSearch<MultiTSPSolution> pipedLocalSearch = new PipedLocalSearch<>(problem, listOfPipedSearches);
      pipedLocalSearch.addStopCriterion(this.getParameters().getStopCriterion());
      
      pipedLocalSearch.start();
      pipedLocalSearch.dispose();
      
      finalSolution = pipedLocalSearch.getBestSolution().getPositionsVisitedByEachCar();
    }
    
    return finalSolution;
  }
  
}
