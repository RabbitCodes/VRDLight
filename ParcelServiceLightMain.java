
import instanceGeneration.RandomInstanceUniformBoxes;
import localSearchParameters.JamesMethod;
import localSearchParameters.LocalSearchParameters;
import model.instance.Instance;
import model.instance.ObjectiveFunction;
import model.solution.Solution;
import multiTSP.MultiTSP;
import speedUp.IteratedSpeedUp;

public class ParcelServiceLightMain {
  
  public static void main(String[] args) {
    
    final Instance instance = new RandomInstanceUniformBoxes(
      2,   // numberOfCars,
      200,   // numberOfBoxes,
      200,   // maxDepotDistanceOfBoxes,
      true,   // allowSameDestinationOfBoxes,
      true,   // destinationsAsDoubles,
      2,   // numberOfDrones,
      ObjectiveFunction.AVERAGE_DELIVERY_TIME // ObjectiveFunction
      
    ).generate();
    
    instance.check();
    instance.printInstanceInConsole();
    
    final LocalSearchParameters parametersMultiTSP =
      new LocalSearchParameters(600000, JamesMethod.PARALLEL_TEMPERING, 1e-4, 1.4, -1, 3, -1, null);
    final LocalSearchParameters parametersSpeedUp =
      new LocalSearchParameters(60000, JamesMethod.PARALLEL_TEMPERING, 1e-3, 1, -1, 3, -1, null);
      
    final Solution tsp = new MultiTSP(instance, parametersMultiTSP).solveMultiTSP();
    final Solution speedUp = new IteratedSpeedUp(instance, parametersSpeedUp, tsp).compute();
    
    speedUp.printSolutionInConsole();
    
  }
  
}
