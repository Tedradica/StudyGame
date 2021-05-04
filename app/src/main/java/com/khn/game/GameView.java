package com.khn.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class GameView extends View {

    //screen informations
    int screenWidth, screenHeight;
    int ground_Height;
    String touch_position;
    boolean buttonStatus;
    float get_X = -1;
    private GameView.GameThread T;
    Paint p = new Paint();

    //character informations
    int character_width, character_Height;
    int character_x, character_y;
    int speed = 3;
    float movement_senstive = 100; // 1%

    //wepon informations
    boolean attack_status = false;
    int wepon_width, wepon_Height;
    int wepon_x, wepon_y;
    int wepon_speed = 10;

    //ball informations
    int[] ball_speed_list = {-5, -4, -3, -2};
    int ball_speed_init = 3;

    int[] ball_bouns = new int[] {screenHeight / 6, screenHeight / 6, screenHeight / 6, screenHeight / 6};
    Bitmap character = BitmapFactory.decodeResource(getResources(), R.drawable.character);
    Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
    Bitmap wepon = BitmapFactory.decodeResource(getResources(), R.drawable.wepon);
    Bitmap[] ball_info = {BitmapFactory.decodeResource(getResources(), R.drawable.ball1),
            BitmapFactory.decodeResource(getResources(), R.drawable.ball2),
            BitmapFactory.decodeResource(getResources(), R.drawable.ball3),
            BitmapFactory.decodeResource(getResources(), R.drawable.ball4)};
    List<BallF> balls = new ArrayList<BallF>();

    //Game status
    boolean hit = false;
    boolean complete = false;

    public GameView(Context con, AttributeSet at){
        super(con, at);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenWidth = w;
        this.screenHeight = h;

        //definition object size
        character_width = character.getWidth();
        character_Height = character.getHeight();
        ground_Height = ground.getHeight();
        wepon_width = wepon.getWidth();
        wepon_Height = wepon.getHeight();
        character_x = screenWidth/2 - (character_width/2);
        character_y = screenHeight - ground.getHeight() - character_Height;

        //first_ball
        balls.add(new BallF(screenWidth/2, ball_bouns[0], -3, 5, ball_speed_list, ball_info[0], 0));

        if (T == null){
            T = new GameThread();
            T.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        T.run = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        //Game Text
        p.setColor(Color.BLACK);   // Text Color
        p.setTextSize(screenHeight / 10);

        if (hit) {
            canvas.drawText("Mission Fail.", (float) (screenWidth / 2.7),screenHeight/2, p);
            T.run = false;
        }
        if (complete) canvas.drawText("Mission Complete.", (float) (screenWidth / 3),screenHeight/2, p);

        //wepon X value setting
        if (attack_status == false) {
            wepon_x = character_x + (character_width - character_width / 2 ) - (wepon_width - wepon_width / 2);
            wepon_y = screenHeight - character_Height - ground.getHeight();
        }

        //wepon, ball, character, ground draw
        if (attack_status) canvas.drawBitmap(wepon, wepon_x, wepon_y, null);
        for ( int i = 0 ; i < balls.size() ; i ++){
            canvas.drawBitmap(ball_info[balls.get(i).get_status()], balls.get(i).get_x(), balls.get(i).get_y(), null);
        }
        canvas.drawBitmap(character, character_x, character_y, null);
        canvas.drawBitmap(ground, 0, screenHeight - ground.getHeight(), null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            get_X = event.getX();
            buttonStatus = true;

            if ( hit == true ){
                Activity activity = (Activity)getContext();
                activity.finish();
                //--

            }

        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if ( event.getX() <  ( (float) get_X - screenWidth / movement_senstive) ) {
                //왼쪽
                touch_position = "Left";
            } else if (event.getX() > ( (float) get_X + screenWidth / movement_senstive) ) {
                //오른쪽
                touch_position = "Right";
            } else {
                touch_position = "";
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            buttonStatus = false;

            //이동상태가 아닐경우에 손을 떼면 공격한다.
            if(touch_position == ""){
                attack_status = true;
            }

        }

        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                //Multi touch case
                attack_status = true;
                break;

        }

        return true;
    }

    public void Action () {

        //character movement
        if (touch_position == "Left" && buttonStatus == true){
            if (character_x > 0) {
                character_x -= speed;
            }
        } else if (touch_position == "Right" && buttonStatus == true){
            if (character_x < screenWidth - character_width) {
                character_x += speed;
            }
        }

        //attack
        if (attack_status == true) {
            if (wepon_y > 0){
                wepon_y -= wepon_speed;
                Log.d("DEBUG", "어택발생 x:" + wepon_x +  "  y:" +wepon_y );

            }
            if (wepon_y < 0){
                attack_status = false;
            }
        }

        //ball Movement
        for ( int i = 0 ; i < balls.size() ; i ++){
            Log.d("debug", "값 : " +  balls.get(i).get_y());
            if( balls.get(i).get_x() < 0 || balls.get(i).get_x() > screenWidth - balls.get(i).get_width() ){
                balls.get(i).set_to_x( balls.get(i).get_to_x() * -1 );
            }
            if( balls.get(i).get_y() > screenHeight - (ground_Height + balls.get(i).get_height()) ){
                balls.get(i).set_to_y(balls.get(i).get_speed_y());
            } else if (balls.get(i).get_y() < 0) {

                balls.get(i).set_to_y( balls.get(i).get_speed_y() * -1 );
            } else {
                balls.get(i).set_to_y( (float) (balls.get(i).get_to_y() + 0.01));
            }

            balls.get(i).set_x( balls.get(i).get_x() + balls.get(i).get_to_x() );
            balls.get(i).set_y( balls.get(i).get_y() + balls.get(i).get_to_y() );

            //충돌처리(캐릭터)
            if ( balls.get(i).collision( balls.get(i).get_x(), balls.get(i).get_y(), balls.get(i).get_width(), balls.get(i).get_height(), character_x, character_y, character_width, character_Height) ){
                Log.d("debug", "충돌! (캐릭터)");
                hit = true;
            }

            //충돌처리(총알)
            if ( balls.get(i).collision( balls.get(i).get_x(), balls.get(i).get_y(), balls.get(i).get_width(), balls.get(i).get_height(), wepon_x, wepon_y, wepon_width, wepon_Height) && attack_status == true ){
                attack_status = false;
                int status = balls.get(i).get_status();
                if(status < 3) {
                    balls.add(new BallF(balls.get(i).get_x(), balls.get(i).get_y(), ball_speed_init, -1, ball_speed_list, ball_info[status + 1], status + 1));
                    balls.add(new BallF(balls.get(i).get_x(), balls.get(i).get_y(), ball_speed_init * -1, -1, ball_speed_list, ball_info[status + 1], status + 1));
                }
                balls.remove(i);
            }

            if (balls.size() == 0){
                complete = true;
            }
        }

    }

    class GameThread extends Thread {
        public boolean run = true;

        public void run() {
            while (run) {
                try {
                    postInvalidate();
                    Action();
                    sleep(5);
                } catch ( Exception e ) {

                }
            }
        }
    }

}
