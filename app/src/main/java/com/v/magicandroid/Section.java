package com.v.magicandroid;

/**
 * Author:v
 * Time:2021/4/22
 */
public class Section {
   private long startPoint;
   private long endPoint;
   private float speed;

    public Section() {
    }

    public Section(float speed) {
        this.speed = speed;
    }

    public Section(long startPoint, long endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }


    public long getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(long startPoint) {
        this.startPoint = startPoint;
    }

    public long getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(long endPoint) {
        this.endPoint = endPoint;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Section{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                '}';
    }
}
