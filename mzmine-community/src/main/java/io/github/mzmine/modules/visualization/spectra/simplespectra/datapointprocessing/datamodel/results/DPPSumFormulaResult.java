/*
 * Copyright (c) 2004-2022 The MZmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.mzmine.modules.visualization.spectra.simplespectra.datapointprocessing.datamodel.results;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DPPSumFormulaResult extends DPPResult<String> {

  private final double ppm;
  private final double score;

  private static final NumberFormat format = new DecimalFormat("0.00");

  public DPPSumFormulaResult(String formula, double ppm) {
    super(formula);
    this.ppm = ppm;
    this.score = 0.f;
  }

  public DPPSumFormulaResult(String formula, double ppm, double score) {
    super(formula);
    this.ppm = ppm;
    this.score = score;
  }

  @Override
  public String toString() {
    String strScore = ", -";
    if (score != 0.0d)
      strScore = ", " + format.format(score * 100.0) + " %";
    return getValue() + " (Δ " + format.format(ppm) + " ppm" + strScore + ")";
  }

  public double getPpm() {
    return ppm;
  }

  public double getScore() {
    return score;
  }

  @Override
  public ResultType getResultType() {
    return ResultType.SUMFORMULA;
  }
}
