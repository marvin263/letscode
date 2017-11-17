package com.corejsf;

import java.io.Serializable;
import javax.inject.Named;
   // or import javax.faces.bean.ManagedBean;
import javax.enterprise.context.RequestScoped;
// or import javax.faces.bean.RequestScoped;

@Named // or @ManagedBean
@RequestScoped
public class Planetarium implements Serializable {

    private String selectedPlanet;

    public String getSelectedPlanet() {
        return selectedPlanet;
    }

    public String changePlanet(String newValue) {
        selectedPlanet = newValue;
        return selectedPlanet;
    }
}
