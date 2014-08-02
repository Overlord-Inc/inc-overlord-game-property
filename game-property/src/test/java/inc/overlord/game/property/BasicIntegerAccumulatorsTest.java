/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author achelian
 */
public class BasicIntegerAccumulatorsTest {
  static final int[] ONE_THROUGH_THREE = { 1, 2, 3 };
  int[] values;
  Stream valueStream;
  @Before
  public void setUp() {
    valueStream = Stream.of(1, 2, 3);
  }

  @Test
  public void testSum() {
    assertEquals(6, (int) BasicIntegerAccumulators.SUM.accumulate(valueStream));
  }

  @Test
  public void testMax() {
    assertEquals(3, (int) BasicIntegerAccumulators.MAX.accumulate(valueStream));
  }

  @Test
  public void testMin() {
    assertEquals(1, (int) BasicIntegerAccumulators.MIN.accumulate(valueStream));
  }

  @Test
  public void testProduct() {
    assertEquals(6, (int) BasicIntegerAccumulators.PRODUCT.accumulate(valueStream));
  }

  @Test
  public void testCount() {
    assertEquals(3, (int) BasicIntegerAccumulators.COUNT.accumulate(valueStream));
  }
}
