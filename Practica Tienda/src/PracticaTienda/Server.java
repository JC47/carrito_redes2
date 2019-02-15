/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PracticaTienda;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static utils.Codes.*;
import static utils.Strings.CLIENT_PATH;
import static utils.Strings.SERVER_PATH;

/**
 *
 * @author dany
 */
public class Server {
    public static void main(String []args){
        String directorio=getDir();
        try {
            ServerSocket ss=new ServerSocket(SERVER_REQUEST_PORT);
            for(;;){
                Socket s=ss.accept();
                ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
                int requestCode=0;
                do{
                    try{
                        requestCode=ois.readInt();
                    }catch(IOException e){
                        requestCode=0;
                    }
                    
                    switch(requestCode){
                        case REQUEST_DOWNLOAD://Enviar imagenes
                             break;
                        case REQUEST_GET_STOCK://Enviar Lista de productos
                            break;
                        case REQUEST_BUY://Comprar y enviar ticket
                            break;
                    }
                }while(requestCode!=REQUEST_CLOSE);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getDir() {
        String directorio = System.getProperty("user.dir");
        directorio+=SERVER_PATH;
        System.out.println(directorio);
        File folder=new File(directorio);
        folder.mkdir();
        if(folder.exists())
            if(folder.isDirectory())
                System.out.println("Carpeta creada");
        return directorio;
        }
    
}
