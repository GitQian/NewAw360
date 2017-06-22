package com.android.allwinner.aw360;

/**
 * Created by xiasj on 17-6-9.
 * 360全景应用接口
 */

public class AW360API {

    private AW360Camera mAW360Camera;

    private AW360API() {
        mAW360Camera = new AW360Camera();
    }

    private static class SingletHolder {
        static final AW360API sInstance = new AW360API();
    }

    public static AW360API getInstance() {
        return SingletHolder.sInstance;
    }

    /**
     * 设置镜头Sensor类型
     *
     * @param type 设置镜头Sensor类型。支持如下Sensor类型：
     *             {@link AW360Command#SENSOR_TYPE_1089_OR_3089}
     *             {@link AW360Command#SENSOR_TYPE_1099_OR_1058}
     * @return 设置结果，true：成功，false:失败。
     */
    public boolean setCameraSensorType(int type) {
        return mAW360Camera.setCameraSensorType(type);
    }

    /**
     * @param type 设置镜头类型。支持如下类型：
     *             {@link AW360Command#LENS_TYPE_HK8077}
     *             {@link AW360Command#LENS_TYPE_HAILIN4052}
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCameraLensType(int type) {
        return mAW360Camera.setCameraLensType(type);
    }

    /**
     * 设置显示模式
     *
     * @param mode 显示模式。支持如下模式：
     *             {@link AW360Command#AW_DISPLAY_BIRDVIEW_FRONT}
     *             {@link AW360Command#AW_DISPLAY_BIRDVIEW_RIGHT}
     *             {@link AW360Command#AW_DISPLAY_BIRDVIEW_REAR}
     *             {@link AW360Command#AW_DISPLAY_BIRDVIEW_LEFT}
     *             {@link AW360Command#AW_DISPLAY_BIRDVIEW_ONLY}
     *             {@link AW360Command#AW_DISPLAY_SIDEVIEW_FRONT}
     *             {@link AW360Command#AW_DISPLAY_SIDEVIEW_REAR}
     *             {@link AW360Command#AW_DISPLAY_SIDEVIEW_RIGHT}
     *             {@link AW360Command#AW_DISPLAY_SIDEVIEW_LEFT}
     *             {@link AW360Command#AW_DISPLAY_SIDEVIEW_ALL}
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setViewModel(int mode) {
        return mAW360Camera.setCameraViewMode(mode);
    }

    /**
     * 设置摄像头制式
     *
     * @param standard 摄像头制式。支持如下摄像头制式：
     *                 {@link AW360Command#NTSC}
     *                 {@link AW360Command#PAL}
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCameraStandard(int standard) {
        return mAW360Camera.setCameraStandard(standard);
    }

    /**
     * 设置车宽
     *
     * @param width 车宽（单位：毫米）
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCarWidth(int width) {
        return mAW360Camera.setCarWidth(width);
    }

    /**
     * 设置车长
     *
     * @param height 车长（单位：毫米）
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCarHeight(int height) {
        return mAW360Camera.setCarHeight(height);
    }

    /**
     * 设置左右偏移
     *
     * @param offset 左右偏移值（单位：毫米）
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setSideOffset(int offset) {
        return mAW360Camera.setCarSideOffset(offset);
    }

    /**
     * 设置前摄像头是否镜像
     *
     * @param isMirror 镜像: true,镜像；
     *                 false， 非镜像。
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setFrontCameraMirror(boolean isMirror) {
        if (isMirror) {
            return mAW360Camera.setFrontCameraMirror(AW360Command.CAMERA_MIRROR);
        } else {
            return mAW360Camera.setFrontCameraMirror(AW360Command.CAMERA_NORMAL);
        }
    }

    /**
     * 设置后摄像头是否镜像
     *
     * @param isMirror 镜像: true,镜像；
     *                 false， 非镜像。
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRearCameraMirror(boolean isMirror) {
        if (isMirror) {
            return mAW360Camera.setRearCameraMirror(AW360Command.CAMERA_MIRROR);
        } else {
            return mAW360Camera.setRearCameraMirror(AW360Command.CAMERA_NORMAL);
        }
    }

    /**
     * 设置左摄像头是否镜像
     *
     * @param isMirror 镜像: true,镜像；
     *                 false， 非镜像。
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setLeftCameraMirror(boolean isMirror) {
        if (isMirror) {
            return mAW360Camera.setLeftCameraMirror(AW360Command.CAMERA_MIRROR);
        } else {
            return mAW360Camera.setLeftCameraMirror(AW360Command.CAMERA_NORMAL);
        }
    }

    /**
     * 设置左摄像头是否镜像
     *
     * @param isMirror 镜像: true,镜像；
     *                 false， 非镜像。
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRightCameraMirror(boolean isMirror) {
        if (isMirror) {
            return mAW360Camera.setRightCameraMirror(AW360Command.CAMERA_MIRROR);
        } else {
            return mAW360Camera.setRightCameraMirror(AW360Command.CAMERA_NORMAL);
        }
    }

    /**
     * 设置前摄像头横向偏移值
     *
     * @param x 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setFrontCameraOffsetX(int x) {
        return mAW360Camera.setFrontCameraOffsetX(x);
    }

    /**
     * 设置前摄像头纵向偏移值
     *
     * @param y 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setFrontCameraOffsetY(int y) {
        return mAW360Camera.setFrontCameraOffsetY(y);
    }

    /**
     * 设置后摄像头横向偏移值
     *
     * @param x 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRearCameraOffsetX(int x) {
        return mAW360Camera.setRearCameraOffsetX(x);
    }

    /**
     * 设置后摄像头纵向偏移值
     *
     * @param y 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRearCameraOffsetY(int y) {
        return mAW360Camera.setRearCameraOffsetY(y);
    }

    /**
     * 设置左摄像头横向偏移值
     *
     * @param x 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setLeftCameraOffsetX(int x) {
        return mAW360Camera.setLeftCameraOffsetX(x);
    }

    /**
     * 设置左摄像头纵向偏移值
     *
     * @param y 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setLeftCameraOffsetY(int y) {
        return mAW360Camera.setLeftCameraOffsetY(y);
    }

    /**
     * 设置右摄像头横向偏移值
     *
     * @param x 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRightCameraOffsetX(int x) {
        return mAW360Camera.setRightCameraOffsetX(x);
    }

    /**
     * 设置右摄像头纵向偏移值
     *
     * @param y 取值范围： -120~120。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRightCameraOffsetY(int y) {
        return mAW360Camera.setRightCameraOffsetY(y);
    }

    /**
     * 设置前摄像头缩放
     *
     * @param scale 取值范围： 0.6~1.4。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setFrontCameraScale(float scale) {
        return mAW360Camera.setFrontCamerScale((int) (scale * 10));
    }

    /**
     * 设置后摄像头缩放
     *
     * @param scale 取值范围： 0.6~1.4。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRearCameraScale(float scale) {
        return mAW360Camera.setRearCamerScale((int) (scale * 10));
    }

    /**
     * 设置左摄像头缩放
     *
     * @param scale 取值范围： 0.6~1.4。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setLeftCameraScale(float scale) {
        return mAW360Camera.setLeftCamerScale((int) (scale * 10));
    }

    /**
     * 设置右摄像头缩放
     *
     * @param scale 取值范围： 0.6~1.4。 超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setRightCameraScale(float scale) {
        return mAW360Camera.setRightCamerScale((int) (scale * 10));
    }

    /**
     * 设置是否显示倒车轨迹线
     *
     * @param show true: 显示
     *             false: 不显示
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setReverseRule(boolean show) {
        if (show) {
            return mAW360Camera.setReverseRule(AW360Command.REVERSE_RULE_SHOW);
        } else {
            return mAW360Camera.setReverseRule(AW360Command.REVERSE_RULE_MISS);
        }
    }

    /**
     * 设置摄像头亮度
     *
     * @param luma 亮度取值范围：0~255。超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCameraLuma(int luma) {
        return mAW360Camera.setCameraLuma(luma);
    }

    /**
     * 设置色度
     *
     * @param hue 色度取值范围：0~255。超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCameraHue(int hue) {
        return mAW360Camera.setCameraHue(hue);
    }

    /**
     * 设置对比度
     *
     * @param contrast 对度取值范围：0~255。超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCameraContrast(int contrast) {
        return mAW360Camera.setCameraContrast(contrast);
    }

    /**
     * 设置饱和度
     *
     * @param saturation 饱和取值范围：0~255。超过范围返回false
     * @return 设置结果，true:成功，false:失败。
     */
    public boolean setCameraSaturation(int saturation) {
        return mAW360Camera.setCameraSaturation(saturation);
    }

