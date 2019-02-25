/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PracticaTienda;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author dany
 */
public class ItemList implements Serializable{
    ArrayList <Item> items;
    
    public ItemList(){
        
    }
    public ItemList(ArrayList <Item> items){
        this.items=items;
    }
    
    public ArrayList<Item> getList(){
        return items;
    }
    
    public void setList(ArrayList<Item> items){
        this.items=items;
    }
}
