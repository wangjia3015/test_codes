package com.example.DataBaseGenerator.tools;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;

/**
 * Created by wangjia-s on 14-11-4.
 */
public class FileHelper {
    public static String loadFromAssets(Context context, String fileName) {
        AssetManager manager = context.getAssets();
        try {
            manager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();

        }
        // TODO
        return null;
    }
}
