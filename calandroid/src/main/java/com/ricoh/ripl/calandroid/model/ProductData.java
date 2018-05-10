package com.ricoh.ripl.calandroid.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by vinaybn on 4/3/18.
 */

public class ProductData {
    public String name;
    public String category;
    public String subcategory;
    public String brand;
    public String upc;
    public String present;
    public ArrayList<RectData> facing;
    public Bitmap thumb = null;
    public String edittextData = null;
    public int edited = 0;
    public String editSelected = null;
}
