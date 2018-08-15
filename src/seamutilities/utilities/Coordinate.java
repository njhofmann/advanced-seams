package seamutilities.utilities;

/**
 * Represents an immutable (x, y) coordinate where x and y are both >= 0.
 */
public class Coordinate {

  /**
   * x value of this Coordinate.
   */
  private final int x;

  /**
   * y value of this Coordinate.
   */
  private final int y;

  /**
   * Default constructor for this Coordinate, takes in two natural numbers to represent the x and y
   * values of this Coordinate.
   * @param x int to assign as the x value of this Coordinate
   * @param y int to assign as the y value of this Coordinate
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
   * Retrieves this Coordinate's x value.
   * @return this Coordinate's x value.
   */
  public int getX() {
    return this.x;
  }

  /**
   * Retrieves this Coordinate's y value.
   * @return this Coordinate's y value.
   */
  public int getY() {
    return this.y;
  }

  /**
   * Retrieves this Coordinate's x and y values.
   * @return this Coordinate's x and y values as a two length int array
   */
  public int[] getXY() {
    return new int[]{this.x, this.y};
  }

}
