package com.unanimous.recordaudio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;



import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button startRecord, stopRecord, playRecord, stopPlay;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    final int REQUEST_PERMISSION_CODE = 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Initialize View
        startRecord = (findViewById(R.id.captureAudio));
        stopRecord = (findViewById(R.id.stoprecording));
        playRecord = (findViewById(R.id.playaudio));
        stopPlay = (findViewById(R.id.stopaudio));

        //Check Permissions
        if(checkAccessPermission()){
            startRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pathSave = Environment.getExternalStorageDirectory()
                            .getAbsolutePath()+"/"
                            + UUID.randomUUID().toString()+ "_audio_record.3gp";
                    setUpMediaRecorder();
                        try{
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    playRecord.setEnabled(false);
                    stopPlay.setEnabled(false);

                    Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_LONG).show();
                }
            });

            stopRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaRecorder.stop();
                    stopRecord.setEnabled(false);
                    playRecord.setEnabled(true);
                    startRecord.setEnabled(true);
                    stopPlay.setEnabled(false);
                }
            });


            playRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    stopPlay.setEnabled(true);
                    startRecord.setEnabled(false);
                    stopRecord.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try{
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Plaing Recorded Audio", Toast.LENGTH_LONG).show();
                }
            });


            stopPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playRecord.setEnabled(true);
                    startRecord.setEnabled(true);
                    stopRecord.setEnabled(false);
                    stopPlay.setEnabled(false);

                    if(mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setUpMediaRecorder();
                    }
                }
            });
        }
        else{
            requestPermission();
        }


    }

    private void setUpMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, " Permission Granted", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(this, " Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
            break;


        }

    }
    private boolean checkAccessPermission() {
        int write_permission_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_permission_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


}
