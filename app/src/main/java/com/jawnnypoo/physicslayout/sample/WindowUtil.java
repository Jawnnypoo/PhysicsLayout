package com.jawnnypoo.physicslayout.sample;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.SensorEvent;
import android.view.Display;
import android.view.Surface;
import android.view.Window;

/**
 * Silly util for window stuff
 * Created by Jawn on 7/15/2015.
 */
public class WindowUtil {

    /**
     * Locks the activity to the orientation the user was in when they entered the activity.
     * Call in Activity.onCreate
     * Android, why you no have this?
     * @param activity activity to lock
     */
    public static void lockToCurrentOrientation(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int height;
        int width;
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;
        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
                break;
            case Surface.ROTATION_180:
                if (height > width) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
                else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
            case Surface.ROTATION_270:
                if (width > height) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            default :
                if (height > width) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
        }
    }

    public static void normalizeForOrientation(Window window, SensorEvent event) {
        Display display = window.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int height;
        int width;
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;
        float x = 0;
        float y = 0;
        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height) {
                    //landscape
                    x = -event.values[1];
                    y = event.values[0];
                }
                else {
                    //reverse portrait
                    x = -event.values[0];
                    y = -event.values[1];
                }
                break;
            case Surface.ROTATION_180:
                if (height > width) {
                    //reverse portrait
                    x = -event.values[0];
                    y = -event.values[1];
                }
                else {
                    //reverse landscape
                    x = event.values[1];
                    y = -event.values[0];
                }
                break;
            case Surface.ROTATION_270:
                if (width > height) {
                    //reverse landscape
                    x = event.values[1];
                    y = -event.values[0];
                }
                else {
                    //portrait
                    x = event.values[0];
                    y = event.values[1];
                }
                break;
            default :
                if (height > width) {
                    //portrait
                    x = event.values[0];
                    y = event.values[1];
                }
                else {
                    //landscape
                    x = -event.values[1];
                    y = event.values[0];
                }
        }
        event.values[0] = x;
        event.values[1] = y;
    }
}
