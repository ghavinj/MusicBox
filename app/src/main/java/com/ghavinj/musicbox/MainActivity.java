package com.ghavinj.musicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;
    private ImageView songImageView;
    private TextView songTitleTextView;
    private TextView artistNameTextView;
    private SeekBar seekBar;
    private TextView leftTimeTextView;
    private TextView rightTimeTextView;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTimeTextView.setText(dateFormat.format(new Date(currentPos)));
                rightTimeTextView.setText(dateFormat.format(new Date(duration - currentPos)));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void setUpUI(){ //setup all of the user interface elements

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.futurebass);

        songImageView = (ImageView)findViewById(R.id.songImageView);
        songTitleTextView = (TextView)findViewById(R.id.songTitleTextView);
        artistNameTextView = (TextView)findViewById(R.id.artistNameTextView);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        leftTimeTextView = (TextView)findViewById(R.id.leftTime);
        rightTimeTextView = (TextView)findViewById(R.id.rightTime);
        prevButton = (Button)findViewById(R.id.prevButton);
        playButton = (Button)findViewById(R.id.playButton);
        nextButton = (Button)findViewById(R.id.nextButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){ //if any button is pressed switch the case to that button
            case R.id.prevButton:
                //code
                backMusic();
                break;

            case R.id.playButton:
                //check to see if mediaPlayer is playing if so then pause if not then play music.
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                }else{
                    startMusic();
                }

                break;

            case R.id.nextButton:
                nextMusic();


        }

    }

    public void pauseMusic(){ //create a method to stop the music.

        if (mediaPlayer != null){ // test to see if the mediaPlayer is empty
            mediaPlayer.pause(); // if the mediaPlayer is not empty and it is playing, pause it.
            playButton.setBackgroundResource(android.R.drawable.ic_media_play); //change the pause icon to a play icon on the button.
        }

    }

    public void startMusic(){
        // create a method to start the music.
        if (mediaPlayer != null){ // test to see if the mediaPlayer is empty.
            mediaPlayer.start(); // if the mediaPlayer is not empty start the mediaPlayer.
            updateThread(); // update the seekBar and time TextView thread changes.
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause); // change the play icon to a pause icon.
        }
    }

    public void backMusic(){
        if (mediaPlayer.isPlaying()){
            //bring it back to zero
            mediaPlayer.seekTo(0);
        }
    }

    public void nextMusic(){
        //bring media player to the end of the song for now
        mediaPlayer.seekTo(mediaPlayer.getDuration());
    }

    public void updateThread(){
        //create a method to start a thread for updating the seekBar and time TextViews.
        thread = new Thread(){
            @Override
            public void run() {

                //create a while loop while the media player is not empty and running to update seekBar and time TextView
                while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    //try and see if letting the thread sleep for 50 milliseconds and then invoking the run method Override will work.
                    try {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //update the seekBar
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                //update the progress and duration TextViews
                                leftTimeTextView.setText(String.valueOf(new SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getCurrentPosition()))));
                                rightTimeTextView.setText(String.valueOf(new SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));
                            }
                        });


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //Start the thread.
        thread.start();

    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        
        super.onDestroy();
    }
}
