/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.gridtli.dom;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author prash
 */
public class SubGrid {

    @Getter
    @Setter
    private double xOrigin;

    @Getter
    @Setter
    private double yOrigin;

    
    @Getter
    @Setter
    private double xInc;
    
    @Getter
    @Setter
    private double yInc;
    
    //@Getter
    //@Setter
    //private boolean covered;

    public SubGrid(double _xOrigin, double _yOrigin) {
        this.xOrigin = _xOrigin;
        this.yOrigin = _yOrigin;
    }
    
    public SubGrid(double _xOrigin, double _yOrigin, double _xInc, double _yInc){
        this.xOrigin = _xOrigin;
        this.yOrigin = _yOrigin;
        this.xInc = _xInc;
        this.yInc = _yInc;
    }

    public boolean smallerThan(SubGrid sgrid){
        
        if(this.xOrigin < sgrid.xOrigin){
            return true;
        }
        if(this.xOrigin == sgrid.xOrigin){
            if(this.yOrigin < sgrid.yOrigin){
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String toString(){
        String str = "";
        str += "(" + this.xOrigin + "," + this.yOrigin +  ")";
        //str += this.xOrigin + "," + this.yOrigin + ":" + this.covered;
        return str;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SubGrid)) {
            return false;
        }
        SubGrid clone = (SubGrid) o;
        if (this.xOrigin == clone.xOrigin) {
            if (this.yOrigin == clone.yOrigin) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.xOrigin) ^ (Double.doubleToLongBits(this.xOrigin) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.yOrigin) ^ (Double.doubleToLongBits(this.yOrigin) >>> 32));
        return hash;
    }

}