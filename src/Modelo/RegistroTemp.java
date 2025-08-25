/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author ilope
 */
public abstract class RegistroTemp {
    protected LocalDate inicio;
    protected LocalDate fin;
    
    public LocalDate getInicio(){ 
        return inicio; 
    }
    public void setInicio(LocalDate inicio){
        this.inicio = inicio; 
    }
    public LocalDate getFin(){ 
        return fin; 
    }
    public void setFin(LocalDate fin){ 
        this.fin = fin; 
    }
    public long getDias() {
        return (inicio != null && fin != null) ? ChronoUnit.DAYS.between(inicio, fin) : 0;
    }

    public RegistroTemp(LocalDate inicio, LocalDate fin) {
        this.inicio = inicio;
        this.fin = fin;
    }
    
}

