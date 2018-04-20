package model.instance;

public class Position {
  
  final double x;
  
  final double y;
  
  public Position(double xCoordinate, double yCoordinate) {
    super();
    this.x = xCoordinate;
    this.y = yCoordinate;
  }
  
  public double getX() {
    return x;
  }
  
  public double getY() {
    return y;
  }
  
  public String toString() {
    return "(" + String.valueOf(this.getX()) + "," + String.valueOf(this.getY()) + ")";
  }
  
  public Position deepCopy() {
    return new Position(this.getX(), this.getY());
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    Position other = (Position) obj;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
      return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
      return false;
    return true;
  }

  
  public double getEuclideanDistanceToDepot() {
    return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
  }
  
  public int compareByAngle(Position position2) {
    return Double.compare(Math.atan2(this.getX(), this.getY()), Math.atan2(position2.getX(), position2.getY()));
  }
  
}
