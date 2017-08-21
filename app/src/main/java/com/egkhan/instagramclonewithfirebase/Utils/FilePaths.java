package com.egkhan.instagramclonewithfirebase.Utils;

import android.os.Environment;

/**
 * Created by EgK on 8/20/2017.
 */

public class FilePaths {

    //storage/emulated/0
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String PICTURES = ROOT_DIR + "/Pictures";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}

