/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.gridtli.DOM;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author prash
 */
public class Point {

    @Getter
    @Setter
    private double x;
    
    @Getter
    @Setter
    private double y;
    
    @Getter
    @Setter
    private String xSignal;
    
    @Getter
    @Setter
    private String ySignal;
    
    
    public Point(double _x, double _y){
        x = _x;
        y = _y;
        xSignal = "";
        ySignal = "";
    }
    
    public Point(double _x, String _xSignal, double _y, String _ySignal){
        x = _x;
        y = _y;
        xSignal = _xSignal;
        ySignal = _ySignal;
    }
    
    public Point(Point copy){
        this.x = copy.x;
        this.y = copy.y;
        this.xSignal = copy.xSignal;
        this.ySignal = copy.ySignal;
    }
    
    @Override
    public String toString(){
        String string = "";
        
        if(this.xSignal.equals("")){
            string += this.x + ";";
        }else{
            string += this.xSignal + "=" + this.x + ";";
        }
        
        if(this.ySignal.equals("")){
            string += this.y;
        }else{
            string += this.ySignal + "=" + this.y;
        }
        
        return string;
    }
}
