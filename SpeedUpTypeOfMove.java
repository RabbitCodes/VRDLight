package speedUp;

public enum SpeedUpTypeOfMove {
  
  TYPE_0(0),
  TYPE_1(1),
  TYPE_2(2),
  TYPE_3(3),
  TYPE_4(4),
  TYPE_5(5),
  TYPE_6(6),
  TYPE_7(7),
  TYPE_8(8),
  TYPE_9(9);
  
  private Integer moveNumber;
  
  SpeedUpTypeOfMove(int moveNumber) {
    this.moveNumber = moveNumber;
  }
  
  public Integer getMoveNumber() {
    return moveNumber;
  }
  
}
