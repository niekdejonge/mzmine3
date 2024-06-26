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

package io.github.mzmine.modules.io.import_rawdata_bruker_tdf.datamodel.callbacks;

import com.sun.jna.Pointer;
import java.util.HashMap;
import java.util.Map;

public class MultipleProfileData implements ProfileCallback {

  Map<Long, ProfileDataPoints> spectra = new HashMap<>();

  public class ProfileDataPoints {
    long precursorId;
    long num_points;
    float[] intensities;
    Pointer userData;
  }

  @Override
  public void invoke(long id, long num_points, Pointer pIntensites, Pointer userData) {
    ProfileDataPoints msms_spectrum = new ProfileDataPoints();
    msms_spectrum.precursorId = id;
    msms_spectrum.num_points = num_points;
    msms_spectrum.intensities = pIntensites.getFloatArray(0, (int) num_points);
    spectra.put(id, msms_spectrum);
  }
}
