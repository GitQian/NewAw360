package com.android.allwinner.aw360;

/**
 * Created by xiasj on 17-6-9.
 */

public class AW360Command {
    /**
     * 设置摄像头Sensor类型
     *
     * @see #SENSOR_TYPE_1089_OR_3089
     * @see #SENSOR_TYPE_1099_OR_1058
     */
    static final int AW_COMMAND_SET_SENSOR_TYPE = 0;

    /**
     * 设置想想头Lens类型
     *
     * @see #LENS_TYPE_HK8077
     * @see #LENS_TYPE_HAILIN4052
     */
    static final int AW_COMMAND_SET_LENS_TYPE = 1;

    /**
     * 设置现实模式
     */
    static final int AW_COMMAND_SET_DISPLAY_VIEW_MODE = 2;

    /**
     * 设置摄像头制式
     */
    static final int AW_COMMAND_SET_SYSTEM = 3;

    /**
     * 设置车宽
     */
    static final int AW_COMMAND_SET_CAR_WIDTH = 4;

    /**
     * 设置车长
     */
    static final int AW_COMMAND_SET_CAR_HEIGHT = 5;

    /**
     * 设置左右偏移
     */
    static final int AW_COMMAND_SET_SIDE_OFFSET = 6;

    /**
     * 设置前置摄像头镜像
     */
    static final int AW_COMMAND_SET_FRONT_CAMERA_MIRROR = 7;

    /**
     * 设置后置摄像头镜像
     */
    static final int AW_COMMAND_SET_REAR_CAMERA_MIRROR = 8;

    /**
     * 设置左置摄像头镜像
     */
    static final int AW_COMMAND_SET_LEFT_CAMERA_MIRROR = 9;

    /**
     * 设置右置摄像头镜像
     */
    static final int AW_COMMAND_SET_RIGHT_CAMERA_MIRROR = 10;

    /**
     * 设置前摄像头横向偏移
     */
    static final int AW_COMMAND_SET_FRONT_CAMERA_OFFSETX = 11;
    /**
     * 设置后摄像头横向偏移
     */
    static final int AW_COMMAND_SET_REAR_CAMERA_OFFSETX = 12;
    /**
     * 设置左摄像头横向偏移
     */
    static final int AW_COMMAND_SET_LEFT_CAMERA_OFFSETX = 13;
    /**
     * 设置右摄像头横向偏移
     */
    static final int AW_COMMAND_SET_RIGHT_CAMERA_OFFSETX = 14;
    /**
     * 设置前摄像头纵向偏移
     */
    static final int AW_COMMAND_SET_FRONT_CAMERA_OFFSETY = 15;
    /**
     * 设置后摄像头纵向偏移
     */
    static final int AW_COMMAND_SET_REAR_CAMERA_OFFSETY = 16;
    /**
     * 设置左摄像头纵向偏移
     */
    static final int AW_COMMAND_SET_LEFT_CAMERA_OFFSETY = 17;
    /**
     * 设置右摄像头纵向偏移
     */
    static final int AW_COMMAND_SET_RIGHT_CAMERA_OFFSETY = 18;
    /**
     * 设置前摄像头缩放
     */
    static final int AW_COMMAND_SET_FRONT_CAMERA_SCALE = 19;
    /**
     * 设置后摄像头缩放
     */
    static final int AW_COMMAND_SET_REAR_CAMERA_SCALE = 20;
    /**
     * 设置左摄像头缩放
     */
    static final int AW_COMMAND_SET_LEFT_CAMERA_SCALE = 21;
    /**
     * 设置右摄像头缩放
     */
    static final int AW_COMMAND_SET_RIGHT_CAMERA_SCALE = 22;
    /**
     * 设置倒车轨迹线
     */
    static final int AW_COMMAND_SET_REVERSE_RULE = 23;
    /**
     * 设置摄像头亮度
     */
    static final int AW_COMMAND_SET_CAMERA_LUMA = 24;
    /**
     * 设置摄像头对比度
     */
    static final int AW_COMMAND_SET_CAMERA_CONTRAST = 25;
    /**
     * 设置摄像头饱和度
     */
    static final int AW_COMMAND_SET_CAMERA_SATURATION = 26;
    /**
     * 设置摄像头色调
     */
    static final int AW_COMMAND_SET_CAMERA_HUE = 27;

