package de.prototype74.neoinfo.utils;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;

public class CameraUtils {

    /**
     * Check if the back camera is working, just initialized or broken by reading 2 special camera
     * characteristics (orientation, max_zoom). The orientation should report 90, max_zoom a larger
     * value than 1. If one (or both) of the characteristics don't match, assume the camera
     * isn't properly working.
     *
     * @param context Context from an app activity
     * @return -1=camera doesn't work, 0=camera works, 1=camera initialized but may not work
     */
    public static int getBackCameraStatus(Context context) {
        int orientation = 0;
        float max_zoom = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // Camera API 2
            CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                for (String camID : cm.getCameraIdList()) {
                    CameraCharacteristics cc = cm.getCameraCharacteristics(camID);
                    if (cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                        orientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        max_zoom = cc.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                        break;
                    }
                }
            } catch (CameraAccessException ce) {
                ce.printStackTrace();
            }
        }
        else {  // (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) // legacy API
            try {
                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                    CameraInfo ci = new CameraInfo();
                    Camera.getCameraInfo(i, ci);
                    if (ci.facing == CameraInfo.CAMERA_FACING_BACK) {
                        orientation = ci.orientation;
                        Camera camera = Camera.open(i);
                        if (camera != null) {
                            Parameters params = camera.getParameters();
                            if (params.isZoomSupported())
                                max_zoom = params.getMaxZoom();
                            camera.release();
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (orientation == 90 && max_zoom > 1)
            return 0; // the back camera is initialized and ready to use
        else if (orientation == 90)
            return 1; // the camera is initialized but probably not usable
        return -1; // failed to initialize camera (camera not working)
    }
}
