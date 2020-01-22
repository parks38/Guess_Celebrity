package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// Celebrity name guessing game
// check if you know all the top 100 celebrity, guess their names and keep track of
// how many celebrities you can get them right.
// uses downloading images through url instead of other methods.

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    String[] answers= new String[4];
    int locationOfCorrectAnswer = 0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    //compare which celeb was chosen, check if it is the right/wrong celeb
    //update a message that shows whether the user got the answer correct
    public void celebChosen (View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(), "Correct!",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebNames.get(chosenCeleb),Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }


    //allows to download the website into our game
    // convert url to string
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        //returns a string of url
        protected String doInBackground(String... urls) {
            //create basic setup
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                //try convert string into an actual url
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    //allows to download image to the program
    // converts string to image that appears on the UI
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                 URL url = new URL(urls[0]);
                 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                 connection.connect();
                 InputStream inputStream = connection.getInputStream();
                 Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                 return myBitmap;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        DownloadTask task = new DownloadTask();
        String result = null;

        try{
            result = task.execute("http://www.posh24.se/kandisar").get();
            // break up website code (split on string) to collect only information needed
            String[] splitResult = result.split("<div class=\"listedArticles\">");
            // find image sources html code
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebURLs.add(m.group(1));
            }
            // find celebrity name of the image source
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){
                celebNames.add(m.group(1));
            }

            newQuestion();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //generate new question every time single game is finished
    public void newQuestion() {
        try {
            // create random celebrities to generate new questions
            Random rand = new Random();
            chosenCeleb = rand.nextInt(celebURLs.size());

            ImageDownloader imageTask = new ImageDownloader();
            // allow image of the celeb to show on the game
            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

            //choose location of the correct answer
            locationOfCorrectAnswer = rand.nextInt(4);
            //create variable of the incorrect answer
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                //check if i is equal to the correct answer
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    //make sure there is no repeats
                    incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    while (incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);

                }
            }

                button0.setText(answers[0]);
                button1.setText(answers[1]);
                button2.setText(answers[2]);
                button3.setText(answers[3]);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // allows user to start the game when pressing the button of start
    // default screen when opening the game.
    public void start (View view) {

    }
}
