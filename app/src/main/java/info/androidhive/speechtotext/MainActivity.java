package info.androidhive.speechtotext;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.sql.DriverManager.println;


public class MainActivity extends Activity {

    /**
     * Declarations
     */
    private TextView txtSpeechInput;

    private ImageButton btnSpeak;

    String str;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private String serverIpAddress = "";

    private boolean connected = false;

    TextView textIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar
        getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    str = result.get(0);
                    setContentView(R.layout.activity_main);
                    Log.d("1- naval", " client oncreate");

                    Button button = (Button) findViewById(R.id.send);
                    textIn = (TextView) findViewById(R.id.textin);
                    /**
                     * Setting the text box with default value
                     */
                    textIn.setText(str);
                    Log.d("settext", " 2-naval");
                    /**
                     * Here we need to fill in textin from MainActivity,
                     * where we received the speech API text
                     */
                    button.setOnClickListener(new View.OnClickListener() {

                                                  @Override
                                                  public void onClick(View arg0) {
                                                      if (!connected) {
                                                          serverIpAddress = "192.168.0.4";
                                                          if (!serverIpAddress.equals("")) {
                                                              Thread cThread = new Thread(new ClientThread());
                                                              cThread.start();
                                                          }
                                                      }
                                                  }
                                              }
                    );


                }
            }
        }
    }

    public class ClientThread implements Runnable {
        Socket socket = null;


        public void run() {
            try {
                socket = new Socket("192.168.0.4", 2222); //use the IP address of the server

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                oos.writeObject(str);

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

        }
    }
}












