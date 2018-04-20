package model.solution;

import java.util.ArrayList;

import model.instance.Car;
import model.instance.Drone;
import model.instance.Position;

public class SolutionAnalysis {
  
  final Solution solution;
  
  final int numberOfDeliveredBoxes;
  
  final double averageTimeToDeliverBox;
  
  final double timeOfLastBoxToDeliver;
  
  final double completionTime;
  
  public SolutionAnalysis(Solution solution) {
    super();
    this.solution = solution;
    
    for (CarSchedule carSchedule : this.getSolution().getCarSchedules()) {
      carSchedule.setReadyForStepNumber(0);
      carSchedule.setTime(0);
    }
    
    for (DroneSchedule droneSchedule : this.getSolution().getDroneSchedules()) {
      droneSchedule.setReadyForStepNumber(0);
      droneSchedule.setTime(0);
    }
    
    boolean someoneMoved = true;
    double totalDeliverTimeForAllBoxes = 0;
    double timeOfLastBoxToDeliver = 0;
    int numberOfDeliveredBoxes = 0;
    
    while (someoneMoved == true) {
      
      someoneMoved = false;
      for (final DroneSchedule droneSchedule : solution.getDroneSchedules()) {
        if (droneSchedule.getReadyForStepNumber() < droneSchedule.getSteps().size()) {
          
          final DroneStep nextDroneStep = droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber());
          final Position dronePosition =
            (droneSchedule.getReadyForStepNumber() > 0)
              ? droneSchedule.getSteps().get(droneSchedule.getReadyForStepNumber() - 1).getEndPoint()
              : this.getSolution().getInstance().getInstanceParameters().getDepot();
              
          if (nextDroneStep.getCarToRideOn() == null) {
            // FLYING
            droneSchedule.setTime(
              droneSchedule.getTime()
                + this.getSolution().getInstance().getDroneFlyingTime(dronePosition, nextDroneStep.getEndPoint()));
            if (nextDroneStep.getDeliveredBox() != null) {
              totalDeliverTimeForAllBoxes = totalDeliverTimeForAllBoxes + droneSchedule.getTime();
              numberOfDeliveredBoxes++;
              if (droneSchedule.getTime() > timeOfLastBoxToDeliver)
                timeOfLastBoxToDeliver = droneSchedule.getTime();
                
            }
            
            droneSchedule.increaseReadyForStepNumberby1();
            someoneMoved = true;
            
          } else {
            // RIDING ON CAR: see below
          }
          
        }
        
      }
      
      for (CarSchedule carSchedule : this.getSolution().getCarSchedules()) {
        if (carSchedule.getReadyForStepNumber() < carSchedule.getSteps().size()) {
          
          final CarStep nextCarStep = carSchedule.getSteps().get(carSchedule.getReadyForStepNumber());
          final Position carPosition =
            (carSchedule.getReadyForStepNumber() > 0)
              ? carSchedule.getSteps().get(carSchedule.getReadyForStepNumber() - 1).getEndPoint()
              : solution.getInstance().getInstanceParameters().getDepot();
              
          if (nextCarStep.getDronesToTake().isEmpty()) {
            
            // RIDING ALONE
            carSchedule.setTime(
              carSchedule.getTime()
                + this.getSolution().getInstance().getCarDrivingTime(carPosition, nextCarStep.getEndPoint()));
                
            if (nextCarStep.getDeliveredBoxes() != null && nextCarStep.getDeliveredBoxes().size() > 0) {
              totalDeliverTimeForAllBoxes =
                totalDeliverTimeForAllBoxes + carSchedule.getTime() * nextCarStep.getDeliveredBoxes().size();
              numberOfDeliveredBoxes = numberOfDeliveredBoxes + nextCarStep.getDeliveredBoxes().size();
              if (carSchedule.getTime() > timeOfLastBoxToDeliver)
                timeOfLastBoxToDeliver = carSchedule.getTime();
                
            }
            carSchedule.increaseReadyForStepNumberby1();
            
            someoneMoved = true;
          }
          
          else {
            // DRONES RIDING ON CAR
            boolean allDronesAreHere = true;
            for (Drone droneOnCar : nextCarStep.getDronesToTake()) {
              DroneSchedule scheduleOfDroneOnCar = solution.getDroneSchedule(droneOnCar);
              
              if (scheduleOfDroneOnCar.getReadyForStepNumber() >= scheduleOfDroneOnCar.getSteps().size()
                
                || scheduleOfDroneOnCar
                  .getSteps()
                  .get(scheduleOfDroneOnCar.getReadyForStepNumber())
                  .getCarToRideOn() == null
                || scheduleOfDroneOnCar
                  .getSteps()
                  .get(scheduleOfDroneOnCar.getReadyForStepNumber())
                  .getCarToRideOn()
                  .equals(carSchedule.getCar()) == false
                || scheduleOfDroneOnCar
                  .getSteps()
                  .get(scheduleOfDroneOnCar.getReadyForStepNumber())
                  .getEndPoint()
                  .equals(nextCarStep.getEndPoint()) == false) {
                allDronesAreHere = false;
              }
            }
            
            if (allDronesAreHere == true) {
              
              double timeToDepart = carSchedule.getTime();
              for (Drone droneOnCar : nextCarStep.getDronesToTake()) {
                if (this.getSolution().getDroneSchedule(droneOnCar).getTime() > timeToDepart) {
                  timeToDepart = this.getSolution().getDroneSchedule(droneOnCar).getTime();
                }
              }
              
              carSchedule.setTime(
                timeToDepart
                  + this.getSolution().getInstance().getCarDrivingTime(carPosition, nextCarStep.getEndPoint()));
                  
              if (nextCarStep.getDeliveredBoxes() != null && nextCarStep.getDeliveredBoxes().size() > 0) {
                totalDeliverTimeForAllBoxes =
                  totalDeliverTimeForAllBoxes + carSchedule.getTime() * nextCarStep.getDeliveredBoxes().size();
                numberOfDeliveredBoxes = numberOfDeliveredBoxes + nextCarStep.getDeliveredBoxes().size();
                if (carSchedule.getTime() > timeOfLastBoxToDeliver)
                  timeOfLastBoxToDeliver = carSchedule.getTime();
              }
              
              for (Drone droneOnCar : nextCarStep.getDronesToTake()) {
                DroneSchedule scheduleOfDroneOnCar = this.getSolution().getDroneSchedule(droneOnCar);
                
                scheduleOfDroneOnCar.setTime(carSchedule.getTime());
                scheduleOfDroneOnCar.increaseReadyForStepNumberby1();
              }
              
              carSchedule.increaseReadyForStepNumberby1();
              
              someoneMoved = true;
              
            }
          }
          
        }
        
      }
      
    }
    
