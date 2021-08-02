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

package io.github.mzmine.datamodel.features;

import io.github.mzmine.datamodel.features.types.DataType;
import io.github.mzmine.datamodel.features.types.LinkedDataType;

public class ListRowBinding implements RowBinding {

  private final LinkedDataType rowType;
  private final DataType featureType;

  public ListRowBinding(LinkedDataType rowType, DataType featureType) {
    this.rowType = rowType;
    this.featureType = featureType;
  }

  @Override
  public void apply(ModularFeatureListRow row) {
    row.streamFeatures().map(f -> f.get(featureType)).forEach(prop -> {
      prop.addListener((o, oldValue, newValue) -> {
        if (row.get(rowType) != null) {
          row.get(rowType).fireChangedEvent();
        }
      });
    });
  }

  @Override
  public DataType getRowType() {
    return rowType;
  }

  @Override
  public DataType getFeatureType() {
    return featureType;
  }
}
