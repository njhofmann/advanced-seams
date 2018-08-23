package masks;

/**
 * Represents an immutable (x, y) coordinate where x and y are both >= 0.
 */
public class Coordinate {

  /**
   * X value of this masks.Coordinate.
   */
  private final int x;

  /**
   * Y value of this masks.Coordinate.
   */
  private final int y;

  /**
   * Default constructor for this masks.Coordinate, takes in two natural numbers to represent the x and y
   * values of this masks.Coordinate.
   * @param x int to assign as the x value of this masks.Coordinate
   * @param y int to assign as the y value of this masks.Coordinate
   * @throws IllegalArgumentException if either given int is negative
   */
  public Coordinate(int x, int y) {
    if (x < 0) {
      throw new IllegalArgumentException("Given x coordinate must be >= 0!");
    }
    else if (y < 0) {
      throw new IllegalArgumentException("Given y coordinate must be >= 0!");
    }

    this.x = x;
    this.y = y;
  }

  /**
   * Retrieves this masks.Coordinate's x value.
   * @return this masks.Coordinate's x value.
   */
  public int getX() {
    return x;
  }

  /**
   * Retrieves this masks.Coordinate's y value.
   * @return this masks.Coordinate's y value.
   */
  public int getY() {
    return y;
  }

  /**
   * Retrieves this masks.Coordinate's x and y values.
   * @return this masks.Coordinate's x and y values as a two length int array
   */
  public int[] getXY() {
    return new int[]{this.x, this.y};
  }

}
