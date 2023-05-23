package com.taewon.mygallag;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.taewon.mygallag.sprites.AlienSprite;
import com.taewon.mygallag.sprites.Sprite;
import com.taewon.mygallag.sprites.StarshipSprite;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvadersView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    //SurfaceView 는 스레드(Runnable)를 이용해 강제로 화면에 그려주므로 view 보다 빠르다. 애니메이션 ,영상 처리에 이용
    //SurfaceHolder.Callback Surface 의 변화감지를 위해 필요

    private static int MAX_ENEMY_COUNT = 10;    //최대 적수

    private Context context;

    private  int characterId;       //StartActivity 에서 받은 비행정 이미지

    private  SurfaceHolder ourHolder; //화면에 그리는데 View 보다 빠르게 그려줌

    private Paint paint;        ////좌표 값 (x,y) 생성

    public  int screenW, screenH;       // 받아온 x,y 값을 넣음

    private Rect src, dst; // 사각형 그리는 클래스

    private ArrayList sprites = new ArrayList();

    private Sprite starship;        //Sprite.java

    private int score, currEnemyCount;      //점수, 적 수

    private Thread gameThread = null;

    private volatile boolean running; //휘발성불 함수

    private Canvas canvas;

    int mapBitmapY = 0;

    public SpaceInvadersView(Context context, int characterId, int x, int y){       // 메인에서 받아온 int characterId(StartActivity 에서 받은 비행정 이미지), int x, int y 값 들이 있음
        super(context);
        this.context = context;
        this.characterId = characterId;
        ourHolder = getHolder();    //현재 SurfaceView 를 리턴 받는다
        paint = new Paint();
        screenW = x;        //받아온 x,y 값
        screenH = y;
        src = new Rect();   //원본 사각형
        dst = new Rect();   //사본 사각형
        dst.set(0, 0, screenW, screenH);    //시작 x,y 와 끝 x,y
        startGame();
    }

    private  void startGame(){
        sprites.clear();    //ArrayList 지우기
        initSprites();      //ArrayList 에 침략자 아이템들 추가하기
        score = 0;          //시작시 스코어 0
    }

    public  void endGame(){     //게임 종료
        Log.e("GameOver", "GameOver");
        Intent intent = new Intent(context, ResultActivity.class);      // 점수를 ResultActivity.java로 보냄
        intent.putExtra("score", score);
        context.startActivity(intent);
        gameThread.stop();
    }


    public void removeSprite(Sprite sprite){
        sprites.remove(sprite);
    }

    private  void initSprites() {   //sprite 초기화
        starship = new StarshipSprite(context, this, characterId, screenW / 2, screenH -400, 1.5f);     //(다형성) 메인에서 받아온 int characterId(StartActivity 에서 받은 비행정 이미지), int screenW(x), int screenH(y) 값을 StarshipSprite 로 보냄
        //StarshipSprite생성 아이템들 생성
        sprites.add(starship);//ArrayList 추가
        spawnEnemy();           //2개가 있는 게 오류 인지 아님 시작 시 비행정 많이 생성할려고 2개 둔지 모르겠음
        spawnEnemy();
    }

    public  void spawnEnemy(){      //적 비행정 생성
        Random r = new Random();
        int x = r.nextInt(300) + 100; //100~399
        int y = r.nextInt(300) + 100; //100~399
        //외계인 아이템
        Sprite alien = new AlienSprite(context, this, R.drawable.ship_0002, 100 + x, 100 + y);      //AlienSprite.java 실행
        sprites.add(alien);
        currEnemyCount++;   //외계인 아이템 개수 증가     (적 비행정 개수 + 1)
    }
    
    public  ArrayList getSprites(){
        return sprites;
    }
    
    public void resume(){   // 사용자가 만든 resume()함수
        running = true;         //public void run() 실행
        gameThread = new Thread(this);
        gameThread.start();
    }

    //Sprite 를 StarshipSprite로 형 변환 후 리턴
    public  StarshipSprite getPlayer(){
        return (StarshipSprite) starship;
    }
    
    public int getScore() {
        return  score;
    }       // 점수 리턴 (반환)
    
    public  void setScore(int score){
        this.score = score;
    }
    
    public void setCurrEnemyCount(int currEnemyCount){
        this.currEnemyCount = currEnemyCount;
    }
    
    public int getCurrEnemyCount() {
        return currEnemyCount;
    }
    
    public void pause(){
        running = false;
        try{
            gameThread.join();//스레드 종료 대기하기
        }catch (InterruptedException e){
            
        }
    }
    
    
    
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
            startGame();
    }               //SurfaceView 가  생성되면 호출 ,(뷰가 생성될 때 호출된다.)

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {              //뷰가 변경될 때 호출된다.

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {            //뷰가 종료될 때 호출된다.

    }

    @Override
    public void run() {     //클래스의 인스턴스가 Thread 객체로 전달될 때 실행
        while (running){                //
            Random r = new Random();
            boolean isEnemySpawn = r.nextInt(100) + 1 < (getPlayer().speed +            //스피드와 파워 증가시 적 비행정 출현 확률 업
                    (int) (getPlayer().getPowerLevel() / 2));

            if(isEnemySpawn && currEnemyCount < MAX_ENEMY_COUNT) spawnEnemy(); // 적 비행정 추가

            for(int i = 0; i < sprites.size(); i++){
                Sprite sprite = (Sprite) sprites.get(i);    //ArrayList 에서 하나씩 가져 와서 움직 이기
                sprite.move();
            }

            for(int p = 0; p < sprites.size(); p++){
                for (int s = p +1; s < sprites.size(); s++){
                    try{
                        Sprite me = (Sprite) sprites.get(p);
                        Sprite other = (Sprite) sprites.get(s);
                        //충돌 체크
                        if(me.checkCollision(other)){       //두 객체 간의 충돌 여부를 판단
                            me.handleCollision(other);      //me.checkCollision(other)로 충돌 여부를 판단하고, 충돌 시에는 두 객체의 handleCollision 메소드를 호출하여 충돌 처리
                            other.handleCollision(me);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            draw();
            try {
                Thread.sleep(10);       //시스템 0.01초간 멈추기
            }catch (Exception e){

            }

        }
    }


    public void draw(){                                                 //getSurface() 표면 개체에 대한 직접 액세스
        if(ourHolder.getSurface().isValid()){       //true, false 반환,    해당 SurfaceHolder 객체가 유효한지를 확인
            canvas = ourHolder.lockCanvas();                        //lockCanvas() CPU 에서 렌더링하기 위한 버퍼를 잠그고 그리기에 사용할 수 있도록 캔버스를 반환
            canvas.drawColor(Color.BLACK);
            mapBitmapY++;
            if(mapBitmapY<0) mapBitmapY = 0;
            paint.setColor(Color.BLUE);
            for(int i = 0; i < sprites.size(); i ++){
                Sprite sprite = (Sprite) sprites.get(i);
                sprite.draw(canvas, paint);
            }
            ourHolder.unlockCanvasAndPost(canvas);      //버퍼를 잠금 해제하여 컴포지터로 전송 , 화면 표시(표면 비트맵에 그려진 그림을 화면으로 내보내 출력을 한다)
        }
    }

}
