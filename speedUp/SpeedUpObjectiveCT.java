package speedUp;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;

public class SpeedUpObjectiveCT implements Objective<SpeedUpSolution, SpeedUpData> {
  
  public Evaluation evaluate(SpeedUpSolution solution, SpeedUpData data) {
    solution.getCurrentSolution().evaluate();
    
    return SimpleEvaluation
      .WITH_VALUE(Math.max(solution.getCarSchedule().getTime(), solution.getDroneSchedule().getTime()));
  }
  
  @Override
  public boolean isMinimizing() {
    return true;
  }
  
}
