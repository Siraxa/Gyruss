package com.mikebarone.flappybird;

public class Enemy {
    float x;
    float y;
    float speed;
    float vel;
    int screenSize;

    public Enemy(float x, float y, int screenSize) {
        this.x = x;
        this.y = y;
        this.speed = 10;
        this.vel = 0f;
        this.screenSize = screenSize;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public void tick(){
        this.y = this.y + this.speed;
        this.speed = this.speed + this.vel;
        if (this.y < screenSize/2)
            this.vel = + 0.05f;
        if (this.y > screenSize/2)
            this.vel = -0.05f;


        //this.x=this.x+this.speed*this.direction;
    }

}
