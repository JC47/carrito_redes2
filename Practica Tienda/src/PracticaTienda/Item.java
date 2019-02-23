/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PracticaTienda;

import java.io.Serializable;

/**
 *
 * @author dany
 */
public class Item implements Serializable{
    
    private String pic;
    private String name;
    private int stock;
    private int price;

    public Item(String pic, String name, int stock, int price) {
        this.pic = pic;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }   
}
