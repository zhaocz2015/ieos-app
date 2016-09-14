
package com.wfzcx.ieos.module.dz_macro;

import android.content.Context;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {



    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {


    }

    @Override
    public int getXOffset(float xpos) {
        return -getWidth()/2;
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}
