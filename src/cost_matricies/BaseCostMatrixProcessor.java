package cost_matricies;

/**
 * Abstract implementation of {@link CostMatrixProcessor} interface which provides universal support
 * for determining if a CostMatrixProcessor is horizontal or vertical.
 */
public abstract class BaseCostMatrixProcessor implements CostMatrixProcessor {

  /**
   * Boolean signalling if this {@link CostMatrixProcessor} is horizontal or not.
   */
  private final boolean isHorizontal;

  /**
   *
   * @param isHorizontal
   */
  protected BaseCostMatrixProcessor(boolean isHorizontal) {
    this.isHorizontal = isHorizontal;
  }

  @Override
  public boolean isHorizontal() {
    return isHorizontal;
  }

  @Override
  public boolean isVertical() {
    return !isHorizontal;
  }
}