    /**
     * 获取摄像头Sensor类型
     * 默认值：
     * {@link AW360Command#SENSOR_TYPE_1089_OR_3089}
     *
     * @return 摄像头Sensor类型
     */
    public int getCameraSensorType() {
        return mAW360Camera.getCameraSensorType();
    }

    /**
     * 获取摄像头Lens类型
     * 默认值：
     * {@link AW360Command#LENS_TYPE_HK8077}
     *
     * @return 摄像头Lens类型
     */
    public int getCameraLensType() {
        return mAW360Camera.getCameraLensType();
    }

    /**
     * 获取全景显示类型
     * 默认值：
     * {@link AW360Command#AW_DISPLAY_BIRDVIEW_FRONT}
     *
     * @return 全景显示类型
     */
    public int getCameraViewMode() {
        return mAW360Camera.getCameraViewMode();
    }

    /**
     * 获取摄像头制式
     * 默认值：
     * {@link AW360Command#NTSC}
     *
     * @return 摄像头制式
     */
    public int getCameraStandard() {
        return mAW360Camera.getCameraStandard();
    }

    /**
     * 获取车宽
     *
     * @return 车宽
     */
    public int getCarWidth() {
        return mAW360Camera.getCarWidth();
    }

    /**
     * 获取车长
     *
     * @return 车长
     */
    public int getCarHeight() {
        return mAW360Camera.getCarHeight();
    }

