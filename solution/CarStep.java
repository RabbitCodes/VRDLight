package model.solution;

import java.util.Collection;
import java.util.HashSet;

import model.instance.Box;
import model.instance.Car;
import model.instance.Drone;
import model.instance.Position;

public class CarStep {
  
  final Car car;
  
  Position endPoint;
  
  final Collection<Drone> dronesToTake;
  
  final Collection<Box> deliveredBoxes;         // deliver these Boxes at
                                                // endPoint, possibly empty
                                                // (definitely empty at last
                                                // step)
  
  public CarStep(Car car, Position endPoint, Collection<Drone> dronesToTake, Collection<Box> deliveredBoxes) {
    super();
    this.car = car;
    this.endPoint = endPoint;
    
    if (dronesToTake == null) {
      this.dronesToTake = new HashSet<Drone>();
    } else {
      this.dronesToTake = dronesToTake;
    }
    
    if (deliveredBoxes == null) {
      this.deliveredBoxes = new HashSet<Box>();
    } else {
      this.deliveredBoxes = deliveredBoxes;
    }
  }
  
  public CarStep(Car car, Position endPoint, Collection<Drone> dronesToTake, Box deliveredBox) {
    super();
    this.car = car;
    this.endPoint = endPoint;
    if (dronesToTake == null) {
      this.dronesToTake = new HashSet<Drone>();
    } else {
      this.dronesToTake = dronesToTake;
    }
    this.deliveredBoxes = new HashSet<Box>();
    if (deliveredBox != null) {
      deliveredBoxes.add(deliveredBox);
    }
    
  }
  
  public Car getCar() {
    return car;
  }
  
  public Position getEndPoint() {
    return endPoint;
  }
  
  public Collection<Drone> getDronesToTake() {
    return dronesToTake;
  }
  
  public Collection<Box> getDeliveredBoxes() {
    return deliveredBoxes;
  }
  
  public void setEndPoint(Position endPoint) {
    this.endPoint = endPoint;
  }
  
  public CarStep deepCopy() {
    return new CarStep(
      this.getCar(),
      new Position(this.getEndPoint().getX(), this.getEndPoint().getY()),
      new HashSet<Drone>(this.getDronesToTake()),
      new HashSet<Box>(this.getDeliveredBoxes()));
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
      
    CarStep other = (CarStep) obj;
    if (car == null) {
      if (other.car != null)
        return false;
    } else if (!car.equals(other.car))
      return false;
      
    if (deliveredBoxes == null) {
      if (other.deliveredBoxes != null)
        return false;
    } else if (!deliveredBoxes.equals(other.deliveredBoxes))
      return false;
      
    if (dronesToTake == null) {
      if (other.dronesToTake != null)
        return false;
    } else if (!dronesToTake.equals(other.dronesToTake))
      return false;
      
    if (endPoint == null) {
      if (other.endPoint != null)
        return false;
    } else if (!endPoint.equals(other.endPoint))
      return false;
      
    return true;
  }
  
  public String toString() {
    String returnMe = "Car " + this.getCar().getID();
    if (this.getDronesToTake() != null && this.getDronesToTake().isEmpty() == false) {
      returnMe = returnMe + " carrying ";
      for (Drone drone : this.getDronesToTake())
        returnMe = returnMe + drone.getID() + " ";
    }
    
    if (this.getDeliveredBoxes() != null && this.getDeliveredBoxes().isEmpty() == false) {
      returnMe = returnMe.trim() + " delivers ";
      for (Box box : this.getDeliveredBoxes())
        returnMe = returnMe + box.getID() + " ";
      returnMe = returnMe + " at ";
      
    } else {
      returnMe = returnMe.trim() + " drives to ";
    }
    
    returnMe = returnMe + this.getEndPoint().toString();
    
    return returnMe.trim();
  }
  
}
