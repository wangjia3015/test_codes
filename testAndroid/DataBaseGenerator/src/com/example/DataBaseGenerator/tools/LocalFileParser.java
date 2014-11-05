package com.example.DataBaseGenerator.tools;

import android.text.TextUtils;
import com.example.DataBaseGenerator.info.AppInfo;
import com.example.DataBaseGenerator.info.RootPathInfo;
import com.example.DataBaseGenerator.info.SubPathInfo;
import com.example.DataBaseGenerator.info.TrashInfoDesc;

/**
 * Created by wangjia-s on 14-11-4.
 */
public class LocalFileParser {

/**
        示例：1;5;100tv播放器;100tv;com.fone.player,com.fone.playera;2;2;1;5
        第一位：1表示根路径
        第二位：软件id，根路径表的主键，唯一
        第三位：软件名
        第四位：软件根目录
        第五位：包名，多个包名用逗号分隔
        第六位：软件类型，具体含义见下表
        第七位：文件类别，具体含义见下表
        第八位：清理建议，具体含义见下表
        第九位：删除建议id，具体含义见下表，对应去查询删除建议表
*/
    public static RootPathInfo parseRootPathLine(String line) {
        String[] params = line.split(";");
        if (params.length >= 9 && !isEmpty(params[2], params[3])) {
            RootPathInfo info = new RootPathInfo();
            info.appId = Integer.parseInt(params[1]);
            info.appName = params[2];
            info.appRootPath = params[3];
            info.pkgs = params[4].split("\\,");
            info.appType = Integer.parseInt(params[5]);
            info.fileType = Integer.parseInt(params[6]);
            //广告数据为2
            if (info.appType == 4) {
                info.clearType = 2;
            } else {
                info.clearType = Integer.valueOf(params[7]);
            }
            info.clearSuggestion = Integer.parseInt(params[8]);
            return info;
        }
        return null;
    }

    public static boolean isEmpty (String ... params ) {
        for (String param : params) {
            if (TextUtils.isEmpty(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 子路径数据：
     * 示例：2;34;2;.cache/images;4;3;1;1
     * 第一位：2表示子路径
     * 第二位：软件id，在子路径表中不唯一，一个软件可能对应多个子路径，表中有多个相同的软件id
     * 第三位：子路径id，一个软件中多个子路径的id，从1开始记
     * 第四位：子路径，*表示与根路径一致
     * 第五位：子路径描述id，对应去查询子路径描述表
     * 第六位：文件类别：具体含义见下表
     * 第七位：清理建议：具体含义见下表
     * 第八位：删除建议id，具体含义见下表，对应去查询删除建议表
     */
    public static SubPathInfo parseSubPath(String line) {

        if (TextUtils.isEmpty(line) || line.startsWith("#")) {
            return null;
        }

        String[] params = line.split(";");
        if (params.length >= 8 && !isEmpty(params[3], params[4])) {
            SubPathInfo info = new SubPathInfo();
            info.appId = Integer.parseInt(params[1]);
            info.subId = Integer.parseInt(params[2]);
            info.subPath = params[3];
            info.subPathDescId = Integer.parseInt(params[4]);
            info.fileType = Integer.parseInt(params[5]);
            info.clearSuggestion = Integer.parseInt(params[6]);
            info.clearResidualType = Integer.parseInt(params[7]);
            return info;
        }
        return null;
    }

    /**
     * 子路径描述数据：
     示例：23;en@Icon cache|zh-CN@图标缓存|zh-TW@圖示緩存
     第一位：子路径描述id，唯一
     第二位：子路径具体描述，支持多语言（en@Icon cache|zh-CN@图标缓存|zh-TW@圖示緩存，解析时先根据|分隔符拿到所有语言的文案，再通过第一个@之前的字段判断语言，选出需要的语言）
     * */
    public static TrashInfoDesc parseTrashInfoDesc(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }

        String[] params = line.split(";");

        if (params.length >= 2 && !isEmpty(params[1])) {
            TrashInfoDesc info = new TrashInfoDesc();
            info.id = Integer.parseInt(params[0]);
            info.text = params[1];
            return info;
        }
        return null;
    }

    public static AppInfo parseAppInfo(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }

        String[] params = line.split(";");

        if (params.length >= 2 && !isEmpty(params[1])) {
            AppInfo info = new AppInfo();
            info.appId = Integer.parseInt(params[0]);
            info.appName = params[1];
            return info;
        }
        return null;
    }

}
