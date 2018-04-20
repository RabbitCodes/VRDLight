package model.instance;

public class Edge {
  
  final Position start;
  
  final Position end;
  
  public Edge(Position start, Position end) {
    super();
    this.start = start;
    this.end = end;
  }
  
  public Position getStart() {
    return start;
  }
  
  public Position getEnd() {
    return end;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());
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
    Edge other = (Edge) obj;
    if (end == null) {
      if (other.end != null)
        return false;
    } else if (!end.equals(other.end))
      return false;
    if (start == null) {
      if (other.start != null)
        return false;
    } else if (!start.equals(other.start))
      return false;
    return true;
  }
  
}
