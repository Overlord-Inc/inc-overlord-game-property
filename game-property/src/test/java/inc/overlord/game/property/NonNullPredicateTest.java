/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author achelian
 */
public class NonNullPredicateTest {
  NonNullPredicate<String> predicate;
  @Before
  public void setUp() {
    predicate = new NonNullPredicate();
  }

  @Test
  public void testFalseOnNull() {
    assertFalse(predicate.test(null));
  }

  @Test
  public void testTrueOnNonNull() {
    assertTrue(predicate.test(""));
  }
}
