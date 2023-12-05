package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaRecorder recorder;
    private MediaPlayer player;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean grabando = false;
    private boolean reproduciendo = false;
    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000; //Tiempo de retroceso (milisegundos)
    private int backwardTime = 5000;

    private Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {

            if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0) {

                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }

        }
        if (!permissionToRecordAccepted) {

            Toast.makeText(this, "Permisos de grabar no aceptados", Toast.LENGTH_LONG).show();
            finish();

        } else {

            Toast.makeText(this, "Permisos de grabar aceptados", Toast.LENGTH_LONG).show();
            startRecording();
        }

    }


    private void startPlaying() {

        player = new MediaPlayer();

        try {
            player.setDataSource(getFilesDir().getAbsolutePath() + File.separator + "Grabacion.3gp");
            player.prepare();
            player.start();
        } catch (IOException e) {

            Toast.makeText(this, "Error. No se ha podido comenzar la reproducción", Toast.LENGTH_LONG).show();
        }
    }


    private void stopPlaying() {
        player.release();
        player = null;
    }


    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            //Inicializamos y añadimos la configuración
            recorder = new MediaRecorder();
            //Seleccionamos la fuente de audio micrófono
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //Seleccionamos el formato de salida (3GP)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //Buscamos la ruta para guardar la grabación
            recorder.setOutputFile(getFilesDir().getAbsolutePath() + File.separator + "Grabacion.3gp");
            //Seleccionamos el codificador
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {

                recorder.prepare();

            } catch (IOException excepcion) {
                Toast.makeText(this, "Error. No se ha podido comenzar la grabación", Toast.LENGTH_LONG).show();

            }

            recorder.start();

        } else {
            //Si no tenemos el permiso hacemos una petición

            Toast.makeText(this, "No tenemos el permiso, asi que hacemos petición", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void stopRecording() {
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;

    }


    public void onClick(View view) {
        Button botonGrabar = findViewById(R.id.botonGrabar);
        Button botonReproducir = findViewById(R.id.botonReproducir);
        TextView txt1=findViewById(R.id.txt1);
        TextView txt2=findViewById(R.id.txt2);

        if (R.id.botonGrabar == view.getId()) {

            if (grabando) {
                stopRecording();
                botonGrabar.setText("●");
                botonGrabar.setTextColor(getResources().getColor(R.color.colorRojo));
            } else {

                startRecording();
                botonGrabar.setText("■");
                botonGrabar.setTextColor(getResources().getColor(R.color.white));
            }
            grabando = !grabando;

        } else if (R.id.botonReproducir == view.getId()) {


            if (reproduciendo) {

                stopPlaying();

                botonReproducir.setText("▶");
            } else {

                startPlaying();
                botonReproducir.setText("||");
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        reproduciendo = false;
                        botonReproducir.setText("▶");
                    }
                });


                finalTime = player.getDuration();
                startTime = player.getCurrentPosition();

                //mostrar tiempo final del archivo de audio
                txt2.setText(String.format("%d min, %d seg",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -

                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        finalTime)))
                );

                //mostrar tiempo actual de reproducción del archivo de audio
                txt1.setText(String.format("%d min, %d seg",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -

                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime)))
                );
            }
            reproduciendo = !reproduciendo;

        } else if (R.id.botonAvanzar == view.getId()) {
            int temp = (int) startTime;
            if ((temp + forwardTime) <= finalTime) {
                startTime = startTime + forwardTime;
                player.seekTo((int) startTime);
                Toast.makeText(getApplicationContext(), "Has avanado 5 segundos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No avanzar 5 segundos", Toast.LENGTH_SHORT).show();
            }


        } else if (R.id.botonRetroceder == view.getId()) {

            int temp = (int) startTime;
            if ((temp - backwardTime) > 0) {
                startTime = startTime - backwardTime;
                player.seekTo((int) startTime);
                Toast.makeText(getApplicationContext(), "Has retrocedido 5 segundos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No puedes retroceder 5 segundos", Toast.LENGTH_SHORT).show();
            }
        }


    }




}



