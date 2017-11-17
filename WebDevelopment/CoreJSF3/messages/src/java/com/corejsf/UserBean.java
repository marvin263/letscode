package com.corejsf;

import java.io.Serializable;

import javax.faces.bean.ManagedBean; 
   // or import javax.inject.Named;
import javax.faces.bean.SessionScoped; 
   // or import javax.enterprise.context.SessionScoped;

@ManagedBean(name="user") // or @Named("user")
@SessionScoped
public class UserBean implements Serializable {
   private String name;
   private int age;
   
   public String getName() { return name; } 
   public void setName(String newValue) { name = newValue; }
   
   public int getAge() { return age; }
   public void setAge(int newValue) { age = newValue; }
}