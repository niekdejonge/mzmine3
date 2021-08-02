/*
 * Copyright 2006-2021 The MZmine Development Team
 *
 * This file is part of MZmine.
 *
 * MZmine is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MZmine; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package io.github.mzmine.modules.visualization.histogram;

import org.jfree.chart.axis.NumberAxis;

public class HistogramDomainAxis extends NumberAxis {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private int visibleCount;
  private double lowerTickValue, upperTickValue;

  public HistogramDomainAxis() {
    super();
  }

  public void setVisibleTickCount(int numOfTicks) {
    this.visibleCount = numOfTicks;
  }

  public void setLowerTickValue(double lowerTickValue) {
    this.lowerTickValue = lowerTickValue;
  }

  public void setUpperTickValue(double upperTickValue) {
    this.upperTickValue = upperTickValue;
  }

  @Override
  protected int calculateVisibleTickCount() {
    return this.visibleCount;
  }

  @Override
  protected double calculateLowestVisibleTickValue() {
    return this.lowerTickValue;
  }

  @Override
  protected double calculateHighestVisibleTickValue() {
    return this.upperTickValue;
  }

}
