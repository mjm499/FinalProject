package com.example.finalproject;

//Class used to represent a specific location.
//Private fields include high and low temperatures, weather conditions,
//zip code, city name, and an image url.
public class locationObject extends Object
{
	private int m_low;
	private int m_high;
	private String m_conditions;
	private String m_zipcode;
	private String m_cityname;
	private String m_imageUrl;
	
	//Constructor that will be used.
	public locationObject(int high, int low, String conditions, String zipcode, String imageUrl) 
	{
		m_low = low;
		m_high = high;
		m_conditions = conditions;
		m_zipcode = zipcode;
		m_cityname = "";
		m_imageUrl = imageUrl;
	}
	
	//Alternate constructor.
	public locationObject()
	{
		m_low = 0;
		m_high = 100;
		m_conditions = "undefined";
		m_zipcode = "undefined";
		m_cityname = "undefined";
	}
	
	//Getters and setters for all private fields.
	public int getHighTemp()
	{
		return m_high;
	}
	public int getLowTemp()
	{
		return m_low;
	}
	public String getConditions()
	{
		return m_conditions;
	}
	public String getZipcode()
	{
		return m_zipcode;
	}
	public String getCityName()
	{
		return m_cityname;
	}
	public void setHighTemp(int high)
	{
		m_high = high;
	}
	public void setLowTemp(int low)
	{
		m_low = low;
	}
	public void setConditions(String conditions)
	{
		m_conditions = conditions;
	}
	public void setZipcode(String zipcode)
	{
		m_zipcode = zipcode;
	}
	public void setCityName(String name)
	{
		m_cityname = name;
	}

	public String getImageUrl() {
		return m_imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.m_imageUrl = imageUrl;
	}
}
