package com.growatt.chargingpile.bean;

import java.io.Serializable;

public class RegisterMap implements Serializable{
	private String regUserName;
	private String regPassword;
	private String regEmail;
	private String regDataLoggerNo;
	private String regValidateCode;
	private String regPhoneNumber;
	private String regTimeZone;
	private String regLanguage;
	private String regCountry;
	private String regCity;
	private String agentCode;
	private String regLng;
	private String regLat;
	public RegisterMap(String regUserName, String regPassword, String regEmail,
                       String regDataLoggerNo, String regValidateCode,
                       String regPhoneNumber, String regTimeZone, String regLanguage,
                       String regCountry, String regCity, String agentCode, String regLng,
                       String regLat) {
		super();
		this.regUserName = regUserName;
		this.regPassword = regPassword;
		this.regEmail = regEmail;
		this.regDataLoggerNo = regDataLoggerNo;
		this.regValidateCode = regValidateCode;
		this.regPhoneNumber = regPhoneNumber;
		this.regTimeZone = regTimeZone;
		this.regLanguage = regLanguage;
		this.regCountry = regCountry;
		this.regCity = regCity;
		this.agentCode = agentCode;
		this.regLng = regLng;
		this.regLat = regLat;
	}
	public RegisterMap() {
		super();
	}
	public String getRegUserName() {
		return regUserName;
	}
	public void setRegUserName(String regUserName) {
		this.regUserName = regUserName;
	}
	public String getRegPassword() {
		return regPassword;
	}
	public void setRegPassword(String regPassword) {
		this.regPassword = regPassword;
	}
	public String getRegEmail() {
		return regEmail;
	}
	public void setRegEmail(String regEmail) {
		this.regEmail = regEmail;
	}
	public String getRegDataLoggerNo() {
		return regDataLoggerNo;
	}
	public void setRegDataLoggerNo(String regDataLoggerNo) {
		this.regDataLoggerNo = regDataLoggerNo;
	}
	public String getRegValidateCode() {
		return regValidateCode;
	}
	public void setRegValidateCode(String regValidateCode) {
		this.regValidateCode = regValidateCode;
	}
	public String getRegPhoneNumber() {
		return regPhoneNumber;
	}
	public void setRegPhoneNumber(String regPhoneNumber) {
		this.regPhoneNumber = regPhoneNumber;
	}
	public String getRegTimeZone() {
		return regTimeZone;
	}
	public void setRegTimeZone(String regTimeZone) {
		this.regTimeZone = regTimeZone;
	}
	public String getRegLanguage() {
		return regLanguage;
	}
	public void setRegLanguage(String regLanguage) {
		this.regLanguage = regLanguage;
	}
	public String getRegCountry() {
		return regCountry;
	}
	public void setRegCountry(String regCountry) {
		this.regCountry = regCountry;
	}
	public String getRegCity() {
		return regCity;
	}
	public void setRegCity(String regCity) {
		this.regCity = regCity;
	}
	public String getAgentCode() {
		return agentCode;
	}
	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	public String getRegLng() {
		return regLng;
	}
	public void setRegLng(String regLng) {
		this.regLng = regLng;
	}
	public String getRegLat() {
		return regLat;
	}
	public void setRegLat(String regLat) {
		this.regLat = regLat;
	}
	@Override
	public String toString() {
		return "RegisterMap [regUserName=" + regUserName + ", regPassword="
				+ regPassword + ", regEmail=" + regEmail + ", regDataLoggerNo="
				+ regDataLoggerNo + ", regValidateCode=" + regValidateCode
				+ ", regPhoneNumber=" + regPhoneNumber + ", regTimeZone="
				+ regTimeZone + ", regLanguage=" + regLanguage
				+ ", regCountry=" + regCountry + ", regCity=" + regCity
				+ ", agentCode=" + agentCode + ", regLng=" + regLng
				+ ", regLat=" + regLat + "]";
	}
	
	
}
