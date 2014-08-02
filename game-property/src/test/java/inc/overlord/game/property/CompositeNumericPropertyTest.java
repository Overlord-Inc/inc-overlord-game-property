/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;
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
public class CompositeNumericPropertyTest {
  CompositeNumericProperty<Integer> property;
  @Mock PropertyChangeListener listener;
  List<Property<Integer>> properties;

  @Before
  public void setUp() {
    initMocks(this);
    property = new CompositeNumericProperty<>();
  }

  private void initProperty() {
    properties = Arrays.asList((Property<Integer>) new ConstantProperty<>(5), (Property<Integer>) new ConstantProperty<>(6));
    property.setComponents(properties);
    property.setAccumulator(BasicIntegerAccumulators.SUM);
    property.addPropertyChangeListener(listener);
    property.validate();
  }
  @Test(expected = IllegalStateException.class)
  public void testValidateNullAccumulator() {
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateNullComponents() {
    // not naturally set
    property.components = null;
    property.validate();
  }

  @Test
  public void testValidateHappyCase() {
    property.setAccumulator(BasicIntegerAccumulators.SUM);
    property.validate();
    assertNotNull(property.getValue());
    assertEquals(0, (int) property.getValue());
    assertEquals(BasicIntegerAccumulators.SUM, property.getAccumulator());
    assertEquals(emptyList(), property.getComponents());
  }

  @Test(expected = NullPointerException.class)
  @SuppressWarnings("null")
  public void testSetAccumulatorNull() {
    property.setAccumulator(null);
  }

  @Test
  public void testSetAccumulatorChangeFunction() {
    initProperty();
    assertEquals(11, (int) property.getValue());
    property.setAccumulator(BasicIntegerAccumulators.PRODUCT);
    assertEquals(30, (int) property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("accumulator", BasicIntegerAccumulators.SUM, BasicIntegerAccumulators.PRODUCT)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 11, 30)));
  }

  @Test
  public void testSetComponentsHappyCase() {
    property.setAccumulator(BasicIntegerAccumulators.SUM);
    property.addPropertyChangeListener(listener);
    property.validate();
    properties = Arrays.asList((Property<Integer>) new ConstantProperty<>(5), (Property<Integer>) new ConstantProperty<>(6));
    property.setComponents(properties);
    assertEquals(properties, property.getComponents());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("components", emptyList(), properties)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 0, 11)));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetValueThrows() {
    initProperty();
    property.setValue(7);
  }

  @Test
  public void testVetoableChangeValueThrows() {
    initProperty();
    try {
      property.vetoableChange(new PropertyChangeEvent(this, "value", 11, 15));
      fail("Should have thrown exception");
    }
    catch (PropertyVetoException e) {
      //
    }
    assertEquals(11, (int) property.getValue());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testVetoableChangeAccumulatorHappyCase() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "accumulator", BasicIntegerAccumulators.SUM, BasicIntegerAccumulators.PRODUCT));
    assertEquals(30, (int) property.getValue());
    assertEquals(BasicIntegerAccumulators.PRODUCT, property.getAccumulator());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("accumulator", BasicIntegerAccumulators.SUM, BasicIntegerAccumulators.PRODUCT)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 11, 30)));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeAccumulatorNullThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "accumulator", BasicIntegerAccumulators.SUM, null));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeAccumulatorNonAccumulatorThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "accumulator", BasicIntegerAccumulators.SUM, 7));
  }

  @Test
  public void testVetoableChangeComponentsHappyCase() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "components", properties, emptyList()));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("components", properties, emptyList())));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", 11, 0)));
    assertEquals(0, (int) property.getValue());
    assertTrue(property.getComponents().isEmpty());
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeComponentsNullThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "components", properties, null));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeComponentsNonListThrows() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "components", properties, 7));
  }

  @Test
  public void testVetoableChangeNonPropertyDoesNothing() throws PropertyVetoException {
    initProperty();
    property.vetoableChange(new PropertyChangeEvent(this, "nothing", null, 7));
  }

  @Test(expected = IllegalStateException.class)
  public void testForceComponentsNullFailsValidation() {
    property.setAccumulator(BasicIntegerAccumulators.SUM);
    property.components = null;
    property.validate();
  }
}
