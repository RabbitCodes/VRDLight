package model.solution;

import model.instance.Box;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Position;

public class DroneStep {
  
  final Drone drone;
  
  Position endPoint;
  
  Car carToRideOn;                    // may be null
  
  Box deliveredBox;                   // may be null, definitely at last step
  
  public DroneStep(Drone drone, Position endPoint, Car carToRideOn, Box deliveredBox) {
    super();
    this.drone = drone;
    this.endPoint = endPoint;
    this.carToRideOn = carToRideOn;
    this.deliveredBox = deliveredBox;
    
  }
  
  public Drone getDrone() {
    return drone;
  }
  
  public Position getEndPoint() {
    return endPoint;
  }
  
  public Car getCarToRideOn() {
    return carToRideOn;
  }
  
  public Box getDeliveredBox() {
    return deliveredBox;
  }
  
  public void setEndPoint(Position endPoint) {
    this.endPoint = endPoint;
  }
  
  public void setCarToRideOn(Car carToRideOn) {
    this.carToRideOn = carToRideOn;
  }
  
  public void setDeliveredBox(Box deliveredBox) {
    this.deliveredBox = deliveredBox;
  }
  
  public DroneStep deepCopy() {
    return new DroneStep(
      this.getDrone(),
      new Position(this.getEndPoint().getX(), this.getEndPoint().getY()),
      this.getCarToRideOn(),
      this.getDeliveredBox());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DroneStep other = (DroneStep) obj;
    if (carToRideOn == null) {
      if (other.carToRideOn != null)
        return false;
    } else if (!carToRideOn.equals(other.carToRideOn))
      return false;
    if (deliveredBox == null) {
      if (other.deliveredBox != null)
        return false;
    } else if (!deliveredBox.equals(other.deliveredBox))
      return false;
    if (drone == null) {
      if (other.drone != null)
        return false;
    } else if (!drone.equals(other.drone))
      return false;
    if (endPoint == null) {
      if (other.endPoint != null)
        return false;
    } else if (!endPoint.equals(other.endPoint))
      return false;
    return true;
  }
  
  public String toString() {
    String returnMe = "Drone " + this.getDrone().getID();
    if (this.getCarToRideOn() != null)
      returnMe = returnMe + " rides on " + this.getCarToRideOn().getID() + " to ";
      
    if (this.getDeliveredBox() != null) {
      returnMe = returnMe + " delivers " + this.getDeliveredBox().getID() + " at ";
    }
    
    if (this.getCarToRideOn() == null && this.getDeliveredBox() == null) {
      returnMe = returnMe + " flies to ";
    }
    
    returnMe = returnMe + this.getEndPoint().toString();
    
    return returnMe.trim();
  }
  
}
