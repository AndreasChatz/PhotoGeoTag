package com.epp1146.photogeotag;

public class PersonInPhoto {

	float array[];
	float xMin, xMax, yMin, yMax, midX, midY, eyeDistance;
	static float ratio, yShift;
	String personsName;

	PersonInPhoto() {
	}

	boolean isPersonClicked(float x, float y) {
		boolean clicked = false;
		if (x > this.xMin & x < this.xMax & y > this.yMin & y < this.yMax) {
			clicked = true;
		}
		return clicked;
	}

	static void setRatioYshift(float ratio, float yShift) {
		PersonInPhoto.ratio = ratio;
		PersonInPhoto.yShift = yShift;
	}

	void setPersonsCoordinates() {
		this.xMin = (midX - eyeDistance) * ratio;
		this.xMax = (midX + eyeDistance) * ratio;
		this.yMin = (midY - eyeDistance) * ratio + yShift;
		this.yMax = (midY + eyeDistance) * ratio + yShift;
	}

	void setPersonsBasicCoordinates(float midX, float midY, float eyeDistance) {
		this.midX = midX;
		this.midY = midY;
		this.eyeDistance = eyeDistance;
	}

	float[] getPersonsCoordinates() {

		return array;
	}
	
	float getRealXMin(){
		float realXmin = this.midX - eyeDistance;
		return realXmin;		
	}
	float getRealYMax(){
		float realYmax = this.midY + eyeDistance;
		return realYmax;	
	}
	
	float getRealXMax(){
		float realXmin = this.midX + eyeDistance;
		return realXmin;
	}
	
	float getRealYMin(){
		float realYMin = this.midY - eyeDistance;
		return realYMin;
	}
	
	void setName(String name){
		this.personsName = name;
	}
	
	String getName(){
		return this.personsName;
	}
}
