/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PracticaTienda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Codes;
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
                System.out.println("Preparado para nueva conexión");
                Socket s=ss.accept();
                ArrayList<Item> stock=getStock();
                ArrayList<File> images=getPics(stock);
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
                        case REQUEST_BUY:buy(stock,oos,ois,transfer);
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
        int cont=1;
        ArrayList<Item> list=new ArrayList();
        try{
            File f = new File(".\\src\\img\\productos\\listaProductos");
            FileInputStream fis = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String linea = br.readLine();
            while(linea!=null && !"".equals(linea)){
                System.out.println(linea);
                String params[];
                params = linea.split(",");
                Item i=new Item(cont++,params[0],params[1],Integer.parseInt(params[2]),Integer.parseInt(params[3]));
                list.add(i);
                linea= br.readLine();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(list.size());
//        Item i=new Item(1,"p1","p1",10,100);
//        Item i2=new Item(2,"p2","p2",10,200);
//        Item i3=new Item(3,"p3","p3",10,300);
//        list.add(i);
//        list.add(i2);
//        list.add(i3);
        return list;
    }

    /**
     * 
     * @param path path of the pics
     * @return list of all pics
     */
    private static ArrayList<File> getPics(ArrayList<Item> imags) {
        ArrayList<File> files=new ArrayList();
        for(int i=0;i<imags.size();i++){
            File dir=new File(imags.get(i).getPic());
            files.add(dir);
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
        /*try {
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
            }*/
        try {
            oos.writeObject(new ItemList(stock));
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static void buy(ArrayList<Item> stock, ObjectOutputStream oos, ObjectInputStream ois,TCPTransfer transfer) {
        try {
            ItemList list=(ItemList) ois.readObject();
            ArrayList<Item> cart=list.getList();
            for(Item i:cart){//Descuenta los artíclulos del stock de la tienda
                //boolean flag=false;
                for(Item a:stock){
                    if(i.getId()==a.getId()){
                        a.setStock(a.getStock()-i.getStock());
                        break;//Si encuentra el artíclulo detiene la iteración
                    }
                }
            }
            File ticket=generateTicket(stock);
            oos.writeInt(TASK_COMPLETE);
            //Se envía el ticket
            oos.writeUTF(ticket.getName());
            oos.flush();
            transfer.sendFile(ticket);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static File generateTicket(ArrayList<Item> cart) {
        File ticket=new File(getDir()+"ticket.txt");
        try {
            FileWriter fw=new FileWriter(ticket);
            BufferedWriter br=new BufferedWriter(fw);
            float total=0;
            for(Item i:cart){
                String line="Artículo: "+i.getName()+" Cantidad: "+i.getStock()+" Precio: "+i.getPrice()+"\n";
                total=total+(i.getStock()*i.getPrice());
                br.append(line);
                br.flush();
            }
            br.append("\n\nTotal: "+total);
            br.flush();
            br.close();
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ticket;
    }

    
    
}
