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
package io.github.mzmine.modules.dataprocessing.featdet_msn;

import org.apache.commons.lang3.ArrayUtils;
import io.github.mzmine.datamodel.features.ModularFeature;
import io.github.mzmine.datamodel.features.ModularFeatureList;
import io.github.mzmine.datamodel.features.ModularFeatureListRow;
import java.util.logging.Logger;
import com.google.common.collect.Range;
import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.datamodel.RawDataFile;
import io.github.mzmine.datamodel.Scan;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import io.github.mzmine.parameters.parametertypes.tolerances.RTTolerance;
import io.github.mzmine.taskcontrol.AbstractTask;
import io.github.mzmine.taskcontrol.TaskStatus;
import io.github.mzmine.util.FeatureUtils;


public class MsnPeakPickingTask extends AbstractTask {

  private final Logger logger = Logger.getLogger(this.getClass().getName());

  private int processedScans, totalScans;

  private final MZmineProject project;
  private final RawDataFile dataFile;
  private final ScanSelection scanSelection;
  private final int msLevel;
  private final MZTolerance mzTolerance;
  private final RTTolerance rtTolerance;

  private final ModularFeatureList newFeatureList;

  public MsnPeakPickingTask(MZmineProject project, RawDataFile dataFile, ParameterSet parameters) {

    this.project = project;
    this.dataFile = dataFile;

    scanSelection = parameters.getParameter(MsnPeakPickerParameters.scanSelection).getValue();
    msLevel = parameters.getParameter(MsnPeakPickerParameters.msLevel).getValue();
    mzTolerance = parameters.getParameter(MsnPeakPickerParameters.mzDifference).getValue();
    rtTolerance = parameters.getParameter(MsnPeakPickerParameters.rtTolerance).getValue();

    newFeatureList = new ModularFeatureList(dataFile.getName() + " MSn features", dataFile);
  }

  public RawDataFile getDataFile() {
    return dataFile;
  }

  @Override
  public double getFinishedPercentage() {
    if (totalScans == 0) {
      return 0f;
    }
    return (double) processedScans / totalScans;
  }

  @Override
  public String getTaskDescription() {
    return "Building MSn feature list based on MSn from " + dataFile;
  }

  @Override
  public void run() {

    setStatus(TaskStatus.PROCESSING);

    int[] totalMSLevel = dataFile.getMSLevels();

    // No MSn scan in datafile.
    if (!ArrayUtils.contains(totalMSLevel, msLevel)) {
      setStatus(TaskStatus.ERROR);
      final String msg = "No MS" + msLevel + " scans in " + dataFile.getName();
      setErrorMessage(msg);
      return;
    }

    final Scan scans[] = scanSelection.getMatchingScans(dataFile);
    totalScans = scans.length;

    // No scans in selection range.
    if (totalScans == 0) {
      setStatus(TaskStatus.ERROR);
      final String msg = "No scans detected in selection range for " + dataFile.getName();
      setErrorMessage(msg);
      return;
    }

    /**
     * Process each MS2 scan to find MSn scans through fragmentationScan tracing. If a MSn scan
     * found, build simple modular feature for MS2 precursor in range.
     */
    for (Scan scan : scans) {

      // Canceled?
      if (isCanceled()) {
        return;
      }

      // MSn scans will be found through MS2 fragmentScan linking.
      if (scan.getMSLevel() != 2) {
        processedScans++;
        continue;
      }

      // Does scan possess MSn scans?
      boolean validScan = false;

      // If MS2, true by default.
      if (scan.getMSLevel() == msLevel) {
        validScan = true;
      } else {

        // Search for MSn Scans.
        int[] scanList = getMSnScanNumbers(scan);

        if (scanList != null) {
          validScan = true;
        }
      }

      // If valid, build simple feature for precursor.
      if (validScan) {

        // Get ranges.
        float scanRT = scan.getRetentionTime();
        double precursorMZ = scan.getPrecursorMZ();

        Range<Float> rtRange = rtTolerance.getToleranceRange(scanRT);
        Range<Double> mzRange = mzTolerance.getToleranceRange(precursorMZ);

        // Build simple feature for precursor in ranges.
        ModularFeature newFeature =
            FeatureUtils.buildSimpleModularFeature(newFeatureList, dataFile, rtRange, mzRange);

        // Add feature to feature list.
        if (newFeature != null) {

          ModularFeatureListRow newFeatureListRow =
              new ModularFeatureListRow(newFeatureList, scan.getScanNumber(), dataFile, newFeature);

          newFeatureList.addRow(newFeatureListRow);
        }
      }

      processedScans++;
    }

    // No MSn features detected in range.
    if (newFeatureList.isEmpty()) {
      setStatus(TaskStatus.ERROR);
      final String msg =
          "No MSn precursor features detected in selected range for " + dataFile.getName();
      setErrorMessage(msg);
      return;
    }

    // Add new feature list to the project
    project.addFeatureList(newFeatureList);

    logger.info(
        "Finished MSn feature builder on " + dataFile + ", " + processedScans + " scans processed");

    setStatus(TaskStatus.FINISHED);
  }

  /**
   * Get scan numbers for input MS level.
   * 
   * @param scan
   * @return
   */
  private int[] getMSnScanNumbers(Scan scan) {

    int[] allLevels = dataFile.getMSLevels();

    // MS level not in data file.
    if (!ArrayUtils.contains(allLevels, msLevel)) {
      return null;
    }

    int[] fragmentScanNumbers = scan.getFragmentScanNumbers();

    // Recursively search fragment scans for all scan numbers at MS level.
    if (fragmentScanNumbers != null) {

      // Return MSn fragment scans numbers if they exist.
      if (scan.getMSLevel() + 1 == msLevel) {
        return fragmentScanNumbers;
      } else {

        // Array for all MSn scan numbers.
        int[] msnScanNumbers = {};

        // Recursively search fragment scan chain.
        for (int fScanNum : fragmentScanNumbers) {

          Scan fragmentScan = dataFile.getScan(fScanNum);

          int[] foundScanNumbers = getMSnScanNumbers(fragmentScan);

          if (foundScanNumbers != null) {

            msnScanNumbers = ArrayUtils.addAll(msnScanNumbers, foundScanNumbers);
          } else {
            return null;
          }
        }

        return msnScanNumbers;
      }
    }

    // No fragment scans found.
    return null;
  }

}
