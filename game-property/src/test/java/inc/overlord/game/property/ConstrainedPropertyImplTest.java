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
import static org.mockito.Matchers.argThat;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 *
 * @author achelian
 */
public class ConstrainedPropertyImplTest {
  ConstrainedPropertyImpl<String, Constraint<String>> property;
  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    property = new ConstrainedPropertyImpl<>();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateDefault() {
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateNullConstraint() {
    property.setValue("ha");
    property.validate();
  }

  @Test
  public void testValidateNullValue() {
    property.setConstraint((String input) -> input);
    property.validate();
  }

  @Test
  public void testValidateHappyCase() {
    property.setConstraint((String input) -> input.toUpperCase());
    property.setValue("ha");
    property.validate();
    assertEquals("HA", property.getValue());
    assertEquals("ha", property.getUnconstrainedValue());
  }

  @Test
  public void testSetUnconstrainedValue() {
    property.setConstraint((String input) -> input.toUpperCase());
    property.setValue("ha");
    property.addPropertyChangeListener(listener);
    property.validate();
    property.setUnconstrainedValue("he");
    assertEquals("HE", property.getValue());
    assertEquals("he", property.getUnconstrainedValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", "HA", "HE")));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("unconstrainedValue", "ha", "he")));
  }

  @Test
  public void testVetoableChangeValueHappyCase() throws PropertyVetoException {
    property.setConstraint((String input) -> input.toUpperCase());
    property.setValue("ho");
    property.addPropertyChangeListener(listener);
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "value", null, "ha"));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("unconstrainedValue", "ho", "ha")));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", "HO", "HA")));
  }

  @Test
  public void testVetoableChangeConstraintHappyCase() throws PropertyVetoException {
    Constraint<String> original = (String input) -> input;
    property.setConstraint(original);
    property.setValue("ha");
    property.addPropertyChangeListener(listener);
    property.validate();
    Constraint<String> replacement = (String input) -> input.toUpperCase();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", original, replacement));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("constraint", original, replacement)));
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", "ha", "HA")));
    assertEquals("ha", property.getUnconstrainedValue());
    assertEquals("HA", property.getValue());
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeConstraintNull() throws PropertyVetoException {
    Constraint<String> original = (String input) -> input;
    property.setConstraint(original);
    property.setValue("ha");
    property.addPropertyChangeListener(listener);
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", original, null));
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoableChangeConstraintNonConstraint() throws PropertyVetoException {
    Constraint<String> original = (String input) -> input;
    property.setConstraint(original);
    property.setValue("ha");
    property.addPropertyChangeListener(listener);
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "constraint", original, 5));
  }

  @Test
  public void testVetoableChangeNothingDoesNothing() throws PropertyVetoException {
    property.setConstraint((String input) -> input);
    property.setValue("ha");
    property.addPropertyChangeListener(listener);
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "nothing", null, 5));
    verifyZeroInteractions(listener);
  }
}
