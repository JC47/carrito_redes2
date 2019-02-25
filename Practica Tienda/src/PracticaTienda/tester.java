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
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static utils.Codes.*;

/**
 *
 * @author dany
 * Tester de ejemplo de la api del cliente
 */
public class tester {
    public static void main(String []args){
        try{
            /*
            Pedir la IP del servidor con la siguiente linea
            String ip=JOptionPane.showInputDialog("Ingrese la IP del servidor");
            */
            Socket cl=new Socket("127.0.0.1",SERVER_REQUEST_PORT);
            ObjectOutputStream oos=new ObjectOutputStream(cl.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(cl.getInputStream());
            Client cliente=new Client(oos,ois);
            cliente.getFiles();
            ArrayList<Item>stock=cliente.getStock();
            ArrayList<Item>cart=new ArrayList();
            for(Item item:stock){//Explora el catálogo y pide uno de cada uno
                cart=cliente.addToCart(item);//este méodo sólo agrega una unidad del producto
            }
            System.out.println("Carrito:");
            for(Item i:cart){
                System.out.println("Artículo: "+i.getName()+" Cantidad: "+i.getStock());
            }
            cliente.addToCart(stock.get(0));//Pide una unidad extra del primer artículo
            System.out.println("Pidiendo uno extra:");
            for(Item i:cart){
                System.out.println("Artículo: "+i.getName()+" Cantidad: "+i.getStock());
            }
            cart=cliente.removeFromCart(stock.get(1));//Quita del carrito una unidad del segundo artículo
            System.out.println("Eliminando uno:");
            for(Item i:cart){
                System.out.println("Artículo: "+i.getName()+" Cantidad: "+i.getStock());
            }
            File ticket=cliente.buy(cart);
            System.out.println("Se recibió el ticket: "+ticket.getName());
            
            oos.writeInt(REQUEST_CLOSE);//Cierra la conexión con el servidor
            oos.flush();
            oos.close();
            ois.close();
            cl.close();
        } catch (IOException ex) {
            Logger.getLogger(tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
