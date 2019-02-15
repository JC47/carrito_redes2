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
import java.util.logging.Level;
import java.util.logging.Logger;
import static utils.Codes.*;
import static utils.Strings.*;
import utils.TCPTransfer;

/**
 *
 * @author dany
 */
public class Client {
    
    ObjectOutputStream oos;
    ObjectInputStream ois;
    TCPTransfer transfer;
    String directorio;
    
    public Client(ObjectOutputStream oos,ObjectInputStream ois){
        this.oos=oos;
        this.ois=ois;
        transfer=new TCPTransfer(oos,ois);
        directorio=getFilesDir();
    }
    
    public void getFiles(){
        int requestCode=0;
        do{
            try{
                requestCode=ois.readInt();
                
            }catch(IOException e){
                requestCode=0;
            }
            if(requestCode==REQUEST_UPLOAD){
                try {
                    int result=transfer.getFile(directorio);
                    System.out.println("Enviando resultado al cliente: "+result);
                    oos.writeInt(result);
                    oos.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }while(requestCode!=TASK_COMPLETE);
    }

    private String getFilesDir() {
        String directorio = System.getProperty("user.dir");
        directorio+=CLIENT_PATH;
        System.out.println(directorio);
        File folder=new File(directorio);
        folder.mkdir();
        if(folder.exists())
            if(folder.isDirectory())
                System.out.println("Carpeta creada");
        return directorio;
    }
    
}