    double completionTime = 0;
    for (CarSchedule carSchedule : this.getSolution().getCarSchedules()) {
      if (carSchedule.getTime() > completionTime) {
        completionTime = carSchedule.getTime();
      }
    }
    for (DroneSchedule droneSchedule : this.getSolution().getDroneSchedules()) {
      if (droneSchedule.getTime() > completionTime) {
        completionTime = droneSchedule.getTime();
      }
    }
    
    double averageTimeToDeliverBox = (double) totalDeliverTimeForAllBoxes / numberOfDeliveredBoxes;
    if (numberOfDeliveredBoxes < this.getSolution().getInstance().getBoxes().size()) {
      System.out.println(
        "Not all boxes got delivered, just "
          + numberOfDeliveredBoxes
          + " of "
          + this.getSolution().getInstance().getBoxes().size());
      averageTimeToDeliverBox = Double.MAX_VALUE;
    }
    
    this.numberOfDeliveredBoxes = numberOfDeliveredBoxes;
    this.averageTimeToDeliverBox = averageTimeToDeliverBox;
    this.timeOfLastBoxToDeliver = timeOfLastBoxToDeliver;
    this.completionTime = completionTime;
    
  }
  
  public Solution getSolution() {
    return solution;
  }
  
  public int getNumberOfDeliveredBoxes() {
    return numberOfDeliveredBoxes;
  }
  
  public double getAverageTimeToDeliverBox() {
    return averageTimeToDeliverBox;
  }
  
  public double getTimeOfLastBoxToDeliver() {
    return timeOfLastBoxToDeliver;
  }
  
  public double getCompletionTime() {
    return completionTime;
  }
  
  public double getMaxNettoDrivingTimeCar() {
    double maxNettoDrivingTimeCar = 0;
    for (Car car : this.getSolution().getInstance().getCars()) {
      double nettoDrivingTime = this.getNettoDrivingTime(car);
      if (nettoDrivingTime > maxNettoDrivingTimeCar)
        maxNettoDrivingTimeCar = nettoDrivingTime;
    }
    return maxNettoDrivingTimeCar;
  }
  
  public double getMaxNettoMovingTimeDrone() {
    double maxNettoMovingTimeDrone = 0;
    for (Drone drone : this.getSolution().getInstance().getDrones()) {
      double nettoMovingTime = this.getNettoMovingTime(drone);
      if (nettoMovingTime > maxNettoMovingTimeDrone)
        maxNettoMovingTimeDrone = nettoMovingTime;
    }
    return maxNettoMovingTimeDrone;
  }
  
  public double getMaxNettoFlyingTimeDrone() {
    double maxNettoFlyingTimeDrone = 0;
    for (Drone drone : this.getSolution().getInstance().getDrones()) {
      double nettoFlyingTime = this.getNettoFlyingTime(drone);
      if (nettoFlyingTime > maxNettoFlyingTimeDrone)
        maxNettoFlyingTimeDrone = nettoFlyingTime;
    }
    return maxNettoFlyingTimeDrone;
  }
  
  public double getNettoMovingTime(Drone drone) {
    final ArrayList<DroneStep> steps = this.getSolution().getDroneSchedule(drone).getSteps();
    double length = 0;
    Position currentPosition = this.getSolution().getInstance().getInstanceParameters().getDepot();
    
    for (DroneStep droneStep : steps) {
      if (droneStep.getCarToRideOn() == null) {
        length = length + this.getSolution().getInstance().getDroneFlyingTime(currentPosition, droneStep.getEndPoint());
      } else {
        length = length + this.getSolution().getInstance().getCarDrivingTime(currentPosition, droneStep.getEndPoint());
      }
      
      currentPosition = droneStep.getEndPoint();
    }
    
    return length;
  }
  
  public double getNettoFlyingTime(Drone drone) {
    final ArrayList<DroneStep> steps = this.getSolution().getDroneSchedule(drone).getSteps();
    double length = 0;
    Position currentPosition = this.getSolution().getInstance().getInstanceParameters().getDepot();
    
    for (DroneStep droneStep : steps) {
      if (droneStep.getCarToRideOn() == null) {
        length = length + this.getSolution().getInstance().getDroneFlyingTime(currentPosition, droneStep.getEndPoint());
      }
      
      currentPosition = droneStep.getEndPoint();
    }
    
    return length;
  }
  
  public double getNettoDrivingTime(Car car) {
    final ArrayList<CarStep> steps = this.getSolution().getCarSchedule(car).getSteps();
    double length = 0;
    Position currentPosition = this.getSolution().getInstance().getInstanceParameters().getDepot();
    
    for (CarStep carStep : steps) {
      length = length + this.getSolution().getInstance().getCarDrivingTime(currentPosition, carStep.getEndPoint());
      currentPosition = carStep.getEndPoint();
    }
    
    return length;
  }
  
}
