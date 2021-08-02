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

package io.github.mzmine.modules.dataprocessing.group_metacorrelate.corrgrouping;


import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.datamodel.features.ModularFeatureList;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.taskcontrol.Task;
import io.github.mzmine.util.ExitCode;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class CorrelateGroupingModule implements MZmineProcessingModule {

  private static final String NAME = "metaCorrelate";

  private static final String DESCRIPTION =
      "This method correlates and groups feature list rows based on multiple criteria (feature shape, intensity across samples, ...) ";

  @Override
  public @NotNull
  String getName() {
    return NAME;
  }

  @Override
  public @NotNull
  String getDescription() {

    return DESCRIPTION;
  }

  @Override
  public @NotNull
  MZmineModuleCategory getModuleCategory() {
    return MZmineModuleCategory.FEATURE_GROUPING;
  }

  @Override
  public @NotNull
  Class<? extends ParameterSet> getParameterSetClass() {
    return CorrelateGroupingParameters.class;
  }

  @Override
  @NotNull
  public ExitCode runModule(@NotNull MZmineProject project, @NotNull final ParameterSet parameters,
      @NotNull final Collection<Task> tasks) {

    ModularFeatureList[] featureLists = parameters
        .getParameter(CorrelateGroupingParameters.PEAK_LISTS).getValue().getMatchingFeatureLists();
    for (ModularFeatureList pkl : featureLists) {
      tasks.add(new CorrelateGroupingTask(project, parameters, pkl));
    }

    return ExitCode.OK;
  }
}
