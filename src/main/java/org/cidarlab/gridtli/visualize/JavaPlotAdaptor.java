/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.gridtli.visualize;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import org.cidarlab.gridtli.dom.Grid;
import org.cidarlab.gridtli.dom.Signal;
import org.cidarlab.gridtli.dom.SubGrid;
import org.cidarlab.gridtli.tli.TemporalLogicInference;

/**
 *
 * @author prash
 */
public class JavaPlotAdaptor {
 
    
    public static List<Point> getSignalJPlotPoints(Signal signal){
        List<Point> points = new ArrayList<Point>();
        for(org.cidarlab.gridtli.dom.Point point:signal.getPoints()){
            points.add(new Point(point.getX(),point.getY()));
        }
        return points;
    }
    
    public static List<Point> getSubGridJPlotPoints(Set<SubGrid> subgrids){
        List<Point> points = new ArrayList<Point>();
        for(SubGrid subgrid:subgrids){
            points.add(new Point(subgrid.getXOrigin(),subgrid.getYOrigin()));
        }
        return points;
    }
    
    public static NamedPlotColor getRandomColor(){
        Random rand = new Random();
        int i= rand.nextInt(NamedPlotColor.values().length);
        return NamedPlotColor.values()[i];
    }
    
    public static int getClusterIndex(List<Set<Signal>> clusters, Signal s){
        for(int i=0;i<clusters.size();i++){
            if(clusters.get(i).contains(s)){
                return i;
            }
        }
        return 0;
    }
    
