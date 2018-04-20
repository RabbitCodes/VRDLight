package model.instance;

public class InstanceParameters {
  
  final Position depot;
  
  final MovingTimeMetric drivingTimeMetric;
  
  final MovingTimeMetric flyingTimeMetric;
  
  final double drivingTimeSpeed;
  
  final double flyingTimeSpeed;
  
  final ObjectiveFunction objectiveFunction;
  
  public InstanceParameters(
    final Position depot,
    MovingTimeMetric drivingTimeMetric,
    MovingTimeMetric flyingTimeMetric,
    double drivingTimeSpeed,
    double flyingTimeSpeed,
    final ObjectiveFunction objectiveFunction) {
    super();
    this.depot = depot;
    this.drivingTimeMetric = drivingTimeMetric;
    this.flyingTimeMetric = flyingTimeMetric;
    this.drivingTimeSpeed = drivingTimeSpeed;
    this.flyingTimeSpeed = flyingTimeSpeed;
    this.objectiveFunction = objectiveFunction;
  }
  
  public MovingTimeMetric getDrivingTimeMetric() {
    return drivingTimeMetric;
  }
  
  public MovingTimeMetric getFlyingTimeMetric() {
    return flyingTimeMetric;
  }
  
  public double getDrivingTimeSpeed() {
    return drivingTimeSpeed;
  }
  
  public double getFlyingTimeSpeed() {
    return flyingTimeSpeed;
  }
  
  public Position getDepot() {
    return depot;
  }
  
  public ObjectiveFunction getObjectiveFunction() {
    return objectiveFunction;
  }
  
}
