/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.util.function.BinaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author achelian
 * @param <T>
 */
public interface Accumulator<T> {
  String name();
  BinaryOperator<T> getOperator();
  T getIdentity();
  default T accumulate(Stream<T> stream) {
    return stream.reduce(getIdentity(), getOperator());
  }
}
