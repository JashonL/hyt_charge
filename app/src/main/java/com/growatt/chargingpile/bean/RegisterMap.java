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
	private String regPostCode;
	private String regInstaller;

	private String regInstallEmail;
	private String regInstallPhone;
	private String regInstallAddress;
	private String regInstallDate;
	private String reInstallChargeId;


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

	public String getRegPostCode() {
		return regPostCode;
	}

	public String getRegInstaller() {
		return regInstaller;
	}

	public void setRegInstaller(String regInstaller) {
		this.regInstaller = regInstaller;
	}

	public void setRegPostCode(String regPostCode) {
		this.regPostCode = regPostCode;
	}

	public String getRegInstallEmail() {
		return regInstallEmail;
	}

	public void setRegInstallEmail(String regInstallEmail) {
		this.regInstallEmail = regInstallEmail;
	}

	public String getRegInstallPhone() {
		return regInstallPhone;
	}

	public void setRegInstallPhone(String regInstallPhone) {
		this.regInstallPhone = regInstallPhone;
	}

	public String getRegInstallAddress() {
		return regInstallAddress;
	}

	public void setRegInstallAddress(String regInstallAddress) {
		this.regInstallAddress = regInstallAddress;
	}

	public String getRegInstallDate() {
		return regInstallDate;
	}

	public void setRegInstallDate(String regInstallDate) {
		this.regInstallDate = regInstallDate;
	}

	public String getReInstallChargeId() {
		return reInstallChargeId;
	}

	public void setReInstallChargeId(String reInstallChargeId) {
		this.reInstallChargeId = reInstallChargeId;
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
				+ ", regLat=" + regLat + "]"+",regPostCode="+regPostCode;
	}
	
	
}