    /**
     * 获取摄像头Sensor类型
     */
    static final int AW_COMMAND_GET_SENSOR_TYPE = 28;

    /**
     * 获取摄像头Lens类型
     */
    static final int AW_COMMAND_GET_LENS_TYPE = 29;

    /**
     * 获取全景显示类型
     */
    static final int AW_COMMAND_GET_DISPLAY_VIEW_MODE = 30;

    /**
     * 获取摄像头制式
     */
    static final int AW_COMMAND_GET_SYSTEM = 31;

    /**
     * 获取车宽
     */
    static final int AW_COMMAND_GET_CAR_WIDTH = 32;

    /**
     * 获取车长
     */
    static final int AW_COMMAND_GET_CAR_HEIGHT = 33;

    /**
     * 获取左右偏移
     */
    static final int AW_COMMAND_GET_SIDE_OFFSET = 34;

    /**
     * 获取前摄像头镜像
     */
    static final int AW_COMMAND_GET_FRONT_CAMERA_MIRROR = 35;

    /**
     * 获取后摄像头镜像
     */
    static final int AW_COMMAND_GET_REAR_CAMERA_MIRROR = 36;

    /**
     * 获取前摄像头镜像
     */
    static final int AW_COMMAND_GET_LEFT_CAMERA_MIRROR = 37;

    /**
     * 获取后摄像头镜像
     */
    static final int AW_COMMAND_GET_RIGHT_CAMERA_MIRROR = 38;

    /**
     * 获取前摄像头横向偏移
     */
    static final int AW_COMMAND_GET_FRONT_CAMERA_OFFSETX = 39;
    /**
     * 获取后摄像头横向偏移
     */
    static final int AW_COMMAND_GET_REAR_CAMERA_OFFSETX = 40;
    /**
     * 获取左摄像头横向偏移
     */
    static final int AW_COMMAND_GET_LEFT_CAMERA_OFFSETX = 41;
    /**
     * 获取右摄像头横向偏移
     */
    static final int AW_COMMAND_GET_RIGHT_CAMERA_OFFSETX = 42;
    /**
     * 获取前摄像头纵向偏移
     */
    static final int AW_COMMAND_GET_FRONT_CAMERA_OFFSETY = 43;
    /**
     * 获取后摄像头纵向偏移
     */
    static final int AW_COMMAND_GET_REAR_CAMERA_OFFSETY = 44;
    /**
     * 获取左摄像头纵向偏移
     */
    static final int AW_COMMAND_GET_LEFT_CAMERA_OFFSETY = 45;
    /**
     * 获取右摄像头纵向偏移
     */
    static final int AW_COMMAND_GET_RIGHT_CAMERA_OFFSETY = 46;
    /**
     * 获取前摄像头缩放
     */
    static final int AW_COMMAND_GET_FRONT_CAMERA_SCALE = 47;
    /**
     * 获取后摄像头缩放
     */
    static final int AW_COMMAND_GET_REAR_CAMERA_SCALE = 48;
    /**
     * 获取左摄像头缩放
     */
    static final int AW_COMMAND_GET_LEFT_CAMERA_SCALE = 49;
    /**
     * 获取右摄像头缩放
     */
    static final int AW_COMMAND_GET_RIGHT_CAMERA_SCALE = 50;
    /**
     * 获取倒车轨迹是否显示
     */
    static final int AW_COMMAND_GET_REVERSE_RULE = 51;
    /**
     * 获取摄像头亮度
     */
    static final int AW_COMMAND_GET_CAMERA_LUMA = 52;
    /**
     * 获取摄像头对比度
     */
    static final int AW_COMMAND_GET_CAMERA_CONTRAST = 53;
    /**
     * 获取摄像头饱和度
     */
    static final int AW_COMMAND_GET_CAMERA_SATURATION = 54;
    /**
     * 获取摄像头色度
     */
    static final int AW_COMMAND_GET_CAMERA_HUE = 55;
    /**
     * 获取预览全景和单路摄像头时汽车的汽车坐标
     */
    static final int AW_COMMAND_GET_MIX_OVERLAY_POS = 56;
    /**
     * 获取预览全景时汽车的汽车坐标
     */
    static final int AW_COMMNAD_GET_BD_OVERLAY_POS = 57;
    //add other command
    /**
     * 单个镜头校正
     */
    static final int AW_COMMAND_CAMERA_ADJUST = 58;

