/*
 * Copyright 2006-2020 The MZmine Development Team
 *
 * This file is part of MZmine.
 *
 * MZmine is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MZmine; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.gui.chartbasics.chartthemes;

/**
 * @author SteffenHeu steffen.heuckeroth@gmx.de / steffen.heuckeroth@uni-muenster.de
 */
@FunctionalInterface
public interface LabelColorMatch {

  /**
   * If enabled the color of the labels in a plot will be matched to the color of the features set.
   * Useful in plots with several features sets.
   *
   * @param matchColor true or false
   */
  public void setLabelColorMatch(boolean matchColor);
}
