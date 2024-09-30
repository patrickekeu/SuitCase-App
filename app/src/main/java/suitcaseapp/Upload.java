package com.example.suitcaseapp;

import android.net.Uri;

import com.google.firebase.Timestamp;


public class Upload {
    private String name;
    private String mImageUrl;

    private String title;
    private String price;
    private String desc;
    private Boolean purchased = false;
    private String image;
    private Timestamp timeAdded;

    public Upload()
    {
        // Empty constructor needed
    }
    public Upload(String name, String imageUrl)
    {
        if(name.trim().equals(""))
        {
            name = "No Name";
        }
        this.name = name;
        mImageUrl = imageUrl;
    }

    public String getName()
    {
        return name;
    }

    public boolean isPurchased()
    {
       return purchased;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public String getPrice()
    {
        return price;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImage()
    {
        return image;
    }

    public void setUri(String image)
    {
        this.image = image;
    }

    public void setImageUrl(String imageUrl)
    {
        mImageUrl = imageUrl;
    }
}
