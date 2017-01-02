package com.raosoftware.clientserverchatapp.cscaserver;

import java.io.*;
import java.net.*;

/*
 * Created by urao on 12/29/16.
*/

public class ClientServerThread extends Thread
{
    private ChatServer       server    = null;
    private Socket socket    = null;
    private int              ID        = -1;
    private DataInputStream streamIn  =  null;
    private DataOutputStream streamOut = null;
    private String      ipaddress = "";

    ClientServerThread(ChatServer _server, Socket _socket)
    {
        super();
        server = _server;
        socket = _socket;
        ID     = socket.getPort();
        ipaddress = socket.getInetAddress().getHostAddress();
    }

    void send(String msg)
    {
        try
        {
            streamOut.writeUTF(msg);
            streamOut.flush();
        }
        catch(IOException ioe)
        {
            System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
        }
    }

    int getID()
    {
        return ID;
    }

    String getIpaddress()
    {
        return ipaddress;
    }

    public void run()
    {
        System.out.println("Server Thread " + ID + " running.");
        boolean bool = true;
        while (bool)
        {
            try
            {
                server.handle(ID, streamIn.readUTF());
            }
            catch(IOException ioe)
            {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                bool = false;
            }
        }
    }

    void open() throws IOException
    {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    void close() throws IOException
    {
        if (socket != null)    socket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }

}
