package model.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Instance {
  
  final InstanceParameters instanceParameters;
  
  final Collection<Car> cars;
  
  final Collection<Box> boxes;
  
  final Collection<Drone> drones;
  
  final Map<Edge, Double> carTravellingTimes;
  
  final Map<Edge, Double> droneTravellingTimes;
  
  final double townSpreadNorth;
  
  final double townSpreadEast;
  
  final double townSpreadSouth;
  
  final double townSpreadWest;
  
  public Instance(
    InstanceParameters instanceParameters,
    Collection<Car> cars,
    Collection<Box> boxes,
    Collection<Drone> drones) {
    super();
    this.instanceParameters = instanceParameters;
    this.cars = cars;
    this.boxes = boxes;
    this.drones = drones;
    this.carTravellingTimes = this.initCarTravellingTimes();
    this.droneTravellingTimes = this.initDroneTravellingTimes();
    
    double maxNorth = 0;
    double maxSouth = 0;
    double maxEast = 0;
    double maxWest = 0;
    for (Box box : this.getBoxes()) {
      if (box.getDestination().getY() > maxNorth)
        maxNorth = box.getDestination().getY();
        
      if (box.getDestination().getY() < maxSouth)
        maxSouth = box.getDestination().getY();
        
      if (box.getDestination().getX() > maxEast)
        maxEast = box.getDestination().getX();
        
      if (box.getDestination().getX() < maxWest)
        maxWest = box.getDestination().getX();
    }
    
    if (this.getInstanceParameters().getDepot().getY() > maxNorth)
      maxNorth = this.getInstanceParameters().getDepot().getY();
      
    if (this.getInstanceParameters().getDepot().getY() < maxSouth)
      maxSouth = this.getInstanceParameters().getDepot().getY();
      
    if (this.getInstanceParameters().getDepot().getX() > maxEast)
      maxEast = this.getInstanceParameters().getDepot().getX();
      
    if (this.getInstanceParameters().getDepot().getX() < maxWest)
      maxWest = this.getInstanceParameters().getDepot().getX();
      
    this.townSpreadEast = maxEast;
    this.townSpreadNorth = maxNorth;
    this.townSpreadSouth = maxSouth;
    this.townSpreadWest = maxWest;
    
    this.check();
  }
  
  private double computeTravellingTime(
    Position position1,
    Position position2,
    MovingTimeMetric movingTimeMetric,
    double movingTimeSpeed) {
    if (movingTimeMetric.equals(MovingTimeMetric.EUCLIDEAN))
      return Math.pow(movingTimeSpeed, -1)
        * (Math
          .sqrt(Math.pow(position1.getX() - position2.getX(), 2) + Math.pow(position1.getY() - position2.getY(), 2)));
    else if (movingTimeMetric.equals(MovingTimeMetric.MANHATTAN))
      return Math.pow(movingTimeSpeed, -1)
        * (Math.abs(position1.getX() - position2.getX()) + Math.abs(position1.getY() - position2.getY()));
    else
      throw new RuntimeException(
        "Moving Time Metric of Type " + movingTimeMetric + " unknown, could not compute Travelling Time.");
  }
  
  private Map<Edge, Double> initCarTravellingTimes() {
    final Map<Edge, Double> carTravellingTimes = new HashMap<Edge, Double>();
    final Collection<Position> positionsOfInterest = new HashSet<Position>();
    positionsOfInterest.addAll(this.getAllDestinations());
    positionsOfInterest.add(this.getInstanceParameters().getDepot());
    
    for (Position pos1 : positionsOfInterest)
      for (Position pos2 : positionsOfInterest) {
        final Edge edge = new Edge(pos1, pos2);
        carTravellingTimes.put(
          edge,
          this.computeTravellingTime(
            pos1,
            pos2,
            this.getInstanceParameters().getDrivingTimeMetric(),
            this.getInstanceParameters().getDrivingTimeSpeed()));
            
      }
      
    return carTravellingTimes;
  }
  
  private Map<Edge, Double> initDroneTravellingTimes() {
    final Map<Edge, Double> droneTravellingTimes = new HashMap<Edge, Double>();
    final Collection<Position> positionsOfInterest = new HashSet<Position>();
    positionsOfInterest.addAll(this.getAllDestinations());
    positionsOfInterest.add(this.getInstanceParameters().getDepot());
    
    for (Position pos1 : positionsOfInterest)
      for (Position pos2 : positionsOfInterest) {
        final Edge edge = new Edge(pos1, pos2);
        droneTravellingTimes.put(
          edge,
          this.computeTravellingTime(
            pos1,
            pos2,
            this.getInstanceParameters().getFlyingTimeMetric(),
            this.getInstanceParameters().getFlyingTimeSpeed()));
      }
      
    return droneTravellingTimes;
    
  }
  
  public InstanceParameters getInstanceParameters() {
    return instanceParameters;
  }
  
  public Collection<Car> getCars() {
    return cars;
  }
  
  public Car getCarById(String iD) {
    for (Car car : this.getCars())
      if (car.getID().equals(iD))
        return car;
        
    return null;
  }
  
  public ArrayList<Car> getCarsSortedByID() {
    ArrayList<Car> cars = new ArrayList<Car>(this.getCars());
    Collections.sort(cars, new Comparator<Car>() {
      
      @Override
      public int compare(Car car1, Car car2) {
        return car1.getID().compareTo(car2.getID());
      }
      
    });
    
    return cars;
  }
  
  public Map<Edge, Double> getCarTravellingTimes() {
    return carTravellingTimes;
  }
  
  public Collection<Box> getBoxes() {
    return boxes;
  }
  
  public Box getBoxById(String iD) {
    for (Box box : this.getBoxes())
      if (box.getID().equals(iD))
        return box;
        
    return null;
  }
  
  public Collection<Box> getBoxesByPosition(Position position) {
    Collection<Box> boxes = new HashSet<Box>();
    for (Box box : this.getBoxes())
      if (box.getDestination().equals(position))
        boxes.add(box);
        
    return boxes;
  }
  
  public ArrayList<Box> getBoxesSortedByID() {
    ArrayList<Box> boxes = new ArrayList<Box>(this.getBoxes());
    Collections.sort(boxes, new Comparator<Box>() {
      
      @Override
      public int compare(Box box1, Box box2) {
        return box1.getID().compareTo(box2.getID());
      }
      
    });
    
    return boxes;
  }
  
  public Collection<Drone> getDrones() {
    return drones;
  }
  
  public ArrayList<Drone> getDronesSortedByID() {
    ArrayList<Drone> drones = new ArrayList<Drone>(this.getDrones());
    Collections.sort(drones, new Comparator<Drone>() {
      
      @Override
      public int compare(Drone drone1, Drone drone2) {
        return drone1.getID().compareTo(drone2.getID());
      }
      
    });
    
    return drones;
  }
  
  public Map<Edge, Double> getDroneTravellingTimes() {
    return droneTravellingTimes;
  }
  
  public double getTownSpreadNorth() {
    return townSpreadNorth;
  }
  
  public double getTownSpreadEast() {
    return townSpreadEast;
  }
  
  public double getTownSpreadSouth() {
    return townSpreadSouth;
  }
  
  public double getTownSpreadWest() {
    return townSpreadWest;
  }
  
  public double getWidestDroneReach() {
    double maxReach = 0;
    for (Drone drone : this.getDrones()) {
      if (drone.getReachOfDrone() > maxReach)
        maxReach = drone.getReachOfDrone();
    }
    
    return maxReach;
  }
  
  public Collection<Position> getAllDestinations() {
    Collection<Position> destinations = new HashSet<Position>();
    for (Box box : this.getBoxes())
      destinations.add(box.getDestination());
      
    return destinations;
  }
  
  public void printInstanceInConsole() {
    System.out.println("INFORMATION ABOUT THE INSTANCE!");
    
    // Instanzparameter
    System.out.println("Objective Function: " + this.getInstanceParameters().getObjectiveFunction());
    System.out.println("Depot: " + this.getInstanceParameters().getDepot().toString());
    System.out.println(
      "Driving Time Metric: "
        + String.valueOf(this.getInstanceParameters().getDrivingTimeMetric())
        + ", Driving Speed: "
        + String.valueOf(this.getInstanceParameters().getDrivingTimeSpeed()));
    System.out.println(
      "Flying Time Metric: "
        + String.valueOf(this.getInstanceParameters().getFlyingTimeMetric())
        + ", Flying Speed: "
        + String.valueOf(this.getInstanceParameters().getFlyingTimeSpeed()));
    System.out.println("");
        
    // Cars
    System.out.printf("%-20s %-20s \n", String.valueOf(this.getCars().size()) + " Cars:", "ID");
    for (Car car : this.getCarsSortedByID())
      System.out.printf("%20s %-20s\n", " ", car.getID());
    System.out.println("");
      
    // Drones
    System.out
      .printf("%-20s %-20s %-20s \n", String.valueOf(this.getDrones().size()) + " Drones:", "ID", "reachOfDrone");
    for (Drone drone : this.getDronesSortedByID())
      System.out.printf("%-20s %-20s %-20s  \n", " ", drone.getID(), drone.getReachOfDrone());
    System.out.println("");
      
    // Boxes
    System.out.printf("%-20s %-20s %-20s \n", String.valueOf(this.getBoxes().size()) + " Boxes:", "ID", "destination");
    for (Box box : this.getBoxesSortedByID())
      System.out.printf(
        "%-20s %-20s %-20s \n",
        " ",
        box.getID(),
        "(" + String.valueOf(box.getDestination().getX()) + "," + String.valueOf(box.getDestination().getY()) + ")");
        
    System.out.println("");
  }
  
  public boolean check() {
    // Parameters
    if (this.getInstanceParameters().getDrivingTimeMetric().equals(null)
      || this.getInstanceParameters().getDrivingTimeMetric().equals(null)) {
      throw new RuntimeException("Invalid Instance: Moving Time Metrics not well defined.");
    }
    
    if (this.getInstanceParameters().getDrivingTimeSpeed() <= 0
      || this.getInstanceParameters().getFlyingTimeSpeed() <= 0) {
      throw new RuntimeException("Invalid Instance: Moving Time Speeds shall not be 0 or smaller.");
    }
    
    // Cars
    if (this.getCars().size() == 0) {
      throw new RuntimeException("Invalid Instance: No cars.");
    }
    
    ArrayList<String> iDs = new ArrayList<String>();
    
    for (Car car : this.getCars()) {
      
      if (iDs.contains(car.getID())) {
        throw new RuntimeException("Invalid Instance: Cars need disjunct IDs");
      }
      iDs.add(car.getID());
    }
    
    // Boxes
    if (this.getBoxes().size() == 0) {
      throw new RuntimeException("Invalid Instance: No Boxes.");
    }
    
    if (this.getBoxes().size() < this.getCars().size()) {
      System.out.println("Instance Check. Please: There shall be more Boxes than Cars!");
    }
    
    for (Box box : this.getBoxes()) {
      
      if (iDs.contains(box.getID())) {
        
        throw new RuntimeException("Invalid Instance: Boxes need disjuncs IDs from all other IDs.");
      }
      
      if (box.getDestination().equals(this.getInstanceParameters().getDepot())) {
        throw new RuntimeException("Invalid Instance: No box should have the depot as destination.");
      }
      
      iDs.add(box.getID());
    }
    
    // Drones
    if (this.getDrones().size() == 0) {
      System.out.println("Instance check: Instance has no drones.");
    }
    
    for (Drone drone : this.getDrones()) {
      if (iDs.contains(drone.getID())) {
        
        throw new RuntimeException("Invalid Instance: Drones need disjuncs IDs from all other IDs.");
      }
      
      iDs.add(drone.getID());
    }
    
    return true;
  }
  
  public double getTravellingTime(
    Position position1,
    Position position2,
    MovingTimeMetric movingTimeMetric,
    double movingTimeSpeed) {
    try {
      if (movingTimeMetric.equals(this.getInstanceParameters().getDrivingTimeMetric())
        && movingTimeSpeed == this.getInstanceParameters().getDrivingTimeSpeed())
        return this.getCarDrivingTime(position1, position2);
      if (movingTimeMetric.equals(this.getInstanceParameters().getFlyingTimeMetric())
        && movingTimeSpeed == this.getInstanceParameters().getFlyingTimeSpeed())
        return this.getDroneFlyingTime(position1, position2);
    } catch (NullPointerException e) {}
    
    return this.computeTravellingTime(position1, position2, movingTimeMetric, movingTimeSpeed);
  }
  
  public double getCarDrivingTime(Position position1, Position position2) {
    try {
      return this.getCarTravellingTimes().get(new Edge(position1, position2));
    } catch (NullPointerException e) {}
    
    return this.computeTravellingTime(
      position1,
      position2,
      this.getInstanceParameters().getDrivingTimeMetric(),
      this.getInstanceParameters().getDrivingTimeSpeed());
      
  }
  
  public double getDroneFlyingTime(Position position1, Position position2) {
    try {
      return this.getDroneTravellingTimes().get(new Edge(position1, position2));
    } catch (NullPointerException e) {}
    
    return this.computeTravellingTime(
      position1,
      position2,
      this.getInstanceParameters().getFlyingTimeMetric(),
      this.getInstanceParameters().getFlyingTimeSpeed());
      
  }
  
}
