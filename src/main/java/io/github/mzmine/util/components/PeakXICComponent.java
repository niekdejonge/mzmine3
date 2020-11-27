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

package io.github.mzmine.util.components;

import io.github.mzmine.datamodel.features.Feature;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import org.jfree.fx.FXGraphics2D;
import com.google.common.collect.Range;
import io.github.mzmine.datamodel.DataPoint;
import io.github.mzmine.datamodel.RawDataFile;
import javafx.scene.canvas.Canvas;

/**
 * Simple lightweight component for plotting peak shape
 */
public class PeakXICComponent extends Canvas {

  public static final Color XICColor = Color.blue;
  public static final Border componentBorder = BorderFactory.createLineBorder(Color.lightGray);

  private Feature peak;

  private Range<Float> rtRange;
  private double maxIntensity;

  /**
   * @param peak Picked peak to plot
   */
  public PeakXICComponent(Feature peak) {
    this(peak, peak.getRawDataPointsIntensityRange().upperEndpoint());
  }

  /**
   * @param peak Picked peak to plot
   */
  public PeakXICComponent(Feature peak, double maxIntensity) {

    this.peak = peak;

    // find features boundaries
    RawDataFile dataFile = peak.getRawDataFile();
    this.rtRange = dataFile.getDataRTRange();
    this.maxIntensity = maxIntensity;

    // this.setBorder(componentBorder);

    paint();

    widthProperty().addListener(e -> paint());
    heightProperty().addListener(e -> paint());

  }

  public void paint() {

    // use Graphics2D for antialiasing
    Graphics2D g2 = new FXGraphics2D(this.getGraphicsContext2D());

    // turn on antialiasing
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // get scan numbers, one features point per each scan
    RawDataFile dataFile = peak.getRawDataFile();
    Integer scanNumbers[] = peak.getScanNumbers().toArray(new Integer[0]);

    // If we have no features, just return
    if (scanNumbers.length == 0)
      return;

    // for each datapoint, find [X:Y] coordinates of its point in painted
    // image
    int xValues[] = new int[scanNumbers.length];
    int yValues[] = new int[scanNumbers.length];

    // find one datapoint with maximum intensity in each scan
    for (int i = 0; i < scanNumbers.length; i++) {

      double dataPointIntensity = 0;
      DataPoint dataPoint = peak.getDataPoint(scanNumbers[i]);

      if (dataPoint != null)
        dataPointIntensity = dataPoint.getIntensity();

      // get retention time (X value)
      double retentionTime = dataFile.getScan(scanNumbers[i]).getRetentionTime();

      // calculate [X:Y] coordinates
      final double rtLen = rtRange.upperEndpoint() - rtRange.lowerEndpoint();
      xValues[i] = (int) Math
          .floor((retentionTime - rtRange.lowerEndpoint()) / rtLen * ((int) getWidth() - 1));
      yValues[i] = (int) getHeight()
          - (int) Math.floor(dataPointIntensity / maxIntensity * ((int) getHeight() - 1));

    }

    // create a path for a peak polygon
    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    path.moveTo(xValues[0], (int) getHeight() - 1);

    // add features points to the path
    for (int i = 0; i < (xValues.length - 1); i++) {
      path.lineTo(xValues[i + 1], yValues[i + 1]);
    }
    path.lineTo(xValues[xValues.length - 1], (int) getHeight() - 1);

    // close the path to form a polygon
    path.closePath();

    // fill the peak area
    g2.setColor(XICColor);
    g2.fill(path);

  }

}
