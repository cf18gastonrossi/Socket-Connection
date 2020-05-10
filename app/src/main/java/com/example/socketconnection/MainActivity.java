package com.example.socketconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final int PORT = 5000;
    private static String SERVER = "3.93.65.42";
    private static Button connectButton;
    private static Button changeServerIPButton;
    private static TextView serverIPText;
    private static Boolean isNotificationsEnabled = true;
    private static Toast activeServerToast;
    private static Button notificationsButton;
    private static Button sendCommandButton;
    private static TextView commandText;
    private static Socket socket;
    private static TextView serverMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.connectionButton);
        changeServerIPButton = findViewById(R.id.changeServerIPButton);
        serverIPText = findViewById(R.id.serverIPText);
        notificationsButton = findViewById(R.id.notificationsButton);
        sendCommandButton = findViewById(R.id.sendButton);
        commandText = findViewById(R.id.commandText);
        serverMessage = findViewById(R.id.serverMessage);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryConnection(getApplicationContext());
            }
        });

        changeServerIPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SERVER = serverIPText.getText().toString();
                makeToast(getApplicationContext(), "Server IP succesfully changed to " + SERVER);
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotificationsEnabled) {
                    isNotificationsEnabled = false;
                    notificationsButton.setText("Enable notifications");
                } else {
                    isNotificationsEnabled = true;
                    notificationsButton.setText("Unable notifications");
                }
            }
        });

        sendCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendCommand(commandText.getText().toString(), getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void sendCommand(String message, Context context) throws IOException {
        socket = new Socket(SERVER, PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Dipòsit per imprimir les dades del servidor
        PrintStream output = new PrintStream(socket.getOutputStream());

        //Enviament al servidor de la petició del client
        output.println(message);

        //Recollida des del servidor de la resposta a la petició
        String st = input.readLine();

        //Imprimim la resposta per pantalla
        if (st != null) {
            serverMessage.setText("Servidor> " + st);
            serverMessage.setVisibility(View.VISIBLE);
        }
        //Sortida del programa
        if (message.equals("exit")) {
            makeToast(context, "**** Fi Client ****");
        }
        //Sortida del programa
        if (message.equals("stop")) {
            makeToast(context, "**** Sayonara server ****");
        }
        socket.close();
    }

    public static void tryConnection(Context context) {
        //Socket per la comunicació client-servidor
        try {
            System.out.println("*** Inici Client ***");
            //Inicialitzem la connexió amb el servidor
            socket = new Socket(SERVER, PORT);
            connectButton.setBackgroundColor(Color.GREEN);
            connectButton.setText("Connected");
            makeToast(context, "Server connected");
            socket.close();
        } catch (IOException ex) {
            connectButton.setBackgroundColor(Color.RED);
            connectButton.setText("Try Again");
            makeToast(context, "Server not connected");
            System.err.println("Client> Excepció: " + ex.getMessage());
        }
    }

    public static void makeToast(Context context, String message) {
        if (isNotificationsEnabled) {
            activeServerToast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
