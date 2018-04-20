package model.instance;

public class Box {
  
  final String iD;
  
  final Position destination;
  
  public Box(String iD, Position destination) {
    super();
    this.iD = iD;
    
    this.destination = destination;
  }
  
  public Box(String iD, double destinationX, double destinationY) {
    super();
    this.iD = iD;
    
    this.destination = new Position(destinationX, destinationY);
  }
  
  public String getID() {
    return iD;
  }
  
  public Position getDestination() {
    return destination;
  }
  
  public String toString() {
    return "Box " + this.getID() + " [Destination " + this.getDestination().toString() + ")]";
  }
  
}
