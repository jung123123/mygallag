package com.taewon.mygallag;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    int characterId, effectId;      //img[] 값중 선택한 하나 , 비행정 클릭시 나는 효과음

    ImageButton startBtn;       // 게임 시작 버튼

    TextView guideTv;       //캐릭터 선택 TextView

    MediaPlayer mediaPlayer; //브금   오디오나 비디오 파일을 재생, 일시 중지, 정지 및 다양한 제어 작업을 수행할 수 있는 클래스   , 긴 사운드 재생에 효과적 , 연타 처리의 어려움

    ImageView imgView[] = new ImageView[8];

    Integer img_id[] = {R.id.ship_001, R.id.ship_002, R.id.ship_003, R.id.ship_004, R.id.ship_005, R.id.ship_006, R.id.ship_007, R.id.ship_008}; //비행정 id (ImageView)

    Integer img[] = {R.drawable.ship_0000, R.drawable.ship_0001, R.drawable.ship_0002, R.drawable.ship_0003,
            R.drawable.ship_0004, R.drawable.ship_0005, R.drawable.ship_0006, R.drawable.ship_0007};    //비행정 이미지

    SoundPool soundPool;        //SoundPool 클래스는 메모리에 적재된 오디오 데이터를 빠르게 재생하고 관리하는 기능을 제공    ,   사운드 연타 구현 가능,  짧은 사운드 재생에 효과적 (10초내),  긴 사운드 재생불가


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mediaPlayer = MediaPlayer.create(this, R.raw.robby_bgm);        //노래 설정(추가?)
        mediaPlayer.setLooping(true);       //반복 설정
        mediaPlayer.start();            // 노래 시작
        soundPool = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE,0); // SoundPool (int maxStreams, int streamType, int srcQuality)  자세한건 MainActivity
        effectId = soundPool.load(this,R.raw.reload_sound,1);       //load()함수를 이용해 재생시킬 파일 호출
        startBtn = findViewById(R.id.startBtn);          // 게임 시작 버튼
        guideTv = findViewById(R.id.guideTv);            //캐릭터 선택 TextView


        for(int i = 0; i < imgView.length; i++){
            imgView[i] = findViewById(img_id[i]);
            int index = i;
            imgView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    characterId = img[index];       //characterId 값을 img 위치 값으로 변환 (R.drawable.ship_0000) 등
                    startBtn.setVisibility(View.VISIBLE);       //모습 보임
                    startBtn.setEnabled(true);                  //버튼 활성화
                    startBtn.setImageResource(characterId);     //characterId 의 위치의 이미지 보여줌
                    guideTv.setVisibility(View.INVISIBLE);      //모습 감춤
                    soundPool.play(effectId,1,1,0,0,1.0f); //play (int soundID,float leftVolume, float rightVolume,  int priority, int loop,   float rate)  자세한건 MainActivity
                }
            });
        }

        init();

        }

        private void init(){
            findViewById(R.id.startBtn).setVisibility(View.GONE);       //버튼이 화면에 사라지고 비활성화
            findViewById(R.id.startBtn).setEnabled(false);              //버튼 비활성화
            findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(StartActivity.this,MainActivity.class);               // 메인으로 character 로 characterId를 넘김
                    intent.putExtra("character",characterId);
                    startActivity(intent);
                    finish();       //액티비티의 종료
                }
            });
        }

    @Override
    protected void onDestroy() {        //액티비티가 종료되기전 호출되는 메소드
        super.onDestroy();
        if(mediaPlayer != null){        //mediaPlayer 에 값이 있으면 없애 노래를 끔
            mediaPlayer.release();      //메모리 해제
            mediaPlayer = null;
        }
    }
}