    public static JavaPlot plotGrid(Grid grid, List<Set<Signal>> clusters){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        List<NamedPlotColor> clusterColors = new ArrayList<NamedPlotColor>();
        for (Set<Signal> cluster : clusters) {
            NamedPlotColor randColor = getRandomColor();
            while(clusterColors.contains(randColor)){
                randColor = randColor = getRandomColor();
            }
            clusterColors.add(randColor);
        }
        //clusterColors.add(NamedPlotColor.RED);
        //clusterColors.add(NamedPlotColor.BLUE);
        //clusterColors.add(NamedPlotColor.BLACK);
        
        for(Signal signal:grid.getSignals()){
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            //System.out.println("Signal Index :: " + signal.getIndex() + ", Color :: " + clusterColors.get(getClusterIndex(clusters,signal)).name());
            //sps.setLineType(clusterColors.get(getClusterIndex(clusters,signal)));
            sps.setLineType(NamedPlotColor.BLACK);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"yellow\"";
                
                plot.set(obj, rect);
                count++;
            }
        }
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    
    public static JavaPlot plotSpecificCluster(Grid grid, Set<Signal> cluster){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        Set<SubGrid> coveredCluster = TemporalLogicInference.getAllCoveredSubGrids(cluster);
        
        for(Signal signal:cluster){
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            //System.out.println("Signal Index :: " + signal.getIndex() + ", Color :: " + clusterColors.get(getClusterIndex(clusters,signal)).name());
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                if (coveredCluster.contains(subgrid)) {
                    String rect = "rect from " + subgrid.getXOrigin() + "," + subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin() + grid.getYIncrement()) + " fc rgb \"yellow\"";
                    plot.set(obj, rect);
                    count++;

                }
    
            }
        }
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    
    
    public static JavaPlot plotGrid(Grid grid){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        for(Signal signal:grid.getSignals()){
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.BLACK);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"#B3B3FA\"";
                
                plot.set(obj, rect);
                count++;
            }
        }
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    
    public static JavaPlot plotGrid_withTestingData(Grid grid, List<Signal> test){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        for(Signal signal:grid.getSignals()){
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.BLACK);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        for(Signal signal:test){
            PlotStyle sps_test = new PlotStyle();
            sps_test.setStyle(Style.LINES);
            sps_test.setLineType(NamedPlotColor.RED);
            PointDataSet psd_test = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp_test = new DataSetPlot(psd_test);
            dsp_test.setPlotStyle(sps_test);
            plot.addPlot(dsp_test);
        }
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"#B3B3FA\"";
                
                plot.set(obj, rect);
                count++;
            }
        }
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    public static JavaPlot plotGrid_withTestingData(Grid grid, List<Signal> satisfy, List<Signal> notSatisfy){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        for(Signal signal:satisfy){
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.BLACK);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        for(Signal signal:notSatisfy){
            PlotStyle sps_test = new PlotStyle();
            sps_test.setStyle(Style.LINES);
            sps_test.setLineType(NamedPlotColor.RED);
            sps_test.setLineWidth(3);
            PointDataSet psd_test = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp_test = new DataSetPlot(psd_test);
            dsp_test.setPlotStyle(sps_test);
            plot.addPlot(dsp_test);
        }
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"#B3B3FA\"";
                
                plot.set(obj, rect);
                count++;
            }
        }
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    
    public static JavaPlot plotGridData1_1(Grid grid){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        
        
        for(int i=0;i<10;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.ORANGE_RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        for(int i=10;i<11;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.DARK_VIOLET);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        for(int i=11;i<12;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.MIDNIGHT_BLUE);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(!grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"beige\"";
                
                plot.set(obj, rect);
                count++;
            }
        }
        
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    
    
    public static JavaPlot plotGridData1_1alt_1(Grid grid){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        
        
        
        
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(!grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"white\"";
                
                plot.set(obj, rect);
                count++;
            } else{
                String color = "";
                for(int i=0;i<10;i++){
                    if (grid.getSignals().get(i).coversSubGrid(subgrid)) {
                        color = "yellow";
                        String obj = "object " + count;
                        String rect = "rect from " + subgrid.getXOrigin() + "," + subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin() + grid.getYIncrement()) + " fc rgb \"" + color + "\"";

                        plot.set(obj, rect);
                        count++;
                    }
                }
                
                
            }
        }
        
        for(int i=0;i<10;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        for(int i=10;i<11;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        for(int i=11;i<12;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    
    public static JavaPlot plotGridData1_1alt(Grid grid){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        
        
        
        
        int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(!grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"white\"";
                
                plot.set(obj, rect);
                count++;
            } else{
                String color = "";
                for(int i=0;i<10;i++){
                    if(grid.getSignals().get(i).coversSubGrid(subgrid)){
                        color = "yellow";
                    }
                }
                for(int i=10;i<11;i++){
                    if(grid.getSignals().get(i).coversSubGrid(subgrid)){
                        color = "green";
                    }
                }
                for(int i=11;i<12;i++){
                    if(grid.getSignals().get(i).coversSubGrid(subgrid)){
                        color = "orange";
                    }
                }
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \""+color+"\"";
                
                plot.set(obj, rect);
                count++;
            }
        }
        
        for(int i=0;i<10;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        for(int i=10;i<11;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        for(int i=11;i<12;i++){
            Signal signal = grid.getSignals().get(i);
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    
    public static JavaPlot plotSignals(Grid grid){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        //plot.addPlot(dspgrid);
        
        for(Signal signal:grid.getSignals()){
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        /*int count =1;
        for(SubGrid subgrid: grid.getSubGrid().keySet()){
            if(!grid.isSpecificSubGridCovered(subgrid)){
                
                //System.out.println("Covered: " +  subgrid.getXOrigin()+","+subgrid.getYOrigin());
                String obj = "object " + count;
                String rect = "rect from " + subgrid.getXOrigin()+","+subgrid.getYOrigin() + " to " + (subgrid.getXOrigin() + grid.getXIncrement()) + "," + (subgrid.getYOrigin()+grid.getYIncrement()) + " fc rgb \"cyan\"";
                
                plot.set(obj, rect);
                count++;
            }
        }*/
        plot.set("style fill", "transparent solid 0.5");
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
        
    }
    
    public static JavaPlot plotGridwithoutCover(Grid grid){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        ps.setLineType(NamedPlotColor.BLACK);
        PointDataSet pdsgrid = new PointDataSet(getSubGridJPlotPoints(grid.getSubGrid().keySet()));
        DataSetPlot dspgrid = new DataSetPlot(pdsgrid);
        dspgrid.setPlotStyle(ps);
        plot.addPlot(dspgrid);
        
        for(Signal signal:grid.getSignals()){
            PlotStyle sps = new PlotStyle();
            sps.setStyle(Style.LINES);
            sps.setLineType(NamedPlotColor.RED);
            PointDataSet psd = new PointDataSet(getSignalJPlotPoints(signal));
            DataSetPlot dsp = new DataSetPlot(psd);
            dsp.setPlotStyle(sps);
            plot.addPlot(dsp);
        }
        
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("Grid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        return plot;
    }
    
    public static JavaPlot visualizeSubGrid(Set<SubGrid> subgrids){
        JavaPlot plot = new JavaPlot();
        PlotStyle ps = new PlotStyle();
        ps.setStyle(Style.DOTS);
        PointDataSet pds = new PointDataSet(getSubGridJPlotPoints(subgrids));
        DataSetPlot dsp = new DataSetPlot(pds);
        dsp.setPlotStyle(ps);
        plot.addPlot(dsp);
        
        plot.getAxis("x").setLabel("x");
        plot.getAxis("y").setLabel("y");
        plot.setTitle("SubGrid");
        plot.set("xzeroaxis", "");
        plot.set("yzeroaxis", "");
        plot.set("key", "off");
        
        return plot;
    }
    
    public static void plotToFile(JavaPlot plot, String filepath){
        
        ImageTerminal png = new ImageTerminal();
        File file = new File(filepath);
        
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            System.err.print("File " + filepath + " not found.\n");
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }
        
        plot.setPersist(false);
        plot.setTerminal(png);
        plot.plot();
        
        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            System.err.print(ex);
        }
    } 
   
    
}