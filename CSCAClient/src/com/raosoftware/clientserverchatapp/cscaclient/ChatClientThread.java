package com.raosoftware.clientserverchatapp.cscaclient;

/*
 * Created by urao on 12/30/16.
*/

import java.net.*;
import java.io.*;

public class ChatClientThread extends Thread
{
    private Socket           socket   = null;
    private ChatClient       client   = null;
    private DataInputStream  streamIn = null;

    ChatClientThread(ChatClient _client, Socket _socket)
    {
        client   = _client;
        socket   = _socket;
        open();
        start();
    }

    private void open()
    {
        try
        {
            streamIn  = new DataInputStream(socket.getInputStream());
        }
        catch(IOException ioe)
        {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    void close()
    {
        try
        {
            if (streamIn != null) streamIn.close();
        }
        catch(IOException ioe)
        {
            System.out.println("Error closing input stream: " + ioe);
        }
    }

    public void run()
    {
        boolean bool = true;
        while (bool)
        {
            try
            {
                client.handle(streamIn.readUTF());
            }
            catch(IOException ioe)
            {
                System.out.println("Listening error: " + ioe.getMessage());
                client.stop();
                bool = false;
            }
        }
    }
}