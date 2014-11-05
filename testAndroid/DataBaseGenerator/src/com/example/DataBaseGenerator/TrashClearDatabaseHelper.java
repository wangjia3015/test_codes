package com.example.DataBaseGenerator;


public class TrashClearDatabaseHelper {

    public static final String LOCAL_DATA_DB_NAME = "t_c_c_d_l";
    public static final String LOCAL_DATA_DB_NAME_GZIP = "t_c_c_d_l.gzip";
    public static final String LOCAL_DATA_DB_TEMP = "t_c_c_d_t";
    public static final String ADD_DATA_DB_NAME = "t_c_c_d_a";
    public static final String ADD_DATA_DB_NAME_GZIP = "t_c_c_d_a.gzip";
    public static final String DELETE_DATA_DB_NAME = "t_c_c_d_d";
    public static final String DELETE_DATA_DB_NAME_GZIP = "t_c_c_d_d.gzip";
    public static final String UPDATE_DATA_DB_NAME = "t_c_c_d_u";
    public static final String UPDATE_DATA_DB_NAME_GZIP = "t_c_c_d_u.gzip";
    public static final int DB_VERSION = 1;

    /** 根路径数据表 */
    public static final String TABLE_ROOT_PATH = "t_r_p";
    //软件id
    public static final String ROOT_COLUMN_APP_ID = "r_a_i";
    //软件名称
    public static final String ROOT_COLUMN_APP_NAME = "r_a_n";
    //软件根路径
    public static final String ROOT_COLUMN_ROOT_PATH = "r_r_p";
    //软件根路径前缀，只包含一级目录，/sdcard/Android/data/xxx特殊处理
    public static final String ROOT_COLUMN_ROOT_FIST_PATH = "r_r_f_p";
    //软件类型
    public static final String ROOT_COLUMN_APP_TYPE = "r_a_t";
    //文件类别
    public static final String ROOT_COLUMN_FILE_TYPE = "r_f_t";
    //清理类型（0：未知 1：建议保留 2：一键清理）
    public static final String ROOT_COLUMN_CLEAR_TYPE = "r_c_t";
    //清理建议 0:无建议 1:重新加载，消耗流量 2:无法恢复 3:无法使用 4:无法收听 5:无法观看 6:无法浏览 7:无法查看 8:程序崩溃 9:网络异常
    public static final String ROOT_COLUMN_CLEAR_SUGGESTION = "r_c_s";

    /** 子路径数据表 */
    public static final String TABLE_SUB_PATH = "t_s_p";
    //软件id
    public static final String SUB_COLUMN_APP_ID = "s_a_i";
    //子路径id
    public static final String SUB_COLUMN_SUB_ID = "s_s_i";
    //子路径
    public static final String SUB_COLUMN_SUB_PATH = "s_s_p";
    //子路径描述
    public static final String SUB_COLUMN_SUB_PATH_DESCRIPTION = "s_s_p_d";
    //文件类别
    public static final String SUB_COLUMN_FILE_TYPE = "s_f_t";
    //清理类型（0：未知 1：建议保留 2：一键清理）
    public static final String SUB_COLUMN_CLEAR_TYPE = "s_c_t";
    //清理建议 0:无建议 1:重新加载，消耗流量 2:无法恢复 3:无法使用 4:无法收听 5:无法观看 6:无法浏览 7:无法查看
    public static final String SUB_COLUMN_CLEAR_SUGGESTION = "s_c_s";
    //卸载残留清理类型 0：未知 1：建议保留 2：一键清理
    public static final String SUB_COLUMN_CLEAR_RESIDUAL_TYPE = "s_c_r_t";

    /** 软件包名表 */
    public static final String TABLE_APP_PKGNAME = "t_a_p";
    //软件id
    public static final String PKG_COLUMN_APP_ID = "p_a_i";
    //软件包名
    public static final String PKG_COLUMN_APP_PKGNAME = "p_a_p";



    /** id对应的app 信息表 */
    public static final String TABLE_APP_INFO = "t_a_i";
    //软件id
    public static final String AP_COLUMN_APP_ID = "a_a_i";
    //应用名称
    public static final String AP_APP_NAME = "a_a_n";


    /** 子路径描述表 */
    public static final String TABLE_SUB_PATH_DESCRIPTION = "t_s_p_d";
    //子路径描述id
    public static final String DESC_COLUMN_DESC_ID = "d_d_i";
    //子路径描述
    public static final String DESC_COLUMN_SUP_PATH_DESCRIPTION = "d_s_p_d";

}
