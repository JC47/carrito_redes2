/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PracticaTienda;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import static utils.Codes.*;
import static utils.Strings.CLIENT_PATH;
import static utils.Strings.SERVER_PATH;
import utils.TCPTransfer;

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
                ArrayList<Item> stock=getStock();
                ArrayList<File> images=getPics(directorio);
                ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
                TCPTransfer transfer=new TCPTransfer(oos,ois);
                int requestCode=0;
                do{
                    try{
                        requestCode=ois.readInt();
                    }catch(IOException e){
                        requestCode=0;
                    }
                    
                    switch(requestCode){
                        case REQUEST_DOWNLOAD:sendPics(images,transfer);
                             break;
                        case REQUEST_GET_STOCK:sendStock(stock,oos,ois);
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

    /**
     * 
     * @return List of all avilable stock
     */
    private static ArrayList<Item> getStock() {
        Item i=new Item(1,"p1","p1",10,100);
        Item i2=new Item(2,"p2","p2",10,200);
        Item i3=new Item(3,"p3","p3",10,300);
        ArrayList<Item> list=new ArrayList();
        list.add(i);
        list.add(i2);
        list.add(i3);
        return list;
    }

    /**
     * 
     * @param path path of the pics
     * @return list of all pics
     */
    private static ArrayList<File> getPics(String path) {
        ArrayList<File> files=new ArrayList();
        File dir=new File(path);
        if(dir.isDirectory()){
            File []lectura=dir.listFiles(IMAGE_FILTER);
            files.addAll(Arrays.asList(lectura));
        }
        return files;
    }
    
    static final String[] EXTENSIONS = new String[]{
        "gif", "png", "bmp","jpg","jpeg" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    private static void sendPics(ArrayList<File> pics,TCPTransfer transfer) {
        System.out.println("Preparado para enviar imagenes");
        for(File pic:pics){
            try {
                transfer.getOutput().writeInt(REQUEST_UPLOAD);
                transfer.getOutput().flush();
                transfer.sendFile(pic);
                int result=transfer.getInput().readInt();
                if(result==ERROR)
                    System.out.println("Error al enviar el archivo: "+pic.getName()+" verifique e intente de nuevo");
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            transfer.getOutput().writeInt(TASK_COMPLETE);
            transfer.getOutput().flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Se enviaron todas las imagenes");
        
    }

    private static void sendStock(ArrayList<Item> stock, ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            System.out.println("Enviando catálogo");
            for(Item item:stock){
                oos.writeInt(REQUEST_UPLOAD);
                oos.flush();
                System.out.println("Enviando artículo: "+item.getName());
                oos.writeObject(item);
                ois.readInt();
            }
            oos.writeInt(TASK_COMPLETE);
            oos.flush();
            System.out.println("Catálogo enviado");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
}
