/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.Transient;
import java.util.ArrayList;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * @author achelian
 * @param <T>
 */
@EqualsAndHashCode(callSuper = false, exclude = {"propertyChangeSupport"})
public class CompositeNumericProperty<T extends Number> implements Property<T> {
  @Delegate
  final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  @Getter(onMethod = @_(@Transient))
  T value;
  @Getter
  Accumulator<T> accumulator;
  List<Property<T>> components = emptyList();
  @Getter
  boolean creationMode = true;

  public void setAccumulator(@NonNull Accumulator<T> value) {
    Accumulator<T> oldAccumulator = accumulator;
    accumulator = value;
    if (!isCreationMode()) {
      firePropertyChange("accumulator", oldAccumulator, accumulator);
    }
    if (!isCreationMode()) {
      T oldValue = this.value;
      this.value = accumulator.accumulate(components.stream().map((Property<T> p) -> p.getValue()));
      firePropertyChange("value", oldValue, this.value);
    }
  }

  public List<Property<T>> getComponents() {
    if (components.isEmpty()) {
      return emptyList();
    }
    else {
      return new ArrayList<>(components);
    }
  }

  public void setComponents(@NonNull List<Property<T>> value) {
    List<Property<T>> oldComponents = components;
    if (value.isEmpty()) {
      components = emptyList();
    }
    else {
      components = new ArrayList<>(value);
    }
    if (!creationMode) {
      firePropertyChange("components", oldComponents, value);
    }
    if (!creationMode) {
      T oldValue = this.value;
      this.value = accumulator.accumulate(components.stream().map((Property<T> v) -> v.getValue()));
      firePropertyChange("value", oldValue, this.value);
    }
  }

  @Override
  public void setValue(T value) {
    throw new UnsupportedOperationException("Cannot directly set the value of a composite property");
  }

  @PostConstruct
  @Override
  public void validate() {
    if (accumulator == null) {
      throw new IllegalStateException("accumulator cannot be null");
    }
    if (components == null) {
      throw new IllegalStateException("components cannot be null");
    }
    value = accumulator.accumulate(components.stream().map((Property<T> v) -> v.getValue()));
    creationMode = false;
  }

  @Override
  @SuppressWarnings("UnnecessaryReturnStatement")
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    switch (evt.getPropertyName()) {
      case "value": {
        throw new PropertyVetoException("cannot set value directly", evt);
      }
      case "components": {
        if (evt.getNewValue() == null) {
          throw new PropertyVetoException("cannot set components to null", evt);
        }
        if (evt.getNewValue() instanceof List) {
          setComponents((List<Property<T>>) evt.getNewValue());
        }
        else {
          throw new PropertyVetoException("components must be list", evt);
        }
        return;
      }
      case "accumulator": {
        if (evt.getNewValue() == null) {
          throw new PropertyVetoException("cannot set accumulator to null", evt);
        }
        if (evt.getNewValue() instanceof Accumulator) {
          setAccumulator((Accumulator<T>) evt.getNewValue());
        }
        else {
          throw new PropertyVetoException("accumulator is not the right class", evt);
        }
        return;
      }
    }
  }
  
}
