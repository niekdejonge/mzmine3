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

package io.github.mzmine.modules.dataprocessing.id_spectral_library_match;

import io.github.mzmine.datamodel.features.FeatureList;
import io.github.mzmine.datamodel.features.FeatureListRow;
import io.github.mzmine.datamodel.features.SimpleFeatureListAppliedMethod;
import io.github.mzmine.datamodel.features.types.DataTypes;
import io.github.mzmine.datamodel.features.types.annotations.SpectralLibraryMatchesType;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.taskcontrol.TaskStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

class SpectralLibrarySearchTask extends RowsSpectralMatchTask {

  private static final Logger logger = Logger.getLogger(SpectralLibrarySearchTask.class.getName());
  private final FeatureList[] featureLists;

  public SpectralLibrarySearchTask(ParameterSet parameters, FeatureList[] featureLists,
      @NotNull Instant moduleCallDate) {
    super(parameters, combineRows(featureLists), moduleCallDate);
    this.featureLists = featureLists;
  }

  public static List<FeatureListRow> combineRows(FeatureList[] featureLists) {
    List<FeatureListRow> rows = new ArrayList<>();
    // add row type
    for (var flist : featureLists) {
      flist.addRowType(DataTypes.get(SpectralLibraryMatchesType.class));
      rows.addAll(flist.getRows());
    }
    return rows;
  }

  @Override
  public void run() {
    setStatus(TaskStatus.PROCESSING);

    logger.info(() -> String
        .format("Spectral library matching in %d feature lists (%d rows) against libraries: %s",
            featureLists.length, rows.size(), librariesJoined));

    // run the actual subtask
    super.run();

    // Add task description to peakList
    if (!isCanceled()) {
      for (var flist : featureLists) {
        flist.addDescriptionOfAppliedTask(new SimpleFeatureListAppliedMethod(
            "Spectral library matching with libraries: " + librariesJoined,
            SpectralLibrarySearchModule.class, parameters, getModuleCallDate()));
      }

      setStatus(TaskStatus.FINISHED);
    }
  }

}
