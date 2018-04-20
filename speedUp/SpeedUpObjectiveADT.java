package speedUp;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;

public class SpeedUpObjectiveADT implements Objective<SpeedUpSolution, SpeedUpData> {
  
  public Evaluation evaluate(SpeedUpSolution solution, SpeedUpData data) {
    return SimpleEvaluation.WITH_VALUE(solution.getCurrentSolution().evaluate());
  }
  
  public boolean isMinimizing() {
    return true;
  }
}
