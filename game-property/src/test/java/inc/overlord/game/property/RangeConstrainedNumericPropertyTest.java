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
public class RangeConstrainedNumericPropertyTest {
  RangeConstrainedNumericProperty<Integer> property;
  Range<Integer> range;
  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    property = new RangeConstrainedNumericProperty<>();
  }

  private void initProperty() {
    range = new Range<>(0, 10);
    range.validate();
    property.setConstraint(range);
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
    range = new Range<>(0, 10);
    range.validate();
    property.setConstraint(range);
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValdiateNullRange() {
    property.setValue(7);
    property.validate();
  }

  @Test
  public void testValidateHappyCase() {
    range = new Range<>(0, 10);
    range.validate();
    property.setValue(7);
    property.setConstraint(range);
    property.validate();
    assertEquals(7, (int) property.getValue());
    assertSame(range, property.getConstraint());
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
    Range<Integer> replacement = new Range<>(3, 7);
    property.setConstraint(replacement);
    assertEquals(5, (int) property.getValue());
    assertSame(replacement, property.getConstraint());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("constraint", range, replacement)));
  }

  @Test(expected = NullPointerException.class)
  public void testSetPredicateNullThrows() {
    initProperty();
    property.setConstraint(null);
  }

  @Test
  public void testSetPredicateMaskHigh() {
    initProperty();
    Range<Integer> replacement = new Range<>(7, 10);
    replacement.validate();
    property.setConstraint(replacement);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("constraint", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test
  public void testSetPredicateMaskLow() {
    initProperty();
    Range<Integer> replacement = new Range<>(0, 3);
    replacement.validate();
    property.setConstraint(replacement);
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("constraint", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 3)));
  }

  @Test
  public void testVetoableChangePredicateNoChange() throws PropertyVetoException {
    initProperty();
    Range<Integer> replacement = new Range<>(3, 7);
    replacement.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", range, replacement));
    assertEquals(5, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("constraint", range, replacement)));
  }

  @Test
  public void testVetoableChangePredicateMasksLow() throws PropertyVetoException {
    initProperty();
    Range<Integer> replacement = new Range<>(0, 3);
    replacement.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", range, replacement));
    assertEquals(3, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("constraint", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 3)));
  }

  @Test
  public void testVetoableChangePredicateMasksHigh() throws PropertyVetoException {
    initProperty();
    Range<Integer> replacement = new Range<>(7, 10);
    replacement.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", range, replacement));
    assertEquals(7, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("constraint", range, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 5, 7)));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangePredicateNullThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", range, null));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangePredicateNonRangePredicateThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", range, new NonNullPredicate()));
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

  @Test
  public void testEqualsAndHashCode() {
    initProperty();
    RangeConstrainedNumericProperty<Integer> property2 = new RangeConstrainedNumericProperty<>();
    Range<Integer> range2 = new Range<>(0, 10);
    range2.validate();
    property2.setConstraint(range2);
    property2.setValue(5);
    property2.validate();
    assertTrue(property.equals(property2));
    assertTrue(property2.equals(property));
    assertEquals(property.hashCode(), property2.hashCode());
    range2.setMax(8);
    assertFalse(property.equals(property2));
    assertFalse(property2.equals(property));
  }
}
