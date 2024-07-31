/**
 * Represents data of a row
 * @author Reda TARGAOUI & Ilyass EL MAAIDLI
 * @since 16 november 2023
 */
package com.DataConverter.Model;

public class RowData {
    // Attributes :
    private float time;
    private float x;
    private float y;
    private float leftDiam;
    private float rightDiam;
    private String image;

    /**
     * Initialize attributes
     */
    public RowData() {
        this.time = 0;
        this.x = 0;
        this.y = 0;
        this.leftDiam = 0;
        this.rightDiam = 0;
        this.image = "";
    }


    /**
     * Get time
     * @return time
     */
    public float getTime() {
        return time;
    }

    /**
     * Set time
     * @param time time
     */
    public void setTime(float time) {
        this.time = time;
    }

    /**
     * Get x
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * Set x
     * @param x x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Get y
     * @return y
     */
    public float getY() {
        return y;
    }

    /**
     * Set y
     * @param y y
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Get leftDiam
     * @return leftDiam
     */
    public float getLeftDiam() {
        return leftDiam;
    }

    /**
     * Set leftDiam
     * @param leftDiam leftDiam
     */
    public void setLeftDiam(float leftDiam) {
        this.leftDiam = leftDiam;
    }

    /**
     * Get rightDiam
     * @return rightDiam
     */
    public float getRightDiam() {
        return rightDiam;
    }

    /**
     * Set rightDiam
     * @param rightDiam rightDiam
     */
    public void setRightDiam(float rightDiam) {
        this.rightDiam = rightDiam;
    }

    /**
     * Get image
     * @return image
     */
    public String getImage() {
        return image;
    }

    /**
     * Set image
     * @param image image
     */
    public void setImage(String image) {
        this.image = image;
    }

}