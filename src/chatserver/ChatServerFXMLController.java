/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 *
 * @author kevinbudd
 */
public class ChatServerFXMLController implements Initializable {
    
    @FXML
    private TextArea txtChat;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        new Thread( () -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8005);
        txtChat.appendText("MultiThreadServer started at " 
          + new Date() + '\n');
    
        while (true) {
          // Listen for a new connection request
          Socket socket = serverSocket.accept();
    
          Platform.runLater( () -> {
            // Find and display the client's host name, and IP address
            InetAddress inetAddress = socket.getInetAddress();
            txtChat.appendText("New clients host name is "
              + inetAddress.getHostName() + " IP Address is "
              + inetAddress.getHostAddress() + " " 
              + serverSocket.getLocalPort() + " "
                    + new Date() + '\n');
            
          });
          
          // Create and start a new thread for the connection
          new Thread(new HandleAClient(socket)).start();
        }
      }
      catch(IOException ex) {
        System.err.println(ex);
      }
    }).start();
  }
        
  // Define the thread class for handling new connection
  class HandleAClient implements Runnable {
    private Socket socket; // A connected socket

    /** Construct a thread */
    public HandleAClient(Socket socket) {
      this.socket = socket;
    }

    /** Run a thread */
    @Override
    public void run() {
      try {
        // Create data input and output streams
        DataInputStream inputFromClient = new DataInputStream(
          socket.getInputStream());
        DataOutputStream outputToClient = new DataOutputStream(
          socket.getOutputStream());

        // Continuously serve the client
        while (true) {
          
          // Receive a message from a client
          String message = inputFromClient.readUTF();

          // broadcast message to all 
          outputToClient.writeUTF(message + '\n');
          
          Platform.runLater(() -> {
            //txtChat.appendText(name + ": " + message + '\n');
          });
        }
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}