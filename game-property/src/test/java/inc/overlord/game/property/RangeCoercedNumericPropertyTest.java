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
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 *
 * @author achelian
 */
public class RangeCoercedNumericPropertyTest {
  RangeCoercedNumericProperty<Integer> property;
  RangePredicate<Integer> range;

  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    property = new RangeCoercedNumericProperty<>();
    range = new RangePredicate<>();
    range.setMax(10);
    range.setMin(0);
    range.validate();
    property.setPredicate(range);
    property.setValue(5);
    property.addPropertyChangeListener(listener);
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateOnEmptyItem() {
    property = new RangeCoercedNumericProperty();
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateOnMissingRange() {
    property = new RangeCoercedNumericProperty<>();
    property.setValue(10);
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateOnMissingValue() {
    property = new RangeCoercedNumericProperty<>();
    property.setPredicate(range);
    property.validate();
  }

  @Test
  public void testValidateHappyCase() {
    property.validate();
    assertEquals((Integer) 5, property.getValue());
    assertSame(range, property.getPredicate());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testValidateCoercionCase() {
    property.setValue(15);
    property.validate();
    assertEquals((Integer) 10, property.getValue());
    verifyZeroInteractions(listener);
  }

  @Test(expected = NullPointerException.class)
  @SuppressWarnings("null")
  public void testSetPredicateNullValue() {
    property.validate();
    property.setPredicate(null);
  }

  @Test
  public void testSetPredicateNoCoercionHappyCase() {
    property.validate();
    RangePredicate<Integer> replacementRange = new RangePredicate(3, 7);
    replacementRange.validate();
    property.setPredicate(replacementRange);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacementRange)));
    assertEquals((Integer) 5, property.getValue());
  }

  @Test
  public void testSetPredicateCoercionLow() {
    property.validate();
    RangePredicate<Integer> replacementRange = new RangePredicate(7, 10);
    replacementRange.validate();
    property.setPredicate(replacementRange);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacementRange)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
    assertEquals((Integer) 7, property.getValue());
  }

  @Test
  public void testSetPredicateCoercionHigh() {
    property.validate();
    RangePredicate<Integer> replacementRange = new RangePredicate(0, 3);
    replacementRange.validate();
    property.setPredicate(replacementRange);
    assertEquals((Integer) 3, property.getValue());
  }

  @Test
  public void testSetPredicateMinNoCoercion() {
    property.validate();
    range.setMin(3);
    assertEquals((Integer) 5, property.getValue());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testSetPredicateMaxNoCoercion() {
    property.validate();
    range.setMax(7);
    assertEquals((Integer) 5, property.getValue());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testSetPredicateMinWithCoercion() {
    property.validate();
    range.setMin(7);
    assertEquals((Integer) 7, property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test
  public void testSetPredicateMaxWithCoerceion() {
    property.validate();
    range.setMax(3);
    assertEquals((Integer) 3, property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 3)));
  }

  @Test
  public void testVetoableValueChangeHappyCase() throws PropertyVetoException {
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "value", 5, 7));
    assertEquals((Integer) 7, property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test
  public void testVetoableValueChangeFailsLow() {
    property.validate();
    try {
      property.vetoableChange(new PropertyChangeEvent(this, "value", 5, -1));
      fail("Did not throw exception");
    }
    catch (PropertyVetoException e) {
    }
    assertEquals((Integer) 5, property.getValue());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testVetoableValueChangeFailsHigh() {
    property.validate();
    try {
      property.vetoableChange(new PropertyChangeEvent(this, "value", 5, 11));
      fail("Did not throw exception");
    }
    catch (PropertyVetoException e) {
    }
    assertEquals((Integer) 5, property.getValue());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testVetoableChangePredicateHappyCase() throws PropertyVetoException {
    property.validate();
    RangePredicate<Integer> replacement = new RangePredicate(3, 7);
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, replacement));
    assertEquals((Integer) 5, property.getValue());
    assertSame(replacement, property.getPredicate());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangePredicateNotRangePredicate() throws PropertyVetoException {
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, 5));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangePredicateNullValue() throws PropertyVetoException {
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, null));
  }

  @Test
  public void testVetoableChangePredicateCoerceLow() throws PropertyVetoException {
    property.validate();
    RangePredicate<Integer> replacement = new RangePredicate<>(0, 3);
    replacement.validate();    
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, replacement));
    assertEquals((Integer) 3, property.getValue());
    assertSame(replacement, property.getPredicate());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 3)));
  }

  @Test
  public void testVetoableChangePredicateCoerceHigh() throws PropertyVetoException {
    property.validate();
    RangePredicate<Integer> replacement = new RangePredicate<>(7, 10);
    replacement.validate();    
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, replacement));
    assertEquals((Integer) 7, property.getValue());
    assertSame(replacement, property.getPredicate());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test
  public void testVetoableChangeNotHandled() throws PropertyVetoException {
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "nothing", null, 5));
    verifyZeroInteractions(listener);
  }
}
