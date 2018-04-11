package com.example.parkingfast;

import android.R.string;

public class Poi 
{
	 private String Name;         //店家名稱
     private double Latitude;    //店家緯度
     private double Longitude;   //店家經度
     private double Distance;    //店家距離
     private String Address;	//店家地址
     private int Pay;		//店家計費方式
     

     //建立物件時需帶入景點店家名稱、景點店家緯度、景點店家經度
     public Poi(String name , double latitude , double longitude , String address , int pay)
     {
       //將資訊帶入類別屬性
        Name = name ;
        Latitude = latitude ;
        Longitude = longitude ;
        Address = address ;
        Pay = pay;
     }
     
    //取得店家名稱
     public String getName() 
     {
        return Name;
     }

    //取得店家緯度
     public double getLatitude()
     {
        return Latitude;
     }

    //取得店家經度
     public double getLongitude()
     {
        return Longitude;
     }
     
     //取得店家地址
      public String getAddress() 
      {
         return Address;
      }
      
      //取得店家計費方式
      public int getPay() 
      {
         return Pay;
      }
      
    //寫入店家距離
     public void setDistance(double distance)
     {
        Distance = distance;
     }
     
    //取的店家距離
     public double getDistance()
     {
        return Distance;
     }
	
}
