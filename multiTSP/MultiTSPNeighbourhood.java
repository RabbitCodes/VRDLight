package multiTSP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamesframework.core.search.neigh.Neighbourhood;

public class MultiTSPNeighbourhood implements Neighbourhood<MultiTSPSolution>{

 

    public MultiTSPMove getRandomMove(MultiTSPSolution solution, Random rnd) {
        
        int numPositions = solution.getNumberOfPositions(); 
        
        if(rnd.nextBoolean())
        {
            int i = rnd.nextInt(numPositions-1);
            int j = rnd.nextInt(numPositions);
    
            if(j==i)
              return new MultiTSPMove(i,i+1); 
            else if(j<i)
              return new MultiTSPMove(j,i);
            else 
              return new MultiTSPMove(i, j);
        }
        else
        {
            int fromCar; 
            do
            {
                fromCar = rnd.nextInt(solution.getPositionsVisitedByEachCar().size());
            }
            while(solution.getPositionsVisitedByEachCar().get(fromCar).size()==0); 
            
            int toCar = rnd.nextInt(solution.getPositionsVisitedByEachCar().size()); 
            

            int oldBoxNr = rnd.nextInt(solution.getPositionsVisitedByEachCar().get(fromCar).size()); 

            
            int newBoxNr; 
            if(fromCar == toCar)
            {
               newBoxNr = rnd.nextInt(solution.getPositionsVisitedByEachCar().get(toCar).size()); 
            }
            else
            {
               newBoxNr = rnd.nextInt(solution.getPositionsVisitedByEachCar().get(toCar).size()+1); 
            }
            
            return new MultiTSPMove(fromCar,oldBoxNr,toCar,newBoxNr); 
            
        }
    }

    public List<MultiTSPMove> getAllMoves(MultiTSPSolution solution) {

        int n = solution.getNumberOfPositions(); 
        List<MultiTSPMove> moves = new ArrayList<>();
        for(int i=0; i<n; i++)
        {
            for(int j=0; j<n; j++)
            {
                if(i != j)
                {
                  if (j<=i)                    
                    moves.add(new MultiTSPMove(j,i));
                  else 
                    moves.add(new MultiTSPMove(i,j)); 
                }
            }
        }
        
        for(int fromCar = 0; fromCar < solution.getPositionsVisitedByEachCar().size(); fromCar++)
          for(int toCar = 0; toCar < solution.getPositionsVisitedByEachCar().size(); toCar++)
            for(int oldBoxNr = 0; oldBoxNr < solution.getPositionsVisitedByEachCar().get(fromCar).size(); oldBoxNr++)
              for(int newBoxNr = 0; newBoxNr < solution.getPositionsVisitedByEachCar().get(toCar).size(); newBoxNr++)
                moves.add(new MultiTSPMove(fromCar,oldBoxNr,toCar,newBoxNr)); 
            
        
        return moves;
    }

}
