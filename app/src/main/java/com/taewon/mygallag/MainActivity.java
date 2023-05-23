package com.taewon.mygallag;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    private Intent userIntent;

    ArrayList<Integer> bgMusicList;     //브금 리스트

    public static SoundPool effectSound;    //SoundPool 클래스는 메모리에 적재된 오디오 데이터를 빠르게 재생하고 관리하는 기능을 제공    ,   사운드 연타 구현 가능,  짧은 사운드 재생에 효과적 (10초내),  긴 사운드 재생불가

    public static float effectVolumn;       //효과음 음량

    ImageButton specialShitBtn;     // 필사기 버튼

    public  static  ImageButton fireBtn, reloadBtn; //일반 공격, 총알 장전

    JoystickView joyStick;          //https://github.com/controlwear/virtual-joystick-android참조 (조이스틱)

    public static TextView scoreTv; //점수 (TextView)

    LinearLayout gameFrame;     //main 에서 가장 위 레이아웃 (전체를 감싸는 LinearLayout)

   ImageView pauseBtn;          //설정? 일시정지? (||) 버튼

   public static LinearLayout lifeFrame;    //하트 ImageView를 감싸는 LinearLayout

   SpaceInvadersView spaceInvadersView; //SpaceInvadersView.java

   public static MediaPlayer bgMusic;   //브금   오디오나 비디오 파일을 재생, 일시 중지, 정지 및 다양한 제어 작업을 수행할 수 있는 클래스   , 긴 사운드 재생에 효과적 , 연타 처리의 어려움

   int bgMusicIndex;

   public static TextView bulletCount;  //총알개수

   private static ArrayList<Integer> effectSoundList;   //효과음 리스트

   public static final int PLAYER_SHOT = 0;     // effectSound index 0

   public static final int PLAYER_HURT = 1;      // effectSound index 1

   public static final int PLAYER_RELOAD = 2;       // effectSound index 2

   public static final int PLAYER_GET_ITEM = 3;     // effectSound index 3



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIntent =getIntent();    //현재 액티비티에서 이전 액티비티나 다른 구성 요소로부터 전달받은 데이터를 가져올떄 사용
        bgMusicIndex = 0;   //초기값 0
        bgMusicList = new ArrayList<Integer>(); // 브금 리스트
        bgMusicList.add(R.raw.main_game_bgm1);  //브금 1
        bgMusicList.add(R.raw.main_game_bgm2);  //브금 2
        bgMusicList.add(R.raw.main_game_bgm3);  //브금 3

        effectSound = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE,0);         // SoundPool (int maxStreams, int streamType, int srcQuality)
        effectVolumn = 1;   //초기값 1                                                                               //maxStreams에는 최대로 동시에 재생 가능한 음악파일 숫자
                                                                                                           //streamType는 재생 타입,  보통은 AudioManager.STREAM_MUSIC
        specialShitBtn = findViewById(R.id.specialShotBtn); //번개(필사기)                                   //srcQuality는 음악재생 품질, 0이 default
        joyStick = findViewById(R.id.joyStick);         //(조이스틱)
        scoreTv = findViewById(R.id.score);             //점수 (TextView)
        fireBtn = findViewById(R.id.fireBtn);           //일반 공격
        reloadBtn = findViewById(R.id.reloadBtn);       //리로드(총알 장전)
        gameFrame = findViewById(R.id.gameFrame);       //main 에서 가장 위 레이아웃 (전체를 감싸는 LinearLayout)
        pauseBtn = findViewById(R.id.pauseBtn);         //일지정지 (||) 버튼
        lifeFrame = findViewById(R.id.lifeFrame);        //하트 ImageView를 감싸는 LinearLayout

        init();
        setBtnBehavior();   //조이스틱 작동함수 실행

    }

    @Override
    protected void onResume() { //액티비티가 다시 시작되어 사용자와 상호작용할 준비가 된 상태로 전환될 때 호출되는 콜백 메소드
        super.onResume();
        bgMusic.start();    //브금 시작
        spaceInvadersView.resume(); //spaceInvadersView 의 resume 실행
    }

    private void init(){

        Display display = getWindowManager().getDefaultDisplay();   //view의 display를 얻어온다       Display 객체를 가져오면, 디스플레이의 크기와 해상도 등의 정보를 얻음
        Point size = new Point();   //좌표 값 (x,y) 생성
        display.getSize(size);

        spaceInvadersView = new SpaceInvadersView(this,
                userIntent.getIntExtra("character",R.drawable.ship_0000),size.x,size.y);        // (StartActivity에서 받은 값)SpaceInvadersView로 character 이름으로
                                                                                                    // R.drawable.ship_0000 넘김 (size.x,size.y) -> 디스플레이의 크기를 넘김
        gameFrame.addView(spaceInvadersView);//프레임에 만든 아이템 넣기,   spaceInvadersView 를 gameFrame(전체를 감싸는 LinearLayout)에 포함 시킴

        //음악 교체
        changeBgMusic();  // 처음 시작시 브금을 틀기 위해
        bgMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { //음악이 끝날때  (재생한 후 해당 미디어를 모두 재생이 되었을 때 설정하는 이벤트)
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                changeBgMusic();    // 브금이 끝나갈때 호툴
            }
        });


        bulletCount =findViewById(R.id.bulletCount);//총알개수

        //spaceInvadersView의 getPlayer()구현
        bulletCount.setText(spaceInvadersView.getPlayer().getBulletsCount() + "/30");   //30/30 (현재 보유 총알) 현재 총알 값을 받아옴
        scoreTv.setText(Integer.toString(spaceInvadersView.getScore()));//score:0   점수

        effectSoundList = new ArrayList<>();            //add(int index, E element)  load()함수를 이용해 재생시킬 파일 호출
        effectSoundList.add(PLAYER_SHOT,effectSound.load(MainActivity.this,R.raw.player_shot_sound,1));     //총알 발사 효과음             숫자 1은 우선 순위
        effectSoundList.add(PLAYER_HURT,effectSound.load(MainActivity.this,R.raw.player_hurt_sound,1));     //데미지 받을시 효과음
        effectSoundList.add(PLAYER_RELOAD,effectSound.load(MainActivity.this,R.raw.reload_sound,1));
        effectSoundList.add(PLAYER_GET_ITEM,effectSound.load(MainActivity.this,R.raw.player_get_item_sound,1)); //아이템 먹을시 효과음
        bgMusic.start();    //음악이 변경되면 재생


    }

    private void changeBgMusic(){
        bgMusic = MediaPlayer.create(this,bgMusicList.get(bgMusicIndex));// context -> 음악 하나  mediaPlayer=MediaPlayer.create(this, R.raw.sound);
        bgMusic.start();    //브금 시작
        bgMusicIndex++;//음악 변경을 위해 증가
        bgMusicIndex = bgMusicIndex % bgMusicList.size();//음악 개수만큼만 바뀌게 하기  (n%3 = 나머지가 0 1 2 만 나옴)


    }

    @Override
    protected void onPause() {  //일시 정지 시
        super.onPause();
        bgMusic.pause();    // 브금 일시 정지
        spaceInvadersView.pause();  //spaceInvadersView 일시정지
    }
    
    public  static  void effectSound(int flag){
        effectSound.play(effectSoundList.get(flag), effectVolumn, effectVolumn, //play (int soundID,float leftVolume, float rightVolume,  int priority, int loop,   float rate)
                0, 0, 1.0f);                                          //soundID : 재생시킬 파일의 resID        // leftVolume : 왼쪽 볼륨 크기 (range : 0.0 ~ 1.0)    //rightVolume : 오른쪽 볼륨 크리 (range : 0.0 ~ 1.0)
        //Soundpool 실행                                                        // priority : 우선순위 ( 0이 가장 낮음을 나타냅니다)     //loop : 재생횟수입니다. (0일경우 1번만 재생 -1일 경우에는 무한반복)  //rate : 재생속도입니다.0.5로 할 경우 2배 느리게 재생되고 2.0으로 할 경우 2배 빠르게 재생됩니다. (range : 0.5 ~ 2.0)
    }

    
    private  void setBtnBehavior(){
        joyStick.setAutoReCenterButton(true);       //자동으로 중앙에 다시 정렬된 버튼
        joyStick.setOnKeyListener(new View.OnKeyListener() {            //(키 이벤트)
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("keycode", Integer.toString(i));
                return false;
            }
        });
        
        joyStick.setOnMoveListener(new JoystickView.OnMoveListener() {  // 위치 (터치) 이벤트
            @Override
            public void onMove(int angle, int strength) {       // angle - 전류 각도 ,  strength – 현재 강도    조이스틱 보기 단추가 이동되면 호출
                Log.d("angle", Integer.toString(angle));
                Log.d("force", Integer.toString(strength));
                
                if(angle > 67.5 && angle < 112.5){
                    //위
                    spaceInvadersView.getPlayer().moveUp(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                } else if (angle > 247.5 && angle < 292.5) {
                    //아래
                    spaceInvadersView.getPlayer().moveDown(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                } else if (angle > 112.5 && angle < 157.5) {
                    //왼쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                } else if (angle > 157.5 && angle < 202.5) {
                    //왼쪽 
                    spaceInvadersView.getPlayer().moveLeft(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                } else if (angle > 202.5 && angle < 247.5) {
                    //왼쪽 대각선 아래
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                } else if (angle > 22.5 && angle < 67.5) {
                    //오른쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                } else if (angle > 337.5 || angle < 22.5) {
                    //오른쪽
                    spaceInvadersView.getPlayer().moveRight(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                } else if (angle > 292.5 && angle < 337.5) {
                    //오른쪽 대각선 아래
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                }
            }
        });

        fireBtn.setOnClickListener(new View.OnClickListener() {         // 일반 공격 버튼
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().fire();   //spaceInvadersView 를 거처 StarshipSprite 에 있는 fire 실행
            }
        });
        
        reloadBtn.setOnClickListener(new View.OnClickListener() {       //총알 장전 버튼
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().reloadBullets();      //spaceInvadersView 를 거처 StarshipSprite 에 있는 reloadBullets 실행
                //spaceInvadersView -> starshipsprite -> reloadButtlets()
            }
        });
        
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.pause();  //spaceInvadersView 일시정지
                PauseDialog pauseDialog = new PauseDialog(MainActivity.this);   //PauseDialog 클래스가 MainActivity 클래스에서 생성된 것
                pauseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {         //다이얼로그가 닫힐때
                        spaceInvadersView.resume();         //spaceInvadersView resume() 호출
                    }
                });
                pauseDialog.show();     //pauseDialog 띄움
            }
        });

        specialShitBtn.setOnClickListener(new View.OnClickListener() {      //필사기 버튼
            @Override
            public void onClick(View view) {
                if(spaceInvadersView.getPlayer().getSpecialSotCount() >= 0)
                    spaceInvadersView.getPlayer().specialShot();
            }
        });

    }
    
    

}