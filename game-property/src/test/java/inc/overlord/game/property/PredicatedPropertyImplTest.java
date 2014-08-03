/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import static java.util.Collections.singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
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
public class PredicatedPropertyImplTest {
  PredicatedPropertyImpl<String, Predicate<String>> property;
  @Mock PropertyChangeListener listener;

  @Before
  public void setUp() {
    initMocks(this);
    property = new PredicatedPropertyImpl<>();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidateNoPredicate() {
    property.validate();
  }

  @Test(expected = IllegalStateException.class)
  public void testValidatePredicateFails() {
    property.setPredicate((String) -> false);
    property.validate();
  }

  @Test
  public void testSetPredicateNull() {
    Predicate<String> predicate = (String) -> true;
    property.setPredicate(predicate);
    property.addPropertyChangeListener(listener);
    property.setPredicate(null);
    assertSame(predicate, property.getPredicate());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testSetPredicateToFalse() {
    Predicate<String> predicate = (String) -> true;
    property.setPredicate(predicate);
    property.addPropertyChangeListener(listener);
    property.validate();
    property.setPredicate((String) -> false);
    assertSame(predicate, property.getPredicate());
    verifyZeroInteractions(listener);
  }

  @Test
  public void testSetValueHappyCase() {
    property.setValue("foo");
    property.setPredicate((String) -> true);
    property.addPropertyChangeListener(listener);
    property.validate();
    property.setValue("bar");
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", "foo", "bar")));
    verifyNoMoreInteractions(listener);
    assertEquals("bar", property.getValue());
  }

  @Test
  public void testSetValueFailsPredicate() {
    property.setValue("foo");
    property.setPredicate((String t) -> "foo".equals(t));
    property.addPropertyChangeListener(listener);
    property.validate();
    property.setValue("bar");
    verifyZeroInteractions(listener);
    assertEquals("foo", property.getValue());
  }

  @Test
  public void testValidateHappyCase() {
    property.setPredicate((String) -> true);
    property.validate();
  }

  @Test
  public void testVetoValueHappyCase() throws PropertyVetoException {
    property.setPredicate((String) -> true);
    property.setValue("foo");
    property.addPropertyChangeListener(listener);
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "value", "foo", "bar"));
    assertEquals("bar", property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("value", "foo", "bar")));
    verifyNoMoreInteractions(listener);
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoNullPredicateThrows() throws PropertyVetoException {
    property.setPredicate((String) -> true);
    property.setValue("foo");
    property.validate();
    property.addPropertyChangeListener(listener);
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", "foo", null));
    verifyZeroInteractions(listener);
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoValueFailsPredicateThrows() throws PropertyVetoException {
    property.setPredicate((String t) -> "foo".equals(t));
    property.setValue("foo");
    property.validate();
    property.addPropertyChangeListener(listener);
    property.vetoableChange(new PropertyChangeEvent(this, "value", "foo", "bar"));
    verifyZeroInteractions(listener);
  }

  @Test(expected = PropertyVetoException.class)
  public void testVetoPredicateWillFailThrows() throws PropertyVetoException {
    property.setPredicate((String) -> true);
    property.setValue("foo");
    property.validate();
    property.addPropertyChangeListener(listener);
    Predicate<String> predicate = (String) -> false;
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", property.getPredicate(), predicate));
    verifyZeroInteractions(listener);
  }

  @Test
  public void testVetoPredicateHappyCase() throws PropertyVetoException {
    Predicate<String> oldPredicate = (String) -> true;
    property.setPredicate(oldPredicate);
    property.setValue("foo");
    property.validate();
    property.addPropertyChangeListener(listener);
    Predicate<String> predicate = (String t) -> "foo".equals(t);
    property.vetoableChange(new PropertyChangeEvent(this, "predicate", property.getPredicate(), predicate));
    assertSame(predicate, property.getPredicate());
    assertEquals("foo", property.getValue());
    verify(listener).propertyChange(argThat(new PropertyChangeEvtGenericMatcher("predicate", oldPredicate, predicate)));
    verifyNoMoreInteractions(listener);
  }

  @Test
  public void testVetoableChangeNothing() throws PropertyVetoException {
    property.setPredicate((String) -> true);
    property.setValue("foo");
    property.validate();
    property.vetoableChange(new PropertyChangeEvent(this, "nothing", null, 5));
    verifyZeroInteractions(listener);
  }

  @Test
  public void testEqualsAndHashCode() {
    SetMatchingPredicate<String> predicate = new SetMatchingPredicate<>();
    predicate.setMatches(singleton("ha"));
    predicate.validate();
    property.setPredicate(predicate);
    property.setValue("ha");
    property.validate();
    SetMatchingPredicate<String> predicate2 = new SetMatchingPredicate<>();
    predicate2.setMatches(singleton("ha"));
    predicate2.validate();
    PredicatedPropertyImpl<String, Predicate<String>> property2 = new PredicatedPropertyImpl<>();
    property2.setPredicate(predicate2);
    property2.setValue("ha");
    property2.validate();
    assertTrue(property.equals(property2));
    assertTrue(property2.equals(property));
    assertEquals(property.hashCode(), property2.hashCode());
    Set<String> setReplacement = new HashSet<>(predicate.getMatches());
    setReplacement.add("ho");
    predicate2.setMatches(setReplacement);
    assertFalse(property.equals(property2));
    assertFalse(property2.equals(property));
  }
}
