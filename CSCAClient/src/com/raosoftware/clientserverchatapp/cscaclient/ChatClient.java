package com.raosoftware.clientserverchatapp.cscaclient;

/*
 * Created by urao on 12/30/16.
*/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends Application
{

    private TextField chatter = new TextField();
    private TextField serverhost = new TextField();
    private TextField serverport = new TextField();
    private TextField message = new TextField();
    private TextArea display = new TextArea();
    private Button bye = new Button("Bye");
    private Button connect = new Button("Connect");
    private Button send = new Button("Send");
    private Socket socket;
    private DataOutputStream streamOut = null;
    private ChatClientThread client    = null;
    private String playerName;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        bye.setDisable(true);
        send.setDisable(true);
        message.setPrefWidth(500);
        display.setWrapText(true);
        HBox bottom = new HBox(40, bye, connect, message, send);
        VBox left = new VBox(10, new Text(""), new Text("Your Name:"), chatter,
                new Text(""), new Text("Server Host:"), serverhost,
                new Text(""), new Text("Server Port:"), serverport);
        Text title = new Text("Sample Client Server Chat App");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        BorderPane chatpane = new BorderPane(display);
        chatpane.setTop(title);
        chatpane.setBottom(bottom);
        chatpane.setLeft(left);
        chatpane.setAlignment(title, Pos.TOP_CENTER);
        chatpane.setMargin(display, new Insets(10));
        chatpane.setMargin(title, new Insets(10));
        chatpane.setMargin(bottom, new Insets(10));
        chatpane.setMargin(left, new Insets(10));
        Scene scene = new Scene(chatpane);
        primaryStage.setScene(scene);
        bye.setOnAction(e -> this.bye());
        send.setOnAction(e -> this.send());
        connect.setOnAction(e -> this.connect());
        primaryStage.show();

    }

    private void bye()
    {
        message.setText(".bye");
        send();
        bye.setDisable(true);
        send.setDisable(true);
        connect.setDisable(false);
        chatter.setDisable(false);
        serverhost.setDisable(false);
        serverport.setDisable(false);
    }

    public void stop()
    {
        Platform.exit();
    }

    private void connect()
    {
        String serverName;
        int serverPort;
        println("Establishing connection. Please wait ...");
        try
        {
            playerName = chatter.getText();
            serverName = serverhost.getText();
            serverPort = Integer.parseInt(serverport.getText());
            if (playerName.length() < 1 || serverName.length() < 5)
            {
                throw new IllegalArgumentException("Invalid Player or Server name(s)");
            }
        }
        catch (Exception e)
        {
            println("Error reading Your name or Server or Port: " + e.getMessage());
            return;
        }
        try
        {
            socket = new Socket(serverName, serverPort);
            println("Connected: " + socket);
            open();
            send.setDisable(false);
            connect.setDisable(true);
            bye.setDisable(false);
            chatter.setDisable(true);
            serverhost.setDisable(true);
            serverport.setDisable(true);
        }
        catch(UnknownHostException uhe)
        {
            println("Host unknown: " + uhe.getMessage());
        }
        catch(IOException ioe)
        {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }

    private void send()
    {
        try
        {
            streamOut.writeUTF(message.getText());
            streamOut.flush();
            message.setText("");
        }
        catch(IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    void handle(String msg)
    {
        if (msg.equals(".bye"))
        {
            println("Good bye. Please close window ...");
            close();
        }
        else
            println(msg);
    }

    private void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new ChatClientThread(this, socket);
            message.setText("CSCAMsg01: " + playerName);
            send();
        }
        catch(IOException ioe)
        {
            println("Error opening output stream: " + ioe);
        }
    }

    private void close()
    {
        try
        {
            if (streamOut != null)  streamOut.close();
            if (socket    != null)  socket.close();
        }
        catch(IOException ioe)
        {
            println("Error closing ...");
        }
        client.close();
        client = null;
    }

    private void println(String msg)
    {
        display.appendText(msg + "\n");
    }


}
