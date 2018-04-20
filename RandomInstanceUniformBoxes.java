package instanceGeneration;

import java.util.Collection;
import java.util.HashSet;

import model.instance.Box;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.instance.InstanceParameters;
import model.instance.MovingTimeMetric;
import model.instance.ObjectiveFunction;
import model.instance.Position;



public class RandomInstanceUniformBoxes {


  final int numberOfCars; 
  final int numberOfBoxes;
  final int maxDepotDistanceOfBoxes; 
  final boolean allowSameDestinationOfBoxes; 
  final boolean destinationsAsDoubles; 
  final int numberOfDrones; 
  final ObjectiveFunction objectiveFunction; 


  


  
  
    public RandomInstanceUniformBoxes(
    int numberOfCars,
    int numberOfBoxes,
    int maxDepotDistanceOfBoxes,
    final boolean allowSameDestinationOfBoxes,
    final boolean destinationsAsDoubles, 
    int numberOfDrones,
    final ObjectiveFunction objectiveFunction)  {
    super();

    this.numberOfCars = numberOfCars;
    this.numberOfBoxes = numberOfBoxes;
    this.maxDepotDistanceOfBoxes = maxDepotDistanceOfBoxes;
    this.allowSameDestinationOfBoxes = allowSameDestinationOfBoxes;
    this.destinationsAsDoubles = destinationsAsDoubles; 
    this.numberOfDrones = numberOfDrones;
    this.objectiveFunction = objectiveFunction; 
  }




  
  public int getNumberOfCars() {
    return numberOfCars;
  }

  public int getNumberOfBoxes() {
    return numberOfBoxes;
  }
  
  public int getMaxDepotDistanceOfBoxes() {
    return maxDepotDistanceOfBoxes;
  }
  
  public boolean isAllowSameDestinationOfBoxes() {
    return allowSameDestinationOfBoxes;
  }
  
  public boolean isDestinationAsDoubles() {
    return destinationsAsDoubles;
  }
  
  public int getNumberOfDrones() {
    return numberOfDrones;
  }
  
  public boolean isDestinationsAsDoubles() {
    return destinationsAsDoubles;
  }
  
  public ObjectiveFunction getObjectiveFunction() {
    return objectiveFunction;
  }





    public Instance generate()
  {
      
      InstanceParameters instanceParameters = new InstanceParameters(new Position(0,0),MovingTimeMetric.MANHATTAN, MovingTimeMetric.EUCLIDEAN,1,1,
        this.getObjectiveFunction()
        ); 

      Collection<Car> cars = new HashSet<Car>(); 
      for(int car = 0; car<this.getNumberOfCars(); car++)
           cars.add(new Car("C"+String.valueOf(car))); 
       
      Collection<Box> boxes = new BoxesGenerator(this.getNumberOfBoxes(), 
        this.getMaxDepotDistanceOfBoxes(), this.isAllowSameDestinationOfBoxes(), this.isDestinationAsDoubles(), new Position(0,0)).generate(); 

      Collection<Drone> drones = new HashSet<Drone>(); 
      for(int drone=0; drone<this.getNumberOfDrones(); drone++)
      {
          drones.add(new Drone("D"+String.valueOf(drone), this.getMaxDepotDistanceOfBoxes()*10)); //drone reach is infinity
      }
       
      
      return new Instance(instanceParameters, cars, boxes, drones); 
  }

}
