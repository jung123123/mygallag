package com.taewon.mygallag.items;


import android.content.Context;

import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.sprites.Sprite;

import java.util.Timer;
import java.util.TimerTask;

public class HealitemSprite extends Sprite {

    SpaceInvadersView game;

    public HealitemSprite(Context context, SpaceInvadersView game,
                          int x, int y, int dx, int dy){
        super(context, R.drawable.heal_item,x,y);

        this.game =game;
        this.dx = dx;
        this.dy = dy;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {         //Timer 클래스는 주어진 시간이 경과한 후에 일련의 작업을 수행하도록 예약하는 기능  ,schedule() 메서드를 호출하여 작업을 예약
                autoRemove();
            }
        }, 10000);      //10초 후 autoRemove() 메서드 실행
    }

    private void autoRemove(){game.removeSprite(this);}

    @Override
    public void move() {
        if((dx < 0) && (x < 120)){
            dx *= -1; return;
        }
        if((dx > 0) && (x > game.screenW - 120)){
            dx *= -1; return;
        }
        if((dy < 0) && (y < 120)){
            dy *= -1; return;
        }
        if((dy > 0) && (y > game.screenH - 120)){
            dy *= -1; return;
        }

        super.move();
    }
}
