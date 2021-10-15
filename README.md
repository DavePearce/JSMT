# Overview

This is a simple library for enumerating values under _constraints_.
For example, suppose you wanted to generate all arrays of size `3`
which are _sorted_.  Or, perhaps you wanted to generate all solutions
to a given formula.  Then, this is the library for you!

## Getting Started

As a simple example, let's suppose we have the following `Point`
class:


```Java
public class Point {
  private final int x;
  private final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  ...
 
  public String toString() {
    return "{x=" + x + ",y=" + y + "}";
  }  
}
```

What we want to do is enumerate all `Point` instances for `0 <= x <=
5` and `0 <= y <= 5`.

Constraint.Set<Point> constraints = new Constraint.Set<>(vs -> new Point(vs[0],vs[1]));
Constraint.Variable x = constraints.add(between(0,5));
Constraint.Variable y = constraints.add(between(0,5));
//
for(Point p : constraints) {
   System.out.println(p);
}		