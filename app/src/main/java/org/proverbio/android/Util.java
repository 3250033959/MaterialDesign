package org.proverbio.android;


import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;


/**
 * @author Juan Pablo Proverbio <proverbio@nowcreatives.co>
 */
public class Util
{
    private Util()
    {
        super();
    }

    /**
     * Gets the max columns number that fits within screen and required width
     * @param context
     * @param width
     * @return
     */
    public static int getMaxColumnsForScreen(AppCompatActivity context, int width)
    {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density  = context.getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        return Math.round(dpWidth/width);
    }
}
