package com.taewon.mygallag.sprites;


import android.content.Context;

import com.taewon.mygallag.SpaceInvadersView;

import java.util.Timer;
import java.util.TimerTask;

public class SpecialshotSprite extends Sprite{
    private SpaceInvadersView game;

    public SpecialshotSprite(Context context, SpaceInvadersView game,
                             int resId, float x, float y){
        super(context, resId, x, y);
        this.game=game;
        game.getPlayer().setSpecialShooting(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                autoRemove();
            }
        },5000);        // 필사기 지속 시간 5초
    }

    @Override
    public void move() {
        super.move();
        this.x = game.getPlayer().getX() - getWidth() + 240;
        this.y = game.getPlayer().getY() - getHeight();
    }

    public void autoRemove(){
        game.getPlayer().setSpecialShooting(false);     // 일반 공격 사용 불가
        game.removeSprite(this);
    }


}
