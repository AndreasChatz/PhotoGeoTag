package com.epp1146.photogeotag;

public class PersonsInPhotoTable {

	float xMin, xMax, yMin, yMax;
	String imagePath, name;
	int _id;

	public PersonsInPhotoTable() {
	}

	public PersonsInPhotoTable(int id, String imagePaht, String name,
			float xMin, float xMax, float yMin, float yMax) {
		this._id = id;
		this.imagePath = imagePaht;
		this.name = name;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setXMin(float xMin) {
		this.xMin = xMin;
	}

	public float getXMin() {
		return this.xMin;
	}

	public void setXMax(float xMax) {
		this.xMax = xMax;
	}

	public float getXMax() {
		return this.xMax;
	}

	public void setYMin(float yMin) {
		this.yMin = yMin;
	}

	public float getYMin() {
		return this.yMin;
	}

	public void setYMax(float yMax) {
		this.yMax = yMax;
	}

	public float getYMax() {
		return this.yMax;
	}

	public void setId(int id) {
		this._id = id;
	}

	public int getId() {
		return this._id;
	}
}
