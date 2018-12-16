package com.mikebarone.flappybird;


public class Bullet {
    float speed ;
    float x;
    float y;
    int direction;
    int owner;
    public Bullet(float x, float y, int direction, int owner) {
        this.x = x;
        this.y=y;
        this.direction=direction;
        this.owner = owner;
        this.speed = 15;
        if(this.owner==1){
            this.speed=5;
        }
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public void tick(){
        this.x=this.x+this.speed*this.direction;
    }

    public int getOwner(){
        return this.owner;
    }
}
