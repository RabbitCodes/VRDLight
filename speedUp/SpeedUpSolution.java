package speedUp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import model.instance.Car;
import model.instance.Drone;
import model.instance.Instance;
import model.solution.CarSchedule;
import model.solution.CarStep;
import model.solution.DroneSchedule;
import model.solution.DroneStep;
import model.solution.Solution;

public class SpeedUpSolution extends org.jamesframework.core.problems.sol.Solution {
  
  Solution currentSolution;
  
  final SpeedUpData data;
  
  public SpeedUpSolution(Solution currentSolution, SpeedUpData data) {
    super();
    this.currentSolution = currentSolution;
    this.data = data;
  }
  
  public Instance getInstance() {
    return currentSolution.getInstance();
  }
  
  public Solution getCurrentSolution() {
    return currentSolution;
  }
  
  public void setCurrentSolution(Solution newSolution) {
    currentSolution = newSolution;
  }
  
  public SpeedUpData getData() {
    return data;
  }
  
  public Car getCar() {
    return this.getData().getCar();
  }
  
  public CarSchedule getCarSchedule() {
    return this.getCurrentSolution().getCarSchedule(this.getData().getCar());
  }
  
  public Drone getDrone() {
    return this.getData().getDrone();
  }
  
  public DroneSchedule getDroneSchedule() {
    return this.getCurrentSolution().getDroneSchedule(this.getData().getDrone());
  }
  
  public int getNumberOfCarStepsBefore() {
    return this.getData().getNumberOfCarStepsBefore();
  }
  
  public int getNumberOfCarStepsAfter() {
    return this.getData().getNumberOfCarStepsAfter();
  }
  
  public int getNumberOfDroneStepsBefore() {
    return this.getData().getNumberOfDroneStepsBefore();
  }
  
  public int getNumberOfDroneStepsAfter() {
    return this.getData().getNumberOfDroneStepsAfter();
  }
  
  @Override
  public SpeedUpSolution copy() {
    
    Collection<CarSchedule> newCarSchedules = new HashSet<CarSchedule>();
    for (CarSchedule everyCarSchedule : this.getCurrentSolution().getCarSchedules()) {
      
      ArrayList<CarStep> carStepsCopy = new ArrayList<CarStep>();
      for (CarStep carStep : everyCarSchedule.getSteps())
        carStepsCopy.add(carStep.deepCopy());
        
      CarSchedule newCarSchedule = new CarSchedule(
        everyCarSchedule.getCar(),
        
        carStepsCopy);
        
      newCarSchedules.add(newCarSchedule);
    }
    
    Collection<DroneSchedule> newDroneSchedules = new HashSet<DroneSchedule>();
    for (DroneSchedule everyDroneSchedule : this.getCurrentSolution().getDroneSchedules()) {
      
      ArrayList<DroneStep> droneStepsCopy = new ArrayList<DroneStep>();
      for (DroneStep droneStep : everyDroneSchedule.getSteps())
        droneStepsCopy.add(droneStep.deepCopy());
        
      DroneSchedule newDroneSchedule = new DroneSchedule(everyDroneSchedule.getDrone(), droneStepsCopy);
      
      newDroneSchedules.add(newDroneSchedule);
    }
    
    return new SpeedUpSolution(
      new Solution(this.getCurrentSolution().getInstance(), newCarSchedules, newDroneSchedules),
      this.getData());
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((currentSolution == null) ? 0 : currentSolution.hashCode());
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
      
    SpeedUpSolution other = (SpeedUpSolution) obj;
    if (currentSolution == null) {
      if (other.currentSolution != null)
        return false;
    } else if (!currentSolution.getInstance().equals(other.currentSolution.getInstance()))
      return false;
      
    if (currentSolution.getCarSchedules().size() != other.getCurrentSolution().getCarSchedules().size())
      return false;
      
    for (CarSchedule carSchedule : currentSolution.getCarSchedules()) {
      CarSchedule otherCarSchedule = other.getCurrentSolution().getCarSchedule(carSchedule.getCar());
      if (carSchedule == null || otherCarSchedule == null)
        return false;
        
      if (carSchedule.equalsStepDeepCopy(otherCarSchedule) == false)
        return false;
    }
    
    if (currentSolution.getDroneSchedules().size() != other.getCurrentSolution().getDroneSchedules().size())
      return false;
      
    for (DroneSchedule droneSchedule : currentSolution.getDroneSchedules()) {
      DroneSchedule otherDroneSchedule = other.getCurrentSolution().getDroneSchedule(droneSchedule.getDrone());
      if (droneSchedule == null || otherDroneSchedule == null)
        return false;
        
      if (droneSchedule.equalsStepDeepCopy(otherDroneSchedule) == false)
        return false;
    }
    
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (!data.equals(other.data))
      return false;
    return true;
  }
  
}
