package multiTSP;

import org.jamesframework.core.search.neigh.Move;

public class MultiTSPMove implements Move<MultiTSPSolution> 
{
    
    private final boolean swap; 
    private final int swap1; 
    private final int swap2; 
    
    private final boolean relocate; 
    private final int fromCar; 
    private final int oldBoxNr; 
    private final int toCar; 
    private final int newBoxNr; 
    

    

    public MultiTSPMove(
      int swap1,
      int swap2) {
      super();
      this.swap = true;
      this.swap1 = swap1;
      this.swap2 = swap2;
      this.relocate = false;
      this.fromCar = -1;
      this.oldBoxNr = -1;
      this.toCar = -1;
      this.newBoxNr = -1;
    }
    
    public MultiTSPMove(
      int fromCar,
      int oldBoxNr,
      int toCar,
      int newBoxNr) {
      super();
      this.swap = false;
      this.swap1 = -1;
      this.swap2 = -1;
      this.relocate =true;
      this.fromCar = fromCar;
      this.oldBoxNr = oldBoxNr;
      this.toCar = toCar;
      this.newBoxNr = newBoxNr;
    }
    
    

    
      public boolean isSwap() {
        return swap;
      }



    
      public int getSwap1() {
        return swap1;
      }



    
      public int getSwap2() {
        return swap2;
      }



    
      public boolean isRelocate() {
        return relocate;
      }



    
      public int getFromCar() {
        return fromCar;
      }



    
      public int getOldBoxNr() {
        return oldBoxNr;
      }



    
      public int getToCar() {
        return toCar;
      }



    
      public int getNewBoxNr() {
        return newBoxNr;
      }



    @Override
    public void apply(MultiTSPSolution solution) {

      if(this.isSwap())
        solution.swapPositions(this.getSwap1(), this.getSwap2());
      else 
        solution.relocateBox(this.getFromCar(), this.getOldBoxNr(), this.getToCar(), this.getNewBoxNr()); 
      
//      if(solution.check()==false)
//      {
//        System.out.println(this.toString()); 
//        solution.printSolutionInConsole();
//        throw new RuntimeException("End"); 
//      }

    }

    @Override
    public void undo(MultiTSPSolution solution) {

      if(this.isSwap())
        solution.swapPositions(this.getSwap1(), this.getSwap2());
      else 
        solution.relocateBox(this.getToCar(), this.getNewBoxNr(), this.getFromCar(), this.getOldBoxNr()); 
    }

    @Override
    public String toString() {
      if(this.swap)
          return "MultiTSPMove: Swap "+swap1+" and "+swap2; 
      else 
          return "MultiTSPMove: Relocate Box Nr. "+oldBoxNr+" of car "+fromCar+" as Box Nr. "+newBoxNr+" on Car "+toCar; 
     
    }
    
    

}


