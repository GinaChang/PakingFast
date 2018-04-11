package com.example.parkingfast;

import android.R.string;

public class Poi 
{
	 private String Name;         //���a�W��
     private double Latitude;    //���a�n��
     private double Longitude;   //���a�g��
     private double Distance;    //���a�Z��
     private String Address;	//���a�a�}
     private int Pay;		//���a�p�O�覡
     

     //�إߪ���ɻݱa�J���I���a�W�١B���I���a�n�סB���I���a�g��
     public Poi(String name , double latitude , double longitude , String address , int pay)
     {
       //�N��T�a�J���O�ݩ�
        Name = name ;
        Latitude = latitude ;
        Longitude = longitude ;
        Address = address ;
        Pay = pay;
     }
     
    //���o���a�W��
     public String getName() 
     {
        return Name;
     }

    //���o���a�n��
     public double getLatitude()
     {
        return Latitude;
     }

    //���o���a�g��
     public double getLongitude()
     {
        return Longitude;
     }
     
     //���o���a�a�}
      public String getAddress() 
      {
         return Address;
      }
      
      //���o���a�p�O�覡
      public int getPay() 
      {
         return Pay;
      }
      
    //�g�J���a�Z��
     public void setDistance(double distance)
     {
        Distance = distance;
     }
     
    //�������a�Z��
     public double getDistance()
     {
        return Distance;
     }
	
}