    /**
     * 全景校正
     */
    static final int AW_COMMAND_BIRDVIEW_ADJUST = 59;


    /**
     * 对应1089和3089摄像头
     */
    public static int SENSOR_TYPE_1089_OR_3089 = 0;
    /**
     * 对应1089和3089摄像头
     */
    public static int SENSOR_TYPE_1099_OR_1058 = 1;

    /**
     * 华科
     */
    public static int LENS_TYPE_HK8077 = 0;

    /**
     * 海林
     */
    public static int LENS_TYPE_HAILIN4052 = 1;


    /**
     * 全景+前摄像头 显示模式
     */
    public static int AW_DISPLAY_BIRDVIEW_FRONT = 0x1;
    /**
     * 全景+右摄像头 显示模式
     */
    public static int AW_DISPLAY_BIRDVIEW_RIGHT = 0x2;
    /**
     * 全景+后摄像头 显示模式
     */
    public static int AW_DISPLAY_BIRDVIEW_REAR = 0x3;
    /**
     * 全景+左摄像头 显示模式
     */
    public static int AW_DISPLAY_BIRDVIEW_LEFT = 0x4;
    public static int AW_DISPLAY_BIRDVIEW_LEFT_RIGHT = 0x5;	/*!< Display LEFT + RIGHT*/
    public static int AW_DISPLAY_BIRDVIEW_LEFT_RIGHTF = 0x6;	/*!< Display LEFT + RIGHT*/
    public static int AW_DISPLAY_BIRDVIEW_LEFT_RIGHTR = 0x7;	/*!< Display LEFT + RIGHT*/
    /**
     * 全景显示模式
     */
    public static int AW_DISPLAY_BIRDVIEW_ONLY = 0x8;
    /**
     * 单独显示摄像头前视原始图像
     */
    public static int AW_DISPLAY_SIDEVIEW_FRONT = 0x9;
    /**
     * 单独显示摄像头右视原始图像
     */
    public static int AW_DISPLAY_SIDEVIEW_RIGHT = 0xa;
    /**
     * 单独显示摄像头后视原始图像
     */
    public static int AW_DISPLAY_SIDEVIEW_REAR = 0xb;
    /**
     * 单独显示摄像头左视原始图像
     */
    public static int AW_DISPLAY_SIDEVIEW_LEFT = 0xc;
    public static int AW_DISPLAY_FRONT = 0xd;			/*!< Display view only*/
    public static int AW_DISPLAY_RIGHT = 0xe;			/*!< Display view only*/
    public static int AW_DISPLAY_REAR = 0xf;			/*!< Display view only*/
    public static int AW_DISPLAY_LEFT = 0x10;				/*!< Display view only*/
    public static int AW_DISPLAY_SIDEVIEW_FOUR = 0x11;
    public static int AW_DISPLAY_DEBUG_FRONT = 0x12;
    public static int AW_DISPLAY_DEBUG_RIGHT = 0x13;
    public static int AW_DISPLAY_DEBUG_REAR = 0x14;
    public static int AW_DISPLAY_DEBUG_LEFT = 0x15;
    /**
     * 显示田字格4路原始图像
     */
    public static int AW_DISPLAY_SIDEVIEW_ALL = 0x16;

    /**
     * 摄像头制式
     */
    public static int PAL = 0;
    public static int NTSC = 1;

    /**
     * 摄像头镜像显示
     */
    public static int CAMERA_MIRROR = 1;

    /**
     * 摄像头正常显示
     */
    public static int CAMERA_NORMAL = 0;

    /**
     * 显示倒车轨迹线
     */
    public static int REVERSE_RULE_SHOW = 1;
    /**
     * 不显示倒车轨迹线
     */
    public static int REVERSE_RULE_MISS = 0;


}
