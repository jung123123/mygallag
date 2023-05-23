package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Sprite {
    protected  float x,y;
    protected  int width, height;
    protected float dx, dy;
    private Bitmap bitmap;  //이미지를 표현하기 위해 사용되는 객체
    protected int id;
    private RectF rect;     //좌표를 사용하여 사각형 영역을 표현


    public Sprite(Context context, int resourceId, float x, float y){
        this.id = resourceId;
        this.x = x; this.y = y;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);  //비트맵 개체 만들기  (Resource 폴더에 저장된 그림파일을 Bitmap 으로 만들어 리턴)
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        rect = new RectF();
    }

    public int getWidth(){return  width;};      //가로
    public int getHeight(){return  height;};    //세로

    public void draw(Canvas canvas, Paint paint){ canvas.drawBitmap(bitmap, x,y,paint);}        //Canvas 객체와 Paint 객체를 사용하여 현재 Sprite 객체를 화면에 그리는 역할
                                                                                                //Canvas : 그리기 명령어를 수행하기 위함, (선을 그릴 수 있는 메서드를 제공)
    public void move(){                                                                         //Paint : 그리기 명령을 어떻게 꾸밀것인지 묘사, (선의 색상을 정의하는 메서드를 제공)
        x=x+dx; y=y+dy;
        rect.left=x; rect.right = x+width;
        rect.top = y; rect.bottom = y + height;
    }

    public float getX() {return x;}
    public float getY() {return y;}
    public float getDx() {return dx;}
    public float getDy() {return dy;}
    public void setDx(float dx) {this.dx = dx;}
    public void setDy(float dy) {this.dy = dy;}
    public RectF getRect(){return rect;}

    public boolean checkCollision(Sprite other){
        return RectF.intersects(this.getRect(), other.getRect());       //rect 가 겹쳐저 있는 지 판단 , Sprite 객체 와 other(받아온 값) 서로 겹치는 영역이 있으면 true 를 반환하고, 그렇지 않으면 false 를 반환
    }

    public void handleCollision(Sprite other){}//충돌 처리 위한  (StarshipSpite.java 의 handleCollision)  다형성 때문에 추가


    public Bitmap getBitmap(){return bitmap;}
    public void setBitmap(Bitmap bitmap){this.bitmap = bitmap;}



}
