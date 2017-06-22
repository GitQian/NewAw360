package com.android.allwinner.aw360;

/**
 * Created by xiasj on 17-6-13.
 * 预览时汽车图片位置
 */

public class CarPosition {
    private float x;
    private float y;
    private float width;
    private float height;

    public CarPosition() {

    }

    public boolean parsePosition(String position) {
        //x=20,y=20,w=20,h=20
        String[] tmps = position.split(",");

        if (tmps.length < 4) {
            return false;
        }

        int[] values = new int[tmps.length];
        for (int i = 0; i < tmps.length; i++) {
            try {
                values[i] = Integer.parseInt(tmps[i].substring(2));
            } catch (NumberFormatException e) {
                return false;
            }
        }

        x = values[0];
        y = values[1];
        width = values[2];
        height = values[3];

        return true;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
