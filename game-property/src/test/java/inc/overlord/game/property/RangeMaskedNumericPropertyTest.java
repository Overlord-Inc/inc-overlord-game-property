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
public class RangeMaskedNumericPropertyTest {
  RangeMaskedNumericProperty<Integer> property;
  RangePredicate<Integer> range;
  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    property = new RangeMaskedNumericProperty<>();
  }

  private void initProperty() {
    range = new RangePredicate<>(0, 10);
    property.setPredicate(range);
    property.setValue(5);
    property.addPropertyChangeListener(listener);
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateDefault() {
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateNullValue() {
    range = new RangePredicate<>(0, 10);
    range.validate();
    property.setPredicate(range);
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValdiateNullRange() {
    property.setValue(7);
    property.validate();
  }

  @Test
  public void testValidateHappyCase() {
    range = new RangePredicate<>(0, 10);
    range.validate();
    property.setValue(7);
    property.setPredicate(range);
    property.validate();
    assertEquals(7, (int) property.getValue());
    assertSame(range, property.getPredicate());
  }

  @Test
  public void testSetValueHappyCase() {
    initProperty();
    property.setValue(7);
    assertEquals(7, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test
  public void testSetValueOutOfRangeHigh() {
    initProperty();
    property.setValue(15);
    assertEquals(10, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 10)));
  }

  @Test
  public void testSetValueOutOfRangeLow() {
    initProperty();
    property.setValue(-5);
    assertEquals(0, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 0)));
  }

  @Test
  public void testSetPredicateHappyCase() {
    initProperty();
    RangePredicate<Integer> replacement = new RangePredicate<>(3, 7);
    property.setPredicate(replacement);
    assertEquals(5, (int) property.getValue());
    assertSame(replacement, property.getPredicate());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
  }

  @Test(expected = NullPointerException.class)
  public void testSetPredicateNullThrows() {
    initProperty();
    property.setPredicate(null);
  }

  @Test
  public void testSetPredicateMaskHigh() {
    initProperty();
    RangePredicate<Integer> replacement = new RangePredicate<>(7, 10);
    replacement.validate();
    property.setPredicate(replacement);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test
  public void testSetPredicateMaskLow() {
    initProperty();
    RangePredicate<Integer> replacement = new RangePredicate<>(0, 3);
    replacement.validate();
    property.setPredicate(replacement);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 3)));
  }

  @Test
  public void testVetoableChangePredicateNoChange() throws PropertyVetoException {
    initProperty();
    RangePredicate<Integer> replacement = new RangePredicate<>(3, 7);
    replacement.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, replacement));
    assertEquals(5, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
  }

  @Test
  public void testVetoableChangePredicateMasksLow() throws PropertyVetoException {
    initProperty();
    RangePredicate<Integer> replacement = new RangePredicate<>(0, 3);
    replacement.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, replacement));
    assertEquals(3, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 3)));
  }

  @Test
  public void testVetoableChangePredicateMasksHigh() throws PropertyVetoException {
    initProperty();
    RangePredicate<Integer> replacement = new RangePredicate<>(7, 10);
    replacement.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, replacement));
    assertEquals(7, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangePredicateNullThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, null));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangePredicateNonRangePredicateThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", range, new NonNullPredicate()));
  }

  @Test
  public void testVetoableChangeValueHappyCase() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "value", 5, 7));
    assertEquals(7, (int) property.getValue());
  }

  @Test
  public void testVetoableChangeValueMaskedHigh() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "value", 5, 15));
    assertEquals(10, (int) property.getValue());
  }

  @Test
  public void testVetoableChangeValueMaskedLow() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "value", 5, -5));
    assertEquals(0, (int) property.getValue());
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeValueNullThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "value", 5, null));
  }

  @Test
  public void testVetoableChangeNothingDoesNothing() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "nothing", null, 5));
  }
}
