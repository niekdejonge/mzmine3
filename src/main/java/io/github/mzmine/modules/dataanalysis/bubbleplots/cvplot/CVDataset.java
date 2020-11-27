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

package io.github.mzmine.modules.dataanalysis.bubbleplots.cvplot;

import io.github.mzmine.datamodel.features.Feature;
import io.github.mzmine.datamodel.features.FeatureList;
import io.github.mzmine.datamodel.features.FeatureListRow;
import java.util.Vector;
import java.util.logging.Logger;

import org.jfree.data.xy.AbstractXYZDataset;

import com.google.common.primitives.Doubles;

import io.github.mzmine.datamodel.RawDataFile;
import io.github.mzmine.modules.dataanalysis.bubbleplots.RTMZDataset;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.util.MathUtils;
import io.github.mzmine.util.FeatureMeasurementType;

public class CVDataset extends AbstractXYZDataset implements RTMZDataset {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private double[] xCoords = new double[0];
  private double[] yCoords = new double[0];
  private double[] colorCoords = new double[0];
  private FeatureListRow[] featureListRows = new FeatureListRow[0];

  private String datasetTitle;

  public CVDataset(FeatureList alignedFeatureList, ParameterSet parameters) {

    int numOfRows = alignedFeatureList.getNumberOfRows();

    RawDataFile selectedFiles[] = parameters.getParameter(CVParameters.dataFiles).getValue();
    FeatureMeasurementType measurementType =
        parameters.getParameter(CVParameters.measurementType).getValue();

    // Generate title for the dataset
    datasetTitle = "Correlation of variation analysis";
    datasetTitle = datasetTitle.concat(" (");
    if (measurementType == FeatureMeasurementType.AREA)
      datasetTitle = datasetTitle.concat("CV of feature areas");
    else
      datasetTitle = datasetTitle.concat("CV of feature heights");
    datasetTitle = datasetTitle.concat(" in " + selectedFiles.length + " files");
    datasetTitle = datasetTitle.concat(")");

    logger.finest("Computing: " + datasetTitle);

    // Loop through rows of aligned feature list
    Vector<Double> xCoordsV = new Vector<Double>();
    Vector<Double> yCoordsV = new Vector<Double>();
    Vector<Double> colorCoordsV = new Vector<Double>();
    Vector<FeatureListRow> featureListRowsV = new Vector<FeatureListRow>();

    for (int rowIndex = 0; rowIndex < numOfRows; rowIndex++) {

      FeatureListRow row = alignedFeatureList.getRow(rowIndex);

      // Collect available feature intensities for selected files
      Vector<Double> featureIntensities = new Vector<Double>();
      for (int fileIndex = 0; fileIndex < selectedFiles.length; fileIndex++) {
        Feature feature = row.getFeature(selectedFiles[fileIndex]);
        if (feature != null) {
          if (measurementType == FeatureMeasurementType.AREA)
            featureIntensities.add((double) feature.getArea());
          else
            featureIntensities.add((double) feature.getHeight());
        }
      }

      // If there are at least two measurements available for this feature
      // then calc CV and include this feature in the plot
      if (featureIntensities.size() > 1) {
        double[] ints = Doubles.toArray(featureIntensities);
        Double cv = MathUtils.calcCV(ints);

        Double rt = (double) row.getAverageRT();
        Double mz = row.getAverageMZ();

        xCoordsV.add(rt);
        yCoordsV.add(mz);
        colorCoordsV.add(cv);
        featureListRowsV.add(row);

      }

    }

    // Finally store all collected values in arrays
    xCoords = Doubles.toArray(xCoordsV);
    yCoords = Doubles.toArray(yCoordsV);
    colorCoords = Doubles.toArray(colorCoordsV);
    featureListRows = featureListRowsV.toArray(new FeatureListRow[0]);

  }

  public String toString() {
    return datasetTitle;
  }

  @Override
  public int getSeriesCount() {
    return 1;
  }

  @Override
  public Comparable<?> getSeriesKey(int series) {
    if (series == 0)
      return new Integer(1);
    else
      return null;
  }

  public Number getZ(int series, int item) {
    if (series != 0)
      return null;
    if ((colorCoords.length - 1) < item)
      return null;
    return colorCoords[item];
  }

  public int getItemCount(int series) {
    return xCoords.length;
  }

  public Number getX(int series, int item) {
    if (series != 0)
      return null;
    if ((xCoords.length - 1) < item)
      return null;
    return xCoords[item];
  }

  public Number getY(int series, int item) {
    if (series != 0)
      return null;
    if ((yCoords.length - 1) < item)
      return null;
    return yCoords[item];
  }

  public FeatureListRow getFeatureListRow(int item) {
    return featureListRows[item];
  }

}
