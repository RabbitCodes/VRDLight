package instanceGeneration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import model.instance.Box;
import model.instance.Position;

public class BoxesGenerator {
  
  final int numberOfBoxes;
  
  final int maxDepotDistanceOfBoxes;
  
  final boolean allowSameDestination;
  
  final boolean destinationsAsDoubles;
  
  final Position depot;
  
  public BoxesGenerator(
    int numberOfBoxes,
    int maxDepotDistanceOfBoxes,
    boolean allowSameDestination,
    boolean destinationAsDoubles,
    final Position depot) {
    super();
    this.numberOfBoxes = numberOfBoxes;
    this.maxDepotDistanceOfBoxes = maxDepotDistanceOfBoxes;
    this.allowSameDestination = allowSameDestination;
    this.destinationsAsDoubles = destinationAsDoubles;
    this.depot = depot;
  }
  
  public int getNumberOfBoxes() {
    return numberOfBoxes;
  }
  
  public int getMaxDepotDistanceOfBoxes() {
    return maxDepotDistanceOfBoxes;
  }
  
  public boolean isAllowSameDestination() {
    return allowSameDestination;
  }
  
  public boolean isDestinationAsDoubles() {
    return destinationsAsDoubles;
  }
  
  public Position getDepot() {
    return depot;
  }
  
  private String generateZeroesForName(int digits, int boxNumber) {
    if (digits == 2) {
      if (boxNumber < 10)
        return "0";
    }
    if (digits == 3) {
      if (boxNumber < 10)
        return "00";
      if (boxNumber < 100)
        return "0";
    }
    
    return "";
  }
  
  private int getDigits() {
    return (this.getNumberOfBoxes() <= 10) ? 1 : ((this.getNumberOfBoxes() <= 100) ? 2 : 3);
  }
  
  private Collection<Box> generateAllowSameDestination() {
    Random rand = new Random();
    
    Collection<Box> boxes = new HashSet<Box>();
    final int digits = this.getDigits();
    for (int box = 0; box < this.getNumberOfBoxes(); box++) {
      
      final String zeroes = this.generateZeroesForName(digits, box);
      Box newBox = null;
      if (this.isDestinationAsDoubles()) {
        do {
          newBox = new Box(
            "B" + zeroes + String.valueOf(box),
            
            rand.nextDouble() * (2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes(),
            rand.nextDouble() * (2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes());
        } while (newBox.getDestination().equals(this.getDepot()));
      } else {
        do {
          newBox = new Box(
            "B" + zeroes + String.valueOf(box),
            
            rand.nextInt(2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes(),
            rand.nextInt(2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes());
        } while (newBox.getDestination().equals(this.getDepot()));
      }
      
      boxes.add(newBox);
      
    }
    
    return boxes;
  }
  
  private Collection<Box> generateForbidSameDestination() {
    Random rand = new Random();
    
    if (Math.pow(this.getMaxDepotDistanceOfBoxes() * 2 + 1, 2) - 1 < this.getNumberOfBoxes()
      && !this.isDestinationAsDoubles())
      throw new RuntimeException(
        "The delivery area is too small to find destination for all the boxes, "
          + "if same destination is not allowed!");
          
    Collection<Box> boxes = new HashSet<Box>();
    final int digits = this.getDigits();
    
    Collection<Position> positions = new HashSet<Position>();
    for (int box = 0; box < this.getNumberOfBoxes(); box++) {
      Box newBox = null;
      final String zeroes = this.generateZeroesForName(digits, box);
      
      if (this.isDestinationAsDoubles()) {
        do {
          newBox = new Box(
            "B" + zeroes + String.valueOf(box),
            
            rand.nextDouble() * (2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes(),
            rand.nextDouble() * (2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes());
        } while (newBox.getDestination().equals(this.getDepot()) || positions.contains(newBox.getDestination()));
      } else {
        do {
          newBox = new Box(
            "B" + zeroes + String.valueOf(box),
            
            rand.nextInt(2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes(),
            rand.nextInt(2 * this.getMaxDepotDistanceOfBoxes() + 1) - this.getMaxDepotDistanceOfBoxes());
        } while (newBox.getDestination().equals(this.getDepot()) || positions.contains(newBox.getDestination()));
      }
      
      positions.add(newBox.getDestination());
      boxes.add(newBox);
    }
    
    return boxes;
  }
  
  public Collection<Box> generate() {
    if (this.isAllowSameDestination())
      return this.generateAllowSameDestination();
    else
      return this.generateForbidSameDestination();
  }
  
}
