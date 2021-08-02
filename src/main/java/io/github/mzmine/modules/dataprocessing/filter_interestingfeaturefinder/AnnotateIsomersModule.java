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

package io.github.mzmine.modules.dataprocessing.filter_interestingfeaturefinder;

import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.taskcontrol.Task;
import io.github.mzmine.util.ExitCode;
import io.github.mzmine.util.MemoryMapStorage;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnnotateIsomersModule implements MZmineProcessingModule {

  @Override
  public @NotNull String getName() {
    return "Annotate possible isomers";
  }

  @Override
  public @Nullable Class<? extends ParameterSet> getParameterSetClass() {
    return AnnotateIsomersParameters.class;
  }

  @Override
  public @NotNull String getDescription() {
    return "Annotates features with the same (or similar) m/z but different mobilities within a "
        + "specified retention time window.";
  }

  @Override
  public @NotNull ExitCode runModule(@NotNull MZmineProject project,
      @NotNull ParameterSet parameters, @NotNull Collection<Task> tasks) {
    final MemoryMapStorage storage = MemoryMapStorage.forFeatureList();

    for (var flist : parameters.getParameter(AnnotateIsomersParameters.featureLists).getValue()
        .getMatchingFeatureLists()) {
      tasks.add(new AnnotateIsomersTask(storage, project, parameters, flist));
    }

    return ExitCode.OK;
  }

  @Override
  public @NotNull MZmineModuleCategory getModuleCategory() {
    return MZmineModuleCategory.FEATURE_GROUPING;
  }
}
