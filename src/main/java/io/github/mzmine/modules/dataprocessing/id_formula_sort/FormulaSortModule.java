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
package io.github.mzmine.modules.dataprocessing.id_formula_sort;

import io.github.mzmine.datamodel.features.FeatureList;
import java.util.Collection;
import javax.annotation.Nonnull;
import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.taskcontrol.Task;
import io.github.mzmine.util.ExitCode;

public class FormulaSortModule implements MZmineProcessingModule {

  private static final String MODULE_NAME = "Sort formulas";
  private static final String MODULE_DESCRIPTION =
      "This module sorts all formula lists of rows in a feature list";

  @Override
  public @Nonnull String getName() {
    return MODULE_NAME;
  }

  @Override
  public @Nonnull MZmineModuleCategory getModuleCategory() {
    return MZmineModuleCategory.IDENTIFICATION;
  }

  @Override
  public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
    return FormulaSortParameters.class;
  }

  @Override
  public @Nonnull String getDescription() {
    return MODULE_DESCRIPTION;
  }

  @Override
  public @Nonnull ExitCode runModule(@Nonnull MZmineProject project,
      @Nonnull ParameterSet parameters, @Nonnull Collection<Task> tasks) {
    FeatureList featureLists[] =
        parameters.getParameter(FormulaSortParameters.FEATURE_LISTS).getValue().getMatchingFeatureLists();

    for (FeatureList featureList : featureLists) {
      Task newTask = new FormulaSortTask(featureList, parameters);
      tasks.add(newTask);
    }
    return ExitCode.OK;
  }
}
