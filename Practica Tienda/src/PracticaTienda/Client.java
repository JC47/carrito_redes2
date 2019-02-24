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
import java.util.ArrayList;
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
    ArrayList <Item> cart;
    
    public Client(ObjectOutputStream oos,ObjectInputStream ois){
        this.oos=oos;
        this.ois=ois;
        transfer=new TCPTransfer(oos,ois);
        directorio=getFilesDir();
        cart=new ArrayList();
    }
    
    public void getFiles() throws IOException{
        int requestCode=0;
        oos.writeInt(REQUEST_DOWNLOAD);
        oos.flush();
        do{
            try{
                requestCode=ois.readInt();
                
            }catch(IOException e){
                requestCode=0;
            }
            if(requestCode==REQUEST_UPLOAD){
                try {
                    int result=transfer.getFile(directorio);
                    System.out.println("Enviando resultado: "+result);
                    oos.writeInt(result);
                    oos.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }while(requestCode!=TASK_COMPLETE);
    }
    
    public ArrayList<Item> getStock() throws IOException{
        System.out.println("Obteniendo Stock");
        ArrayList stock=new ArrayList();
        int requestCode=0;
        oos.writeInt(REQUEST_GET_STOCK);
        oos.flush();
        do{
            try{
                requestCode=ois.readInt();
                
            }catch(IOException e){
                requestCode=0;
            }
            if(requestCode==REQUEST_UPLOAD){
                try {
                    Item i=(Item)ois.readObject();
                    stock.add(i);
                    System.out.println("Artículo recibido: "+i.getName());
                    oos.writeInt(ON_READY_RESULT);
                    oos.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }while(requestCode!=TASK_COMPLETE);
        return stock;
    }
    
    /*public File buy(ArrayList<Item> carrito){
    return     
    }
    
    private File getTicket(){
        
    }*/
    /**
     * 
     * @param i producto a agregar
     * @return lista con el carrito
     */
    public ArrayList<Item> addToCart(Item i){//Agrega un artículo al carrito
        if(cart.isEmpty()){
            cart.add(new Item(i));
        }
        else{
            boolean flag=false;
            for(Item item:cart){
                if(item.getId()==i.getId()){//Si el producto ya se había agregado, se incrementa el contador stock
                    item.setStock(item.getStock()+1);
                    flag=true;
                    break;
                }
            }
            if(!flag){//Si el producto no estaba en el carrito
                cart.add(new Item(i));
            }
        }
        return cart;
    }
    
    public ArrayList<Item>removeFromCart(Item i){
        boolean flag=false;
        for(Item item:cart){
            if(item.getId()==i.getId()){
                flag=true;
                if(item.getStock()==1){//Si sólo había 1 se remueve de la lista
                    cart.remove(item);
                }
                else{
                    item.setStock(item.getStock()-1);//Si había más de uno se resta una unidad al pedido
                }
                break;
            }
        }
        if(!flag){
            System.out.println("El producto seleccionado no estaba en el carrito");
        }
        return cart;
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