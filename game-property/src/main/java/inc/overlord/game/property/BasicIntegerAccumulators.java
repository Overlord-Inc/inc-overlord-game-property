/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inc.overlord.game.property;

import java.beans.Transient;
import java.util.function.BinaryOperator;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * @author achelian
 */
public enum BasicIntegerAccumulators implements Accumulator<Integer> {
  SUM(Integer::sum, 0),
  MAX(Integer::max, Integer.MIN_VALUE),
  MIN(Integer::min, Integer.MAX_VALUE),
  PRODUCT((first, second) -> first * second, 1),
  COUNT((acc, value) -> ++acc, 0);

  @Getter(onMethod = @__(@Transient))
  private final BinaryOperator<Integer> operator;
  private final int startingValue;

  private BasicIntegerAccumulators(@NonNull BinaryOperator<Integer> operation, final int startingValue) {
    this.operator = operation;
    this.startingValue = startingValue;
  }

  @Transient
  @Override
  public Integer getIdentity() {
    return startingValue;
  }
}
