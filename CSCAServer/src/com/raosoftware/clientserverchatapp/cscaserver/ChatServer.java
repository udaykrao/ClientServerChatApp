package com.raosoftware.clientserverchatapp.cscaserver;

/*
 * Created by urao on 12/29/16.
*/

import java.net.*;
import java.io.*;

public class ChatServer implements Runnable
{
    private ClientServerThread clients[] = new ClientServerThread[50];
    private ServerSocket server = null;
    private Thread       thread = null;
    private int clientCount = 0;

    private ChatServer(int port)
    {
        try
        {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        }
        catch (IOException ioe)
        {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                System.out.println("Waiting for a client ...");
                addThread(server.accept());
            }
            catch(IOException ioe)
            {
                System.out.println("Server accept error: " + ioe); stop();
            }
        }
    }

    private void start()
    {
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    private void stop()
    {
        if (thread != null)
        {
            thread = null;
        }
    }

    private int findClient(int ID)
    {
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }

    synchronized void handle(int ID, String input)
    {
        String player;
        if (input.equals(".bye"))
        {
            clients[findClient(ID)].send(".bye");
            remove(ID);
        }
        else if (input.startsWith("CSCAMsg01: "))
        {
            player = input.substring(11);
            clients[findClient(ID)].setName(player);
            int pos = findClient(ID);
            String ipaddress = clients[findClient(ID)].getIpaddress();
            System.out.println("Adding client thread " + pos + ": " + player + " (" + ID + " at " + ipaddress + ")");
        }
        else
        {
            player = clients[findClient(ID)].getName();
            for (int i = 0; i < clientCount; i++)
                clients[i].send(player + "(" + ID + "): " + input);
        }
    }

    synchronized void remove(int ID)
    {
        int pos = findClient(ID);
        String player = clients[findClient(ID)].getName();
        String ipaddress = clients[findClient(ID)].getIpaddress();
        if (pos >= 0)
        {
            ClientServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + pos + ": " + player + " (" + ID + " at " + ipaddress + ")");
            if (pos < clientCount-1)
                for (int i = pos+1; i < clientCount; i++)
                    clients[i-1] = clients[i];
            clientCount--;
            try
            {
                toTerminate.close();
            }
            catch(IOException ioe)
            {
                System.out.println("Error closing thread: " + ioe);
            }
        }
    }

    private void addThread(Socket socket)
    {
        if (clientCount < clients.length)
        {
            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new ClientServerThread(this, socket);
            try
            {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            }
            catch(IOException ioe)
            {
                System.out.println("Error opening thread: " + ioe);
            }
        }
        else
            System.out.println("Client refused: maximum " + clients.length + " reached.");
    }

    public static void main(String args[])
    {
        ChatServer server = null;
        if (args.length != 1)
        {
            System.out.println("Usage: java ChatServer port");
            server = new ChatServer(59000);
        }
        else
            server = new ChatServer(Integer.parseInt(args[0]));
    }
}