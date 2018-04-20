package speedUp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jamesframework.core.search.neigh.Neighbourhood;

import model.instance.Drone;
import model.instance.Position;
import model.solution.CarStep;
import model.solution.DroneStep;

public class SpeedUpNeighbourhood implements Neighbourhood<SpeedUpSolution> {
  
  private boolean canApplyType1(SpeedUpSolution solution, SpeedUpMove move) {
    if (
    
    this.isInSpeedUpArea(solution, move, 2, 2)
      
      && this.isTogetherBefore(solution, move)
      
      && this.isTogetherAfterwards(solution, move, 2, 2)) {
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final CarStep carStep1 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + 1);
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStep1 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      final Position startPosition =
        (move.getFirstCarStepIndex() > 0)
          ? solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() - 1).getEndPoint()
          : solution.getInstance().getInstanceParameters().getDepot();
      final AbandonablePositionChecker abandonablePositionChecker =
        new AbandonablePositionChecker(
          solution.getCurrentSolution(),
          solution.getCarSchedule(),
          move.getFirstCarStepIndex());
          
      if (
      
      carStep0.getEndPoint().equals(droneStep0.getEndPoint())
        && carStep1.getEndPoint().equals(droneStep1.getEndPoint())
        && carStep0.getDronesToTake().contains(solution.getDrone())
        && carStep1.getDronesToTake().contains(solution.getDrone())
        && droneStep0.getCarToRideOn().equals(solution.getCar())
        && droneStep1.getCarToRideOn().equals(solution.getCar())
        
        && carStep0.getDeliveredBoxes().size() == 1
        
        && abandonablePositionChecker.isAbandoningPossible()
        
        && this.isValidDroneFlyingTime(
          solution,
          startPosition,
          carStep0.getEndPoint(),
          carStep1.getEndPoint(),
          solution.getDrone())) {
        move.setAbandonablePositionChecker(abandonablePositionChecker);
        return true;
      }
    }
    
    return false;
  }
  
  private boolean canApplyType2(SpeedUpSolution solution, SpeedUpMove move) {
    
    if (
    
    this.isInSpeedUpArea(solution, move, 1, 2)
      
      && this.isTogetherBefore(solution, move)
      
      && this.isTogetherAfterwards(solution, move, 1, 2)) {
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStep1 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      
      if (!carStep0.getDronesToTake().contains(solution.getDrone())
        && droneStep0.getCarToRideOn() == null
        && droneStep1.getCarToRideOn() == null
        
        && carStep0.getEndPoint().equals(droneStep1.getEndPoint())
        
        && droneStep0.getDeliveredBox() != null)
        return true;
    }
    
    return false;
  }
  
  private boolean canApplyType3(SpeedUpSolution solution, SpeedUpMove move) {
    if (
    
    this.isInSpeedUpArea(solution, move, 1, 3) && this.isTogetherBefore(solution, move)) {
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStep1 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      final DroneStep droneStepLast = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 2);
      final Position startPosition =
        (move.getFirstCarStepIndex() > 0)
          ? solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() - 1).getEndPoint()
          : solution.getInstance().getInstanceParameters().getDepot();
      if (
      
      droneStep0.getCarToRideOn() == null
        && droneStep1.getCarToRideOn() == null
        
        && droneStepLast.getCarToRideOn() != null
        && droneStepLast.getCarToRideOn().equals(solution.getCar())) {
        
        if (!carStep0.getDronesToTake().contains(solution.getDrone())) {
          final int firstCarStepIndexWhereDroneIsBack =
            1
              + solution.getCarSchedule().getLastCarStepInARowWithoutDroneInteraction(
                move.getFirstCarStepIndex(),
                solution.getDrone());
          if (firstCarStepIndexWhereDroneIsBack < solution.getCarSchedule().getSteps().size()) {
            final CarStep carStepLast = solution.getCarSchedule().getSteps().get(firstCarStepIndexWhereDroneIsBack);
            
            if (this
              .isInSpeedUpArea(solution, move, firstCarStepIndexWhereDroneIsBack - move.getFirstCarStepIndex() + 1, 3)
              
              && this.isTogetherAfterwards(
                solution,
                move,
                firstCarStepIndexWhereDroneIsBack - move.getFirstCarStepIndex() + 1,
                3)
                
              && carStepLast.getDronesToTake().contains(solution.getDrone())
              && droneStepLast.getEndPoint().equals(carStepLast.getEndPoint())
              && this.isValidDroneFlyingTime(
                solution,
                startPosition,
                droneStep0.getEndPoint(),
                droneStepLast.getEndPoint(),
                solution.getDrone())) {
              move.setLastCarStepIndex(firstCarStepIndexWhereDroneIsBack);
              return true;
            }
          }
        }
        
      }
    }
    
    return false;
  }
  
  private boolean canApplyType4(SpeedUpSolution solution, SpeedUpMove move) {
    if (
    
    this.isInSpeedUpArea(solution, move, 1, 2) && this.isTogetherBefore(solution, move)) {
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStep1 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      
      if (
      
      !carStep0.getDronesToTake().contains(solution.getDrone())
        
        && droneStep0.getCarToRideOn() == null
        && droneStep1.getCarToRideOn() == null) {
        if (move.getFirstCarStepIndex() + 1 < solution.getCarSchedule().getSteps().size()
          && !solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + 1).getDronesToTake().contains(
            solution.getDrone())) {
            
          final int lastCarStepIndexWithoutDrone =
            solution.getCarSchedule().getLastCarStepInARowWithoutDroneInteraction(
              move.getFirstCarStepIndex(),
              solution.getDrone());
              
          final Position startPosition =
            (move.getFirstCarStepIndex() > 0)
              ? solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() - 1).getEndPoint()
              : solution.getInstance().getInstanceParameters().getDepot();
          final Position newLandingPosition =
            solution.getCarSchedule().getSteps().get(lastCarStepIndexWithoutDrone - 1).getEndPoint();
            
          if (
          
          this.isInSpeedUpArea(solution, move, lastCarStepIndexWithoutDrone - move.getFirstCarStepIndex() + 1, 2)
            
            && this
              .isTogetherAfterwards(solution, move, lastCarStepIndexWithoutDrone - move.getFirstCarStepIndex() + 1, 2)
              
            && this.isValidDroneFlyingTime(
              solution,
              startPosition,
              droneStep0.getEndPoint(),
              newLandingPosition,
              solution.getDrone())) {
            move.setLastCarStepIndex(lastCarStepIndexWithoutDrone);
            return true;
          }
        }
        
      }
    }
    return false;
  }
  
  private boolean canApplyType5(SpeedUpSolution solution, SpeedUpMove move) {
    
    if (
    
    this.isInSpeedUpArea(solution, move, 1, 3) && this.isTogetherBefore(solution, move)) {
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStep1 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      final DroneStep droneStepLast = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 2);
      final Position startPosition =
        (move.getFirstCarStepIndex() > 0)
          ? solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() - 1).getEndPoint()
          : solution.getInstance().getInstanceParameters().getDepot();
          
      if (
      
      carStep0.getDronesToTake().contains(solution.getDrone())
        && droneStep0.getCarToRideOn() != null
        && droneStep0.getCarToRideOn() == solution.getCar()
        && carStep0.getEndPoint().equals(droneStep0.getEndPoint())
        
        && droneStep1.getCarToRideOn() == null
        && droneStepLast.getCarToRideOn() == null) {
        if (move.getFirstCarStepIndex() + 1 < solution.getCarSchedule().getSteps().size()
          && !solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + 1).getDronesToTake().contains(
            solution.getDrone())) {
            
          final int lastCarStepIndexWithoutDrone =
            solution.getCarSchedule().getLastCarStepInARowWithoutDroneInteraction(
              move.getFirstCarStepIndex(),
              solution.getDrone());
          if (this.isInSpeedUpArea(solution, move, lastCarStepIndexWithoutDrone - move.getFirstCarStepIndex() + 1, 3)
            
            && this
              .isTogetherAfterwards(solution, move, lastCarStepIndexWithoutDrone - move.getFirstCarStepIndex() + 1, 3)
            && this.isValidDroneFlyingTime(
              solution,
              startPosition,
              droneStep1.getEndPoint(),
              droneStepLast.getEndPoint(),
              solution.getDrone())) {
            return true;
          }
        }
        
      }
    }
    
    return false;
  }
  
  private boolean canApplyType6(SpeedUpSolution solution, SpeedUpMove move) {
    
    if (
    
    this.isInSpeedUpArea(solution, move, 1, 2) && this.isTogetherBefore(solution, move)) {
      
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStepLast = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      
      if (
      
      !carStep0.getDronesToTake().contains(solution.getDrone())
        
        && droneStep0.getCarToRideOn() == null
        && droneStepLast.getCarToRideOn() == null) {
        
        if (move.getFirstCarStepIndex() + 1 < solution.getCarSchedule().getSteps().size()
          && !solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + 1).getDronesToTake().contains(
            solution.getDrone()))
            
        {
          final int lastCarStepIndexWithoutDrone =
            solution.getCarSchedule().getLastCarStepInARowWithoutDroneInteraction(
              move.getFirstCarStepIndex(),
              solution.getDrone());
              
          if (this.isInSpeedUpArea(solution, move, lastCarStepIndexWithoutDrone - move.getFirstCarStepIndex() + 1, 2)
            
            && this
              .isTogetherAfterwards(solution, move, lastCarStepIndexWithoutDrone - move.getFirstCarStepIndex() + 1, 2)
            && this.isValidDroneFlyingTime(
              solution,
              carStep0.getEndPoint(),
              droneStep0.getEndPoint(),
              droneStepLast.getEndPoint(),
              solution.getDrone())) {
            return true;
          }
        }
        
      }
    }
    
    return false;
  }
  
  private boolean canApplyType7(SpeedUpSolution solution, SpeedUpMove move) {
    
    if (
    
    this.isInSpeedUpArea(solution, move, 2, 2)
      
      && this.isTogetherBefore(solution, move)) {
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final ArrayList<CarStep> carSteps = solution.getCarSchedule().getSteps();
      
      if (carStep0.getEndPoint().equals(droneStep0.getEndPoint())
        && carStep0.getDronesToTake().contains(solution.getDrone())
        && droneStep0.getCarToRideOn().equals(solution.getCar())) {
        int lastCarStepIndexWithDrone =
          solution
            .getCarSchedule()
            .getLastCarStepInARowWithDroneRidingCar(move.getFirstCarStepIndex(), solution.getDrone());
            
        if (lastCarStepIndexWithDrone != solution.getCarSchedule().getSteps().size() - 1) {
          lastCarStepIndexWithDrone = lastCarStepIndexWithDrone - 1;
        }
        
        if (lastCarStepIndexWithDrone > move.getFirstCarStepIndex()
          
          && this.isTogetherAfterwards(
            solution,
            move,
            lastCarStepIndexWithDrone - move.getFirstCarStepIndex() + 1,
            lastCarStepIndexWithDrone - move.getFirstCarStepIndex() + 1)) {
            
          List<Integer> carStepIndices = new ArrayList<Integer>();
          for (int i = 0; i < lastCarStepIndexWithDrone - move.getFirstCarStepIndex() + 1; i++) {
            if (
            
            solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + i).getDeliveredBoxes().size() == 1) {
              carStepIndices.add(i);
            }
          }
          Collections.shuffle(carStepIndices);
          
          for (int carStep : carStepIndices) {
            final AbandonablePositionChecker checker =
              new AbandonablePositionChecker(
                solution.getCurrentSolution(),
                solution.getCarSchedule(),
                move.getFirstCarStepIndex() + carStep);
            if (checker.isAbandoningPossible()) {
              
              final int carIndexToGiveUp = checker.getCarIndexLeadingToAbandonablePosition();
              
              List<Integer> listForDeparture = new ArrayList<Integer>();
              for (int j = move.getFirstCarStepIndex() - 1; j < carIndexToGiveUp; j++)
                listForDeparture.add(j);
              Collections.shuffle(listForDeparture);
              
              List<Integer> listForArrival = new ArrayList<Integer>();
              for (int j = carIndexToGiveUp + 1; j <= lastCarStepIndexWithDrone; j++)
                listForArrival.add(j);
              Collections.shuffle(listForArrival);
              
              for (final Integer arrival : listForArrival)
                for (final Integer departure : listForDeparture) {
                  
                  if ((departure == -1
                    && this.isValidDroneFlyingTime(
                      solution,
                      solution.getInstance().getInstanceParameters().getDepot(),
                      carSteps.get(carIndexToGiveUp).getEndPoint(),
                      carSteps.get(arrival).getEndPoint(),
                      solution.getDrone()))
                    || (departure >= 0
                      && this.isValidDroneFlyingTime(
                        solution,
                        carSteps.get(departure).getEndPoint(),
                        carSteps.get(carIndexToGiveUp).getEndPoint(),
                        carSteps.get(arrival).getEndPoint(),
                        solution.getDrone()))) {
                    move.setAbandonablePositionChecker(checker);
                    move.setSavedSolution(solution.getCurrentSolution());
                    
                    move.setDroneDepartsAfterCarStepIndex(departure);
                    move.setDroneOvertakesBoxAfterCarStepIndex(carIndexToGiveUp);
                    move.setDroneLandsAfterCarStepIndex(arrival);
                    return true;
                  }
                  
                }
            }
          }
        }
        
      }
    }
    
    return false;
  }
  
  private boolean canApplyType8(SpeedUpSolution solution, SpeedUpMove move) {
    
    if (
    
    this.isInSpeedUpArea(solution, move, 1, 2)
      
      && this.isTogetherBefore(solution, move)) {
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStep1 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      
      if (!carStep0.getDronesToTake().contains(solution.getDrone())
        && droneStep0.getCarToRideOn() == null
        && droneStep1.getCarToRideOn() == null
        
        && droneStep0.getDeliveredBox() != null) {
        final int lastCarStepIndex =
          solution.getCarSchedule().getLastCarStepInARowWithoutDroneInteraction(
            move.getFirstCarStepIndex(),
            solution.getDrone());
            
        if (this.isTogetherAfterwards(solution, move, lastCarStepIndex - move.getFirstCarStepIndex() + 1, 2)
          && this.isInSpeedUpArea(solution, move, lastCarStepIndex - move.getFirstCarStepIndex() + 1, 2)) {
          move.setLastCarStepIndex(lastCarStepIndex);
          move.setBoxInsertion(
            new BoxInsertion(
              solution.getCurrentSolution(),
              solution.getCarSchedule(),
              solution.getNumberOfCarStepsBefore(),
              solution.getNumberOfCarStepsAfter(),
              droneStep0.getDeliveredBox()));
          move.setSavedSolution(solution.getCurrentSolution());
          return true;
          
        }
        
      }
      
    }
    
    return false;
  }
  
  private boolean canApplyType9(SpeedUpSolution solution, SpeedUpMove move) {
    if (
    
    this.isInSpeedUpArea(solution, move, 2, 2)
      
      && this.isTogetherBefore(solution, move)
      
      && this.isTogetherAfterwards(solution, move, 2, 2)) {
      
      final CarStep carStep0 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex());
      final CarStep carStep1 = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + 1);
      final DroneStep droneStep0 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex());
      final DroneStep droneStep1 = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + 1);
      final Position startPosition =
        (move.getFirstCarStepIndex() > 0)
          ? solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() - 1).getEndPoint()
          : solution.getInstance().getInstanceParameters().getDepot();
      final AbandonablePositionChecker abandonablePositionChecker =
        new AbandonablePositionChecker(
          solution.getCurrentSolution(),
          solution.getCarSchedule(),
          move.getFirstCarStepIndex());
          
      if (
      
      droneStep0.getDeliveredBox() != null
        && droneStep0.getCarToRideOn() == null
        && droneStep1.getCarToRideOn() == null
        
        && carStep0.getDeliveredBoxes().size() == 1
        && !carStep0.getDronesToTake().contains(solution.getDrone())
        && !carStep1.getDronesToTake().contains(solution.getDrone())
        
        && droneStep1.getEndPoint().equals(carStep1.getEndPoint())
        
        && abandonablePositionChecker.isAbandoningPossible()
        
        && this.isValidDroneFlyingTime(
          solution,
          startPosition,
          carStep0.getEndPoint(),
          droneStep1.getEndPoint(),
          solution.getDrone())) {
        move.setAbandonablePositionChecker(abandonablePositionChecker);
        
        return true;
      }
    }
    
    return false;
  }
  
  private boolean isInSpeedUpArea(SpeedUpSolution solution, SpeedUpMove move, int lengthForCar, int lengthForDrone) {
    if (move.getFirstCarStepIndex() + lengthForCar - 1 < solution.getCarSchedule().getSteps().size()
      - solution.getNumberOfCarStepsAfter()
      && move.getFirstDroneStepIndex() + lengthForDrone - 1 < solution.getDroneSchedule().getSteps().size()
        - solution.getNumberOfDroneStepsAfter()
      && move.getFirstCarStepIndex() >= solution.getNumberOfCarStepsBefore()
      && move.getFirstDroneStepIndex() >= solution.getNumberOfDroneStepsBefore())
      return true;
    else
      return false;
  }
  
  private boolean isTogetherBefore(final SpeedUpSolution solution, final SpeedUpMove move) {
    if (move.getFirstCarStepIndex() == 0 && move.getFirstDroneStepIndex() == 0)
      return true;
      
    if (move.getFirstCarStepIndex() == 0 || move.getFirstDroneStepIndex() == 0)
      return false;
      
    final CarStep previousCarStep = solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() - 1);
    final DroneStep previousDroneStep = solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() - 1);
    
    if (!previousCarStep.getEndPoint().equals(previousDroneStep.getEndPoint()))
      return false;
      
    if (previousCarStep.getDronesToTake().contains(solution.getDrone())
      && previousDroneStep.getCarToRideOn() != null
      && previousDroneStep.getCarToRideOn().equals(solution.getCar())
      && previousCarStep.getEndPoint().equals(previousDroneStep.getEndPoint()))
      return true;
      
    return false;
  }
  
  private boolean isTogetherAfterwards(
    final SpeedUpSolution solution,
    final SpeedUpMove move,
    final int lengthForCar,
    final int lengthForDrone) {
    
    if (move.getFirstDroneStepIndex() + lengthForDrone == solution.getDroneSchedule().getSteps().size()
      && move.getFirstCarStepIndex() + lengthForCar == solution.getCarSchedule().getSteps().size())
      return true;
      
    if (move.getFirstDroneStepIndex() + lengthForDrone == solution.getDroneSchedule().getSteps().size()
      || move.getFirstCarStepIndex() + lengthForCar == solution.getCarSchedule().getSteps().size())
      return false;
      
    final CarStep followingCarStep =
      solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + lengthForCar);
    final DroneStep followingDroneStep =
      solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + lengthForDrone);
      
    if (!solution.getCarSchedule().getSteps().get(move.getFirstCarStepIndex() + lengthForCar - 1).getEndPoint().equals(
      solution.getDroneSchedule().getSteps().get(move.getFirstDroneStepIndex() + lengthForDrone - 1).getEndPoint()))
      return false;
      
    if (followingCarStep.getDronesToTake().contains(solution.getDrone())
      && followingDroneStep.getCarToRideOn() != null
      && followingDroneStep.getCarToRideOn().equals(solution.getCar())
      && followingCarStep.getEndPoint().equals(followingDroneStep.getEndPoint()))
      return true;
      
    return false;
  }
  
  private boolean isValidDroneFlyingTime(
    final SpeedUpSolution solution,
    final Position droneStart,
    final Position droneDelivery,
    final Position droneFinish,
    final Drone drone) {
    return (solution.getInstance().getDroneFlyingTime(droneStart, droneDelivery)
      + solution.getInstance().getDroneFlyingTime(droneDelivery, droneFinish) <= drone.getReachOfDrone());
      
  }
  
  
  private List<Integer> getStepsOfInterestForDroneOnCar(Collection<SpeedUpTypeOfMove> activeMoves) {
    
    List<Integer> list = new ArrayList<Integer>();
    
    for (SpeedUpTypeOfMove move : activeMoves) {
      list.add(move.getMoveNumber());
      list.add(9 + move.getMoveNumber());
    }
    
    Collections.shuffle(list);
    
    return list;
  }
  
  private SpeedUpMove getRandomMoveToFirstIndicesAndFollowingStep(
    SpeedUpSolution solution,
    int firstIndexOfCar,
    int firstIndexOfDrone) {
    
    for (Integer i : this.getStepsOfInterestForDroneOnCar(solution.getData().getActiveTypesOfMoves())) {
      switch (i) {
        case 1: {
          SpeedUpMove moveType1 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_1);
          if (this.canApplyType1(solution, moveType1)) {
            return moveType1;
          }
          break;
        }
        case 2: {
          SpeedUpMove moveType2 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_2);
          if (this.canApplyType2(solution, moveType2)) {
            return moveType2;
          }
          break;
        }
        case 3: {
          SpeedUpMove moveType3 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_3);
          if (this.canApplyType3(solution, moveType3)) {
            return moveType3;
          }
          break;
        }
        case 4: {
          SpeedUpMove moveType4 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_4);
          if (this.canApplyType4(solution, moveType4)) {
            return moveType4;
          }
          break;
        }
        case 5: {
          SpeedUpMove moveType5 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_5);
          if (this.canApplyType5(solution, moveType5))
            return moveType5;
          break;
        }
        case 6: {
          SpeedUpMove moveType6 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_6);
          if (this.canApplyType6(solution, moveType6)) {
            return moveType6;
          }
          break;
        }
        case 7: {
          SpeedUpMove moveType7 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_7);
          if (this.canApplyType7(solution, moveType7))
            return moveType7;
          break;
        }
        case 8: {
          SpeedUpMove moveType8 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_8);
          if (this.canApplyType8(solution, moveType8)) {
            return moveType8;
          }
          break;
        }
        case 9: {
          SpeedUpMove moveType9 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_9);
          if (this.canApplyType9(solution, moveType9)) {
            return moveType9;
          }
          break;
        }
        case 10: {
          SpeedUpMove moveType1 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_1);
          if (this.canApplyType1(solution, moveType1))
            return moveType1;
          break;
        }
        case 11: {
          SpeedUpMove moveType2 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_2);
          if (this.canApplyType2(solution, moveType2))
            return moveType2;
          break;
        }
        case 12: {
          SpeedUpMove moveType3 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_3);
          if (this.canApplyType3(solution, moveType3))
            return moveType3;
          break;
        }
        case 13: {
          SpeedUpMove moveType4 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_4);
          if (this.canApplyType4(solution, moveType4))
            return moveType4;
          break;
        }
        case 14: {
          SpeedUpMove moveType5 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_5);
          if (this.canApplyType5(solution, moveType5))
            return moveType5;
          break;
        }
        case 15: {
          SpeedUpMove moveType6 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_6);
          if (this.canApplyType6(solution, moveType6))
            return moveType6;
          break;
        }
        case 16: {
          SpeedUpMove moveType7 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_7);
          if (this.canApplyType7(solution, moveType7))
            return moveType7;
          break;
        }
        case 17: {
          SpeedUpMove moveType8 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_8);
          if (this.canApplyType8(solution, moveType8))
            return moveType8;
          break;
        }
        case 18: {
          SpeedUpMove moveType9 = new SpeedUpMove(firstIndexOfCar + 1, firstIndexOfDrone + 1, SpeedUpTypeOfMove.TYPE_9);
          if (this.canApplyType9(solution, moveType9))
            return moveType9;
          break;
        }
        default: {
          break;
        }
      }
    }
    
    return new SpeedUpMove(0, 0, SpeedUpTypeOfMove.TYPE_0);
  }
  
  public SpeedUpMove getRandomMove(SpeedUpSolution solution, Random random) {
    int indexOfCarStep =
      random.nextInt(
        solution.getCarSchedule().getSteps().size()
          - solution.getNumberOfCarStepsAfter()
          - solution.getNumberOfCarStepsBefore())
        + solution.getNumberOfCarStepsBefore();
        
    while (indexOfCarStep > 0
      && indexOfCarStep < solution.getCarSchedule().getSteps().size() - solution.getNumberOfCarStepsAfter()
      && solution
        .getCarSchedule()
        .getNumberOfCorrespondingDroneStep(indexOfCarStep, solution.getDroneSchedule()) == -1) {
      indexOfCarStep++;
    }
    
    int indexOfDroneStep =
      (indexOfCarStep > 0)
        ? solution.getCarSchedule().getNumberOfCorrespondingDroneStep(indexOfCarStep, solution.getDroneSchedule())
        : 0;
        
    SpeedUpMove result = this.getRandomMoveToFirstIndicesAndFollowingStep(solution, indexOfCarStep, indexOfDroneStep);
    
    solution.getData().increaseMoveCount(result.getSpeedUpTypeOfMove());
    
    return result;
  }
  
  private List<SpeedUpMove> getAllMovesToFirstIndexOfCar(SpeedUpSolution solution, int firstIndexOfCar) {
    List<SpeedUpMove> moves = new ArrayList<SpeedUpMove>();
    
    final int correspondingDroneStep =
      solution.getDroneSchedule().getCorrespondingStep(solution.getCarSchedule().getSteps().get(firstIndexOfCar));
      
    if (correspondingDroneStep != -1) {
      SpeedUpMove moveType1 = new SpeedUpMove(firstIndexOfCar, correspondingDroneStep, SpeedUpTypeOfMove.TYPE_1);
      if (this.canApplyType1(solution, moveType1))
        moves.add(moveType1);
        
      SpeedUpMove moveType5 = new SpeedUpMove(firstIndexOfCar, correspondingDroneStep, SpeedUpTypeOfMove.TYPE_5);
      if (this.canApplyType5(solution, moveType5))
        moves.add(moveType5);
        
      SpeedUpMove moveType7 = new SpeedUpMove(firstIndexOfCar, correspondingDroneStep, SpeedUpTypeOfMove.TYPE_7);
      if (this.canApplyType7(solution, moveType7))
        moves.add(moveType7);
    }
    
    else
      if (firstIndexOfCar == 0
        || solution.getDroneSchedule().getCorrespondingStep(
          solution.getCarSchedule().getSteps().get(firstIndexOfCar - 1)) != -1) {
      final int firstIndexOfDrone =
        (firstIndexOfCar == 0)
          ? 0
          : solution
            .getDroneSchedule()
            .getCorrespondingStep(solution.getCarSchedule().getSteps().get(firstIndexOfCar - 1));
            
      SpeedUpMove moveType2 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_2);
      if (this.canApplyType2(solution, moveType2))
        moves.add(moveType2);
        
      SpeedUpMove moveType3 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_3);
      if (this.canApplyType3(solution, moveType3))
        moves.add(moveType3);
        
      SpeedUpMove moveType4 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_4);
      if (this.canApplyType4(solution, moveType4))
        moves.add(moveType4);
        
      SpeedUpMove moveType6 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_6);
      if (this.canApplyType6(solution, moveType6))
        moves.add(moveType6);
        
      SpeedUpMove moveType8 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_8);
      if (this.canApplyType8(solution, moveType8))
        moves.add(moveType8);
        
      SpeedUpMove moveType9 = new SpeedUpMove(firstIndexOfCar, firstIndexOfDrone, SpeedUpTypeOfMove.TYPE_9);
      if (this.canApplyType9(solution, moveType9))
        moves.add(moveType9);
    }
    
    return moves;
  }
  
  public List<SpeedUpMove> getAllMoves(SpeedUpSolution solution) {
    List<SpeedUpMove> moves = new ArrayList<SpeedUpMove>();
    
    for (int indexOfCarStep =
      solution.getNumberOfCarStepsBefore(); indexOfCarStep < solution.getCarSchedule().getSteps().size()
        - solution.getNumberOfCarStepsAfter(); indexOfCarStep++) {
      this.getAllMovesToFirstIndexOfCar(solution, indexOfCarStep);
    }
    
    return moves;
  }
  
}
