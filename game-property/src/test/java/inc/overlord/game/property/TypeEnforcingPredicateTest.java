/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.util.function.Predicate;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author achelian
 */
public class TypeEnforcingPredicateTest {
  TypeEnforcingPredicate<String> predicateNullBlocked;
  TypeEnforcingPredicate<String> predicateNullAllowed;
  
  @Before
  public void setUp() {
    predicateNullBlocked = new TypeEnforcingPredicate<>(String.class, false);
    predicateNullAllowed = new TypeEnforcingPredicate<>(String.class, true);
  }

  @Test
  public void testHappyCase() {
    assertTrue(predicateNullBlocked.test("foo"));
    assertTrue(predicateNullAllowed.test("foo"));
    assertEquals(String.class, predicateNullBlocked.getType());
    assertFalse(predicateNullBlocked.isNullAllowed());
    assertTrue(predicateNullAllowed.isNullAllowed());
  }

  @Test
  public void testFalseCase() {
    assertFalse(((Predicate) predicateNullBlocked).test(1));
    assertFalse(((Predicate) predicateNullAllowed).test(2));
  }

  @Test
  public void testNullCase() {
    assertFalse(predicateNullBlocked.test(null));
    assertTrue(predicateNullAllowed.test(null));
  }

  @Test(expected = NullPointerException.class)
  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  public void testNullClassInConstructor() {
    new TypeEnforcingPredicate<>(null, false);
  }
}
