/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 *
 * @author achelian
 */
public class RangePredicateTest {
  RangePredicate<Integer> predicate;
  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    predicate = new RangePredicate<>();
  }

  public void initPredicate() {
    predicate.setMax(10);
    predicate.setMin(0);
    predicate.addPropertyChangeListener(listener);
    predicate.validate();
  }

  @Test
  public void testSetMaxHappyCase() {
    initPredicate();
    predicate.setMax(20);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("max", 10, 20)));
    verifyNoMoreInteractions(listener);
    assertEquals(20, (int) predicate.getMax());
  }

  @Test
  public void testSetMaxNull() {
    initPredicate();
    predicate.setMax(null);
    assertEquals(10, (int) predicate.getMax());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testSetMaxBelowMin() {
    initPredicate();
    predicate.setMax(-5);
    verifyZeroInteractions(listener);
    assertEquals(10, (int) predicate.getMax());
  }

  @Test
  public void testSetMinHappyCase() {
    initPredicate();
    predicate.setMin(5);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("min", 0, 5)));
    verifyNoMoreInteractions(listener);
    assertEquals(5, (int) predicate.getMin());
  }

  @Test
  public void testSetMinNull() {
    initPredicate();
    predicate.setMin(null);
    assertEquals(0, (int) predicate.getMin());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testSetMinAboveMax() {
    initPredicate();
    predicate.setMin(15);
    verifyZeroInteractions(listener);
    assertEquals(0, (int) predicate.getMin());
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateDefault() {
    predicate.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateMissingMin() {
    predicate.setMax(10);
    predicate.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateMissingMax() {
    predicate.setMin(0);
    predicate.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateMinGreaterThanMax() {
    predicate.setMin(10);
    predicate.setMax(0);
    predicate.validate();
  }

  @Test
  public void testValidateHappyCase() {
    assertTrue(predicate.isCreationMode());
    predicate.setMin(0);
    predicate.setMax(10);
    predicate.validate();
    assertFalse(predicate.isCreationMode());
  }

  @Test
  public void testHappyCase() {
    initPredicate();
    assertTrue(predicate.test(5));
  }

  @Test
  public void testGreaterThanMax() {
    initPredicate();
    assertFalse(predicate.test(15));
  }

  @Test
  public void testLessThanMin() {
    initPredicate();
    assertFalse(predicate.test(-5));
  }

  @Test
  public void testVetoableMaxHappyCase() throws PropertyVetoException {
    initPredicate();
    predicate.vetoableChange(new PropertyChangeEvent(this, "max", 10, 5));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("max", 10, 5)));
    verifyNoMoreInteractions(listener);
    assertEquals(5, (int) predicate.getMax());
  }

  @Test
  public void testVetoableMaxBelowMin() {
    initPredicate();
    try {
      predicate.vetoableChange(new PropertyChangeEvent(this, "max", 10, -5));
      fail("Should have thrown PropertyVetoException");
    }
    catch (PropertyVetoException e) {
      assertEquals(10, (int) predicate.getMax());
      verifyZeroInteractions(listener);
    }
  }

  @Test
  public void testVetoableMinHappyCase() throws PropertyVetoException {
    initPredicate();
    predicate.vetoableChange(new PropertyChangeEvent(this, "min", 0, 5));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("min", 0, 5)));
    verifyNoMoreInteractions(listener);
    assertEquals(5, (int) predicate.getMin());
  }

  @Test
  public void testVetoableMinAboveMax() {
    initPredicate();
    try {
      predicate.vetoableChange(new PropertyChangeEvent(this, "min", 0, 15));
      fail("Should have thrown PropertyVetoException");
    }
    catch (PropertyVetoException e) {
      assertEquals(0, (int) predicate.getMin());
      verifyZeroInteractions(listener);
    }
  }

  @Test
  public void testVetoableUnsupportedProperty() throws PropertyVetoException {
    initPredicate();
    predicate.vetoableChange(new PropertyChangeEvent(this, "nothing", null, 6));
    assertEquals(0, (int) predicate.getMin());
    assertEquals(10, (int) predicate.getMax());
    verifyZeroInteractions(listener);
  }
}
