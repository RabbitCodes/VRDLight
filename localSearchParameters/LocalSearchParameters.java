package localSearchParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.search.stopcriteria.StopCriterion;

public class LocalSearchParameters {
  
  final StopCriterion stopCriterion;
  
  final JamesMethod jamesMethod;
  
  final double minTemperature;
  
  final double maxTemperature;
  
  final int tabuMemorySize;
  
  final int numReplicasForParallelTempering;
  
  final int numVariableNeighbourhoods;
  
  final List<LocalSearchParameters> pipes;
  
  public LocalSearchParameters(
    StopCriterion stopCriterion,
    JamesMethod jamesMethod,
    double minTemperature,
    double maxTemperature,
    int tabuMemorySize,
    int numReplicasForParallelTempering,
    int numVariableNeighbourhoods,
    List<LocalSearchParameters> pipes) {
    super();
    this.stopCriterion = stopCriterion;
    this.jamesMethod = jamesMethod;
    this.minTemperature = minTemperature;
    this.maxTemperature = maxTemperature;
    this.tabuMemorySize = tabuMemorySize;
    this.numReplicasForParallelTempering = numReplicasForParallelTempering;
    this.numVariableNeighbourhoods = numVariableNeighbourhoods;
    if (pipes != null) {
      this.pipes = pipes;
    } else {
      this.pipes = new ArrayList<LocalSearchParameters>();
    }
  }
  
  public LocalSearchParameters(
    int timeLimitInMilliSec,
    JamesMethod jamesMethod,
    double minTemperature,
    double maxTemperature,
    int tabuMemorySize,
    int numReplicasForParallelTempering,
    int numVariableNeighbourhoods,
    List<LocalSearchParameters> pipes) {
    super();
    this.stopCriterion = new MaxRuntime(timeLimitInMilliSec, TimeUnit.MILLISECONDS);
    this.jamesMethod = jamesMethod;
    this.minTemperature = minTemperature;
    this.maxTemperature = maxTemperature;
    this.tabuMemorySize = tabuMemorySize;
    this.numReplicasForParallelTempering = numReplicasForParallelTempering;
    this.numVariableNeighbourhoods = numVariableNeighbourhoods;
    if (pipes != null) {
      this.pipes = pipes;
    } else {
      this.pipes = new ArrayList<LocalSearchParameters>();
    }
  }
  
  public StopCriterion getStopCriterion() {
    return stopCriterion;
  }
  
  public JamesMethod getJamesMethod() {
    return jamesMethod;
  }
  
  public double getMinTemperature() {
    return minTemperature;
  }
  
  public double getMaxTemperature() {
    return maxTemperature;
  }
  
  public int getTabuMemorySize() {
    return tabuMemorySize;
  }
  
  public int getNumReplicasForParallelTempering() {
    return numReplicasForParallelTempering;
  }
  
  public int getNumVariableNeighbourhoods() {
    return numVariableNeighbourhoods;
  }
  
  public List<LocalSearchParameters> getPipes() {
    return pipes;
  }
  
  @Override
  public String toString() {
    return "LocalSearchParameters ["
      + "JamesMethod="
      + this.getJamesMethod().name()
      + ", stopCriterion="
      + this.getStopCriterion().toString()
      + ", minTemperature="
      + this.getMinTemperature()
      + ", maxTemperature="
      + this.getMaxTemperature()
      + ", tabuMemorySize="
      + this.getTabuMemorySize()
      + ", numReplicasForParallelTempering="
      + this.getNumReplicasForParallelTempering()
      + ", numVariableNeighbourhoods="
      + this.getNumVariableNeighbourhoods()
      + ", pipes="
      + this.getPipes()
      + "]";
  }
  
}