    /**
     * 获取左右偏移值
     *
     * @return 左右偏移值
     */
    public int getCarSideOffset() {
        return mAW360Camera.getCarSideOffset();
    }

    /**
     * 获取前摄像头镜像
     *
     * @return 前摄像头镜像
     */
    public boolean getFrontCameraMirror() {
        return mAW360Camera.getFrontCameraMirror() == AW360Command.CAMERA_MIRROR;
    }

    /**
     * 获取后摄像头镜像
     *
     * @return 后摄像头镜像
     */
    public boolean getRearCameraMirror() {
        return mAW360Camera.getRearCameraMirror() == AW360Command.CAMERA_MIRROR;
    }

    /**
     * 获取左摄像头镜像
     *
     * @return 左摄像头镜像
     */
    public boolean getLeftCameraMirror() {
        return mAW360Camera.getLeftCameraMirror() == AW360Command.CAMERA_MIRROR;
    }

    /**
     * 获取右摄像头镜像
     *
     * @return 右摄像头镜像
     */
    public boolean getRightCameraMirror() {
        return mAW360Camera.getRightCameraMirror() == AW360Command.CAMERA_MIRROR;
    }

    /**
     * 获取前摄像头横向偏移
     *
     * @return 前摄像头纵向偏移值
     */
    public int getFrontCameraOffsetX() {
        return mAW360Camera.getFrontCameraOffsetX();
    }

    /**
     * 获取前摄像头横向偏移
     *
     * @return 前摄像头纵向偏移值
     */
    public int getFrontCameraOffsetY() {
        return mAW360Camera.getFrontCameraOffsetY();
    }

    /**
     * 获取后摄像头横向偏移
     *
     * @return 后摄像头纵向偏移值
     */
    public int getRearCameraOffsetX() {
        return mAW360Camera.getRearCameraOffsetX();
    }

    /**
     * 获取后摄像头横向偏移
     *
     * @return 后摄像头纵向偏移值
     */
    public int getRearCameraOffsetY() {
        return mAW360Camera.getRearCameraOffsetY();
    }

    /**
     * 获取左摄像头横向偏移
     *
     * @return 左摄像头纵向偏移值
     */
    public int getLeftCameraOffsetX() {
        return mAW360Camera.getLeftCameraOffsetX();
    }

    /**
     * 获取左摄像头横向偏移
     *
     * @return 左摄像头纵向偏移值
     */
    public int getLeftCameraOffsetY() {
        return mAW360Camera.getLeftCameraOffsetY();
    }

    /**
     * 获取右摄像头横向偏移
     *
     * @return 右摄像头纵向偏移值
     */
    public int getRightCameraOffsetX() {
        return mAW360Camera.getRightCameraOffsetX();
    }

    /**
     * 获取右摄像头横向偏移
     *
     * @return 右摄像头纵向偏移值
     */
    public int getRightCameraOffsetY() {
        return mAW360Camera.getRightCameraOffsetY();
    }


    /**
     * 获取前摄像头缩放比
     *
     * @return 前摄像头缩放比
     */
    public float getFrontCameraScale() {
        return (float) mAW360Camera.getFrontCameraScale() / 10;
    }

    /**
     * 获取后摄像头缩放比
     *
     * @return 后摄像头缩放比
     */
    public float getRearCameraScale() {
        return (float) mAW360Camera.getRearCameraScale() / 10;
    }

    /**
     * 获取左摄像头缩放比
     *
     * @return 左摄像头缩放比
     */
    public float getLeftCameraScale() {
        return (float) mAW360Camera.getLeftCameraScale() / 10;
    }

    /**
     * 获取右摄像头缩放比
     *
     * @return 右摄像头缩放比
     */
    public float getRightCameraScale() {
        return (float) mAW360Camera.getRightCameraScale() / 10;
    }

    /**
     * 获取全景和单路摄像头预览时的汽车图片位置
     * @return 汽车图片位置
     */
    public CarPosition getMixCarPosition() {
        String position = mAW360Camera.getMixPosition();
        CarPosition carPosition = new CarPosition();
        boolean result = carPosition.parsePosition(position);
        if (result) {
            return carPosition;
        }
        return null;
    }

    /**
     * 获取全景预览时的汽车图片位置
     * @return 汽车图片位置
     */
    public CarPosition getBirdViewCarPosition() {
        String position = mAW360Camera.getBirdViewPosition();
        CarPosition carPosition = new CarPosition();
        boolean result = carPosition.parsePosition(position);
        if (result) {
            return carPosition;
        }
        return null;
    }

    /**
     * 镜头单路校正
     */
    public void cameraAdjust() {
        mAW360Camera.cameraAdjust();
    }

    /**
     * 全景校正
     */
    public void birdviewAdjust() {
        mAW360Camera.birdViewAdjust();
    }

}
