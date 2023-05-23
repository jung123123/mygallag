package com.taewon.mygallag.sprites;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;
import java.util.Random;

public class AlienSprite extends Sprite{

    private Context context;
    private SpaceInvadersView game;
    ArrayList<AlienShotSprite> alienShotSprites;
    Handler fireHandler = null;
    boolean isDestroyed = false;

    public AlienSprite(Context context, SpaceInvadersView game, int resId, int x, int y){
        //외계인 만들기

        super(context, resId ,x,y);
        this.context = context;
        this.game = game;
        alienShotSprites = new ArrayList<>();
        Random r = new Random();
        int randomDx = r.nextInt(5);    //0~4
        int randomDy = r.nextInt(5);
        if(randomDy <= 0) dy=1;
        dx = randomDx; dy=randomDy;
        fireHandler = new Handler(Looper.getMainLooper());  //Looper 객체를 이용하여 Handler 클래스의 인스턴스를 생성  --- getMainLooper() 메서드는 현재 실행 중인 애플리케이션의 메인 스레드의 Looper 객체를 반환하는 메서드
        fireHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {     //일정 시간 지난후 실행
                        Random r = new Random();
                        boolean isFire = r.nextInt(100)+1 <= 30;    // 1~100 중 30 이하일시 true
                        if(isFire && !isDestroyed){     // isFire 이 true 일때 실행
                            fire();
                            fireHandler.postDelayed(this,1000);    // 반복 ( isFire 가 false 일때 까지)
                        }
                    }
                },1000);    //1초 후 실행
    }

    @Override
    public void move() {
        super.move();
        if(((dx < 0) && (x < 10)) || ((dx > 0) && ( x > 800))) {
            dx = -dx;
            if (y > game.screenH) {
                game.removeSprite(this);    //SpaceInvaderView
            }
        }
    }

    @Override
    public void handleCollision(Sprite other) {
        if(other instanceof ShotSprite){         //일반 공격               // instanceof => 객체 타입을 확인하는 연산자  ,형변환 가능 여부를 확인하며 true / false 로 결과를 반환
            game.removeSprite(other);
            game.removeSprite(this);
            destroyAlien();
            return;
        }

        if(other instanceof SpecialshotSprite){     //필살기
            game.removeSprite(this);
            destroyAlien();
            return;
        }

    }

    private void destroyAlien(){        //적 비행정 파괴 시
        isDestroyed = true;
        game.setCurrEnemyCount(game.getCurrEnemyCount()-1);     // 적수 감소
        for(int i = 0; i < alienShotSprites.size(); i++)
            game.removeSprite(alienShotSprites.get(i));
        spawnHealItem();                //아이템 드랍 (힐,파워,스피드)
        spawnPowerItem();
        spawnSpeedItem();
                game.setScore(game.getScore()+1);                           //스코어 증가
        MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
    }


    private void fire(){
        AlienShotSprite alienShotSprite = new AlienShotSprite(context, game, getX(), getY()+30,16);
        alienShotSprites.add(alienShotSprite);
        game.getSprites().add(alienShotSprite);
    }

    private void spawnSpeedItem(){      //스피드아이템 드랍
        Random r = new Random();
        int speedItemDrop = r.nextInt(100) + 1;
        if(speedItemDrop <= 5 ){             //5%
            int dx = r.nextInt(10) + 1;
            int dy = r.nextInt(10) + 5;
            game.getSprites().add(new SpeedItemSprite(context, game,
                    (int)this.getX(), (int)this.getY(), dx, dy));
        }
    }

    private void spawnPowerItem(){      //파워아이템 드랍
        Random r = new Random();
        int powerItemDrop = r.nextInt(100)+1;
        if(powerItemDrop <= 3){                 //3%
            int dx = r.nextInt(10) + 1;
            int dy = r.nextInt(10) + 10;
            game.getSprites().add(new PowerItemSprite(context,game,
                    (int)this.getX(), (int)this.getY(), dx, dy));
        }
    }

    private void spawnHealItem(){       //힐아이템 드랍
        Random r = new Random();
        int healItemDrop = r.nextInt(100)+1;
        if(healItemDrop <= 1) {                 //1%
            int dx = r.nextInt(10) + 1;
            int dy = r.nextInt(10) + 10;
            game.getSprites().add(new HealitemSprite(context, game,
                    (int)this.getX(), (int)this.getY(), dx, dy));
        }
    }




}
