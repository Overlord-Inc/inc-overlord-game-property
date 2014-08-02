/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.Mock;

/**
 *
 * @author achelian
 */
public class SetMatchingPredicateTest {
  SetMatchingPredicate<String> predicate;
  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    predicate = new SetMatchingPredicate<>();
    predicate.addPropertyChangeListener(listener);
  }

  @Test
  public void testValidateOnDefault() {
    predicate.validate();
    assertFalse(predicate.isCreationMode());
  }

  @Test
  public void testValidateHappyCase() {
    predicate.setMatches(singleton("ha"));
    predicate.validate();
    assertFalse(predicate.isCreationMode());
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateSetNullBeforeValidate() {
    predicate.setMatches(null);
    predicate.validate();
  }

  @Test
  public void testMatch() {
    predicate.setMatches(singleton("ha"));
    predicate.validate();
    assertTrue(predicate.test("ha"));
    assertFalse(predicate.test(null));
  }

  @Test
  public void testNonMatch() {
    predicate.validate();
    assertFalse(predicate.test("ho"));
  }

  @Test
  public void testSetMatchHappyCase() {
    predicate.validate();
    Set<String> matchSet = singleton("ha");
    predicate.setMatches(matchSet);
    assertEquals(matchSet, predicate.getMatches());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("matches", emptySet(), matchSet)));
  }

  @Test
  public void testSetMatchNull() {
    predicate.validate();
    predicate.setMatches(null);
    verifyZeroInteractions(listener);
  }

  @Test
  public void testVetoableChangeMatchesHappyCase() throws PropertyVetoException {
    predicate.validate();
    Set<String> otherSet = singleton("ha");
    predicate.vetoableChange(new PropertyChangeEvent(this, "matches", emptySet(), otherSet));
    assertEquals(otherSet, predicate.getMatches());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("matches", emptySet(), otherSet)));
  }

  @Test
  public void testVetoableChangeMatchesNull() throws PropertyVetoException {
    predicate.validate();
    try {
      predicate.vetoableChange(new PropertyChangeEvent(this, "matches", emptySet(), null));
      fail("Should have thrown exception");
    }
    catch (PropertyVetoException e) {
      // nothing
    }
    finally {
      verifyZeroInteractions(listener);
    }
  }

  @Test
  public void testVetoableChangeNothing() throws PropertyVetoException {
    predicate.validate();
    predicate.vetoableChange(new PropertyChangeEvent(this, "nothing", null, listener));
    verifyZeroInteractions(listener);
  }

  @Test
  public void testEqualsAndHashCodeEquals() {
    SetMatchingPredicate<String> first = new SetMatchingPredicate<>();
    first.setMatches(singleton("ha"));
    first.validate();
    SetMatchingPredicate<String> second = new SetMatchingPredicate<>();
    second.setMatches(singleton("ha"));
    second.validate();
    assertEquals(first.hashCode(), second.hashCode());
    assertTrue(first.equals(second));
    assertTrue(second.equals(first));
  }

  @Test
  public void testNotEquals() {
    SetMatchingPredicate<String> first = new SetMatchingPredicate<>();
    first.setMatches(singleton("ha"));
    first.validate();
    SetMatchingPredicate<String> second = new SetMatchingPredicate<>();
    second.setMatches(singleton("ho"));
    second.validate();
    assertFalse(first.equals(second));
    assertFalse(second.equals(first));
  }
}
