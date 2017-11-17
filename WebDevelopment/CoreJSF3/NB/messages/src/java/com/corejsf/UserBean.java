package com.corejsf;

import java.io.Serializable;

import javax.inject.Named; 
   // or import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped; 
   // or import javax.faces.bean.SessionScoped;

@Named("user") // or @ManagedBean(name="user")
@SessionScoped
public class UserBean implements Serializable {
   private String name;
   private int age;
   
   public String getName() { return name; } 
   public void setName(String newValue) { name = newValue; }
   
   public int getAge() { return age; }
   public void setAge(int newValue) { age = newValue; }
}