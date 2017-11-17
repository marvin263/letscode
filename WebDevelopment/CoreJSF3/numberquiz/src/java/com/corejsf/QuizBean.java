package com.corejsf;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean; 
   // or import javax.inject.Named;
import javax.faces.bean.SessionScoped; 
   // or import javax.enterprise.context.SessionScoped;

@ManagedBean // or @Named
@SessionScoped
public class QuizBean implements Serializable {
   private ArrayList<ProblemBean> problems = new ArrayList<ProblemBean>();     
   private int currentIndex;
   private int score;
   
   public QuizBean() {      
      problems.add(
         new ProblemBean(new int[] { 3, 1, 4, 1, 5 }, 9)); // pi
      problems.add(
         new ProblemBean(new int[] { 1, 1, 2, 3, 5 }, 8)); // fibonacci
      problems.add(
         new ProblemBean(new int[] { 1, 4, 9, 16, 25 }, 36)); // squares
      problems.add(
         new ProblemBean(new int[] { 2, 3, 5, 7, 11 }, 13)); // primes
      problems.add(
         new ProblemBean(new int[] { 1, 2, 4, 8, 16 }, 32)); // powers of 2
   }   

   public void setProblems(ArrayList<ProblemBean> newValue) { 
      problems = newValue;
      currentIndex = 0;
      score = 0;
   } 
      
   public int getScore() { return score; }

   public ProblemBean getCurrent() { return problems.get(currentIndex); }

   public String getAnswer() { return ""; }
   public void setAnswer(String newValue) { 
      try {
         int answer = Integer.parseInt(newValue.trim());
         if (getCurrent().getSolution() == answer) score++;            
         currentIndex = (currentIndex + 1) % problems.size();
      }
      catch (NumberFormatException ex) {
      }
   }   
}
