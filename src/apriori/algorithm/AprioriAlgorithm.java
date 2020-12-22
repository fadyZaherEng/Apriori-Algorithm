/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori.algorithm;

import com.sun.javafx.scene.text.TextLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class AprioriAlgorithm {
   public static Vector<String> Transaction=new Vector<>();
   public static Vector<Map<String,Integer>> Tables=new Vector<>();
   public static Map<String,Integer> uniq_table=new HashMap<>();
   public static Map<String,Integer>finalRules=new HashMap<>();
   public static int min_Support=3,iterator=0,k,u, numSamilar;
   public static float min_conf=0.7f;
   public static ArrayList<String> Rules=new ArrayList<>();
   public static void main(String[] args) {
   
   String UniqueItems="";
   String temp=""; 
   //input
   Transaction.add("A,B,C,D,E,F");
   Transaction.add("A,C,D,E");
   Transaction.add("C,D,E");
   Transaction.add("B,F");
   Transaction.add("A,B,C,D");
   Transaction.add("A,B,E");
   Transaction.add("A,B,C");
   Transaction.add("A,B,E");
   Transaction.add("A,C");
   Transaction.add("A,C,D,E");
   
   //uniqe Item
   for (String trans : Transaction) {
      char[]arr=trans.toCharArray();
        for (int i = 0; i < arr.length; i++) {
           temp=String.valueOf(arr[i]);
           if(!",".equals(temp) && !UniqueItems.contains(temp)) UniqueItems+=temp;
           }
       }
   
   //create frequent 1 itemset 
   int count=0;
   char []arr=UniqueItems.toCharArray();
   for(int i=0;i<arr.length;i++){
     for (String trans : Transaction) if(trans.contains(String.valueOf(arr[i]))) count++; 
     if(count>=3)  uniq_table.put(String.valueOf(arr[i]),count);
     count=0;
   }
   Tables.add(uniq_table);          
   
   //create frequent 2 itemset 
   k=0;u=1;
   Map<String,Integer>a =new HashMap<>();
   for(Map.Entry<String, Integer> trans:Tables.get(iterator).entrySet())
   {
      for(Map.Entry<String, Integer> t:Tables.get(iterator).entrySet())
      {
        if(k>=u)  a.put(trans.getKey()+","+t.getKey(),0);
        k++;
      } 
      u++;
      k=0;
   }
   temp=""; count=0;
   Map<String,Integer>b =new HashMap<>();
   
   //support to 2 itemset
   for(Map.Entry<String, Integer> itemset:a.entrySet())
    {
        for (String trans : Transaction) {
              String []arr1=trans.split(",");
              for(String item:arr1) temp+=item;
              String []arr2=itemset.getKey().split(",");
              boolean x=true;
              for(int i=0;i<arr2.length;i++)  if(!temp.contains(arr2[i]))  x=false; 
              if(x) count++;   
              temp="";
        }
        if(count>=3) b.put(itemset.getKey(),count);
        count=0;
        temp="";
    }   
    Tables.add(b);
    iterator++;
    numSamilar=1;k=0;u=1;
   
    //create n frequent item set
    while(Tables.get(iterator).size()>1)
      {
        Map<String,Integer> tempTable=new ConcurrentHashMap<>();
        k=0;u=1;
        for(Map.Entry<String, Integer> itemset1:Tables.get(iterator).entrySet())
        { 
          String []tempItemset1=itemset1.getKey().split(",");
         for(Map.Entry<String, Integer> itemset2:Tables.get(iterator).entrySet())
            {
               String []tempItemset2=itemset2.getKey().split(",");
               if(k>=u)
               {
                 boolean x=true;
                 for(int i=0;i<numSamilar ;i++)  if(!tempItemset1[i].equals(tempItemset2[i]))   x=false;
                 String diffrent="";
                 if(x){
                 for(int i=numSamilar;i<tempItemset2.length;i++){
                     if(numSamilar==tempItemset1.length-1)
                       diffrent+=tempItemset2[i];
                     else
                         diffrent+=tempItemset2[i]+",";
                 }
                  tempTable.put(itemset1.getKey()+","+diffrent,0);
                 }
              }
               k++;
            }
           u++;k=0;
         }
        numSamilar++; 
        Tables.add(tempTable);
        iterator++;      
        //calculate Support and remove
        String s="";
        count=0;
        for(Map.Entry<String, Integer> it:Tables.get(iterator).entrySet())
        {
          for (String trans : Transaction)
          {
              String []arr1=trans.split(",");
              for(String item:arr1)  s+=item;
              String []arr2=it.getKey().split(",");
              boolean x=true;
              for(int i=0;i<arr2.length;i++)  if(!s.contains(arr2[i]))  x=false; 
              if(x) count++; 
              s="";
          }
          if(count<3)  Tables.get(iterator).remove(it.getKey());
          if(count>=3) Tables.get(iterator).replace(it.getKey(), count);
          count=0;s="";
        }     
      }
      int k=1;
      System.out.println("All Frequent Itemsets");
      for(Map<String,Integer> table:Tables){
          System.out.println("Frequent "+k+" -Itemsets");
          System.out.println(table);
          k++;
      }  
      //calculate Strong Rules 
       iterator=0;
       for(Map<String,Integer> table:Tables){
        if(iterator!=0){
          for(Map.Entry<String, Integer> it:table.entrySet())
          {
            String s1="",s2="";
            String[]temparr=it.getKey().split(",");   
            int support=it.getValue();
            Rules.clear();
            AllSubset(0,temparr.length, temparr, s1, s2);                              
            count=0;temp="";               
            for(String itemset:Rules)
            {
               String part1="";
               String []arr2=itemset.split(",");
               for(int i=0;i<arr2.length;i++){
                     if(arr2[i].startsWith("-"))break;
                     else part1+=arr2[i]+",";
               }
                 String []arr3=part1.split(",");  
                 for (String trans : Transaction) {
                 String []m=trans.split(",");
                 for(String item:m) temp+=item;     
                 boolean x=true;
                 for(int i=0;i<arr3.length;i++)  if(!temp.contains(arr3[i]))  x=false; 
                 if(x) count++;   
                 temp="";
               }                  
               float confidence=((float)support/count);
               if(confidence>=min_conf) {
               String partOneOutput="";
               String []arrpartOneOutput=itemset.split(",");
               for(int i=0;i<arrpartOneOutput.length;i++){
                     if(arrpartOneOutput[i].startsWith("-"))break;
                     else partOneOutput+=arrpartOneOutput[i]+",";
               }
               partOneOutput=partOneOutput.substring(0,partOneOutput.length()-1);
           
               String partTwoOutput="";
               String []arrpartTwoOutput=itemset.split(",");
               boolean flag=false;
               for(int i=0;i<arrpartTwoOutput.length;i++){
                     if(arrpartTwoOutput[i].startsWith("-")){
                         partTwoOutput+=arrpartTwoOutput[i].substring(1,arrpartTwoOutput[i].length())+",";
                         flag=true;
                         continue;
                     }
                     if(flag) partTwoOutput+=arrpartTwoOutput[i]+",";
               }
               partTwoOutput=partTwoOutput.substring(0,partTwoOutput.length()-1);  
               
               System.out.println(partOneOutput+"-->>"+partTwoOutput+" is Strong Rule with Suppot "+support+" And Conf is "+(confidence*100)+"%");
               }
               count=0;
               temp="";
            }            
           }
         }
        iterator++;
       }       
   }
   public static void AllSubset(int depth,int sz,String[] arr,String a,String b) {
        if(depth==sz){
            if(a.length()>0&&b.length()>0)
                Rules.add(a+'-'+b);
            return;
        }
            AllSubset(depth+1, sz, arr,a+arr[depth]+',', b);
            AllSubset(depth+1, sz, arr, a,b+arr[depth]+',');        
    }
}
