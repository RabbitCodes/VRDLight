package multiTSP;


import java.util.Collection;
import java.util.HashMap;

import model.instance.Box;
import model.instance.Car;
import model.instance.Position;

public class MultiTSPData {


  private final double[][] dist;
  private final double[] distFromDepot; 
  private final Position[] positions; 
  private final Car[] cars;  
  private final HashMap<Position, Collection<Box>> positionMultiplicities; 
  private final int numBoxes; 

  
  
  public MultiTSPData(
    double[][] dist,
    double[] distFromDepot,
    Position[] positions, 
    HashMap<Position, Collection<Box>> positionMultiplicities, 
    int numBoxes, 
    Car[] cars
    ) 
  {
    super();
    this.dist = dist;
    this.distFromDepot = distFromDepot;
    this.positions=positions; 
    this.positionMultiplicities = positionMultiplicities;
    this.numBoxes=numBoxes; 
    this.cars = cars;
  }

  public double getDistance(int from, int to){
      return dist[from][to];
  }

  public int getNumPositions(){
      return dist.length;
  }
  
  public double getDistFromDepot(int to)
  {
       return distFromDepot[to]; 
  }
  
  public Car[] getCars()
  {
      return cars; 
  }
  


  public HashMap<Position, Collection<Box>> getPositionMultiplicities() {
    return positionMultiplicities;
  }
  
  
  public Position[] getPositions() {
    return positions;
  }
  
  public int getNumBoxesAtPosition(int position)
  {
      return this.getPositionMultiplicities().get(this.getPositions()[position]).size(); 
  }
  
  
  public int getNumBoxes() {
    return numBoxes;
  }
  
  
  

  
}

