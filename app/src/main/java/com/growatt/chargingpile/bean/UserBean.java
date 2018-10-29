package com.growatt.chargingpile.bean;

import java.io.Serializable;

public class UserBean implements Serializable{
	public String enabled;
	public String agentCode;
	public String userLanguage;
	public String timeZone;
	public String password;
	public String id;
	public String mailNotice;
	public String phoneNum;
	public String lastLoginIp;
	public String accountName;
	public String approved;
	public String smsNotice;
	public String email;
	public int parentUserId;
	public String company;
	public String activeName;
	public String counrty;
	public String isBigCustomer;
	public String createDate;
	public String rightlevel;
	public String lastLoginTime;
	public String noticeType;
	private int isValiEmail;
	private int isValiPhone;

	public int getIsValiEmail() {
		return isValiEmail;
	}

	public void setIsValiEmail(int isValiEmail) {
		this.isValiEmail = isValiEmail;
	}

	public int getIsValiPhone() {
		return isValiPhone;
	}

	public void setIsValiPhone(int isValiPhone) {
		this.isValiPhone = isValiPhone;
	}

	public UserBean(String enabled, String agentCode, String userLanguage,
                    String timeZone, String password, String id, String mailNotice,
                    String phoneNum, String lastLoginIp, String accountName,
                    String approved, String smsNotice, String email,
                    int parentUserId, String company, String activeName,
                    String counrty, String isBigCustomer, String createDate,
                    String rightlevel, String lastLoginTime, String noticeType) {
		super();
		this.enabled = enabled;
		this.agentCode = agentCode;
		this.userLanguage = userLanguage;
		this.timeZone = timeZone;
		this.password = password;
		this.id = id;
		this.mailNotice = mailNotice;
		this.phoneNum = phoneNum;
		this.lastLoginIp = lastLoginIp;
		this.accountName = accountName;
		this.approved = approved;
		this.smsNotice = smsNotice;
		this.email = email;
		this.parentUserId = parentUserId;
		this.company = company;
		this.activeName = activeName;
		this.counrty = counrty;
		this.isBigCustomer = isBigCustomer;
		this.createDate = createDate;
		this.rightlevel = rightlevel;
		this.lastLoginTime = lastLoginTime;
		this.noticeType = noticeType;
	}
	public UserBean() {
		super();
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public String getAgentCode() {
		return agentCode;
	}
	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	public String getUserLanguage() {
		return userLanguage;
	}
	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMailNotice() {
		return mailNotice;
	}
	public void setMailNotice(String mailNotice) {
		this.mailNotice = mailNotice;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getApproved() {
		return approved;
	}
	public void setApproved(String approved) {
		this.approved = approved;
	}
	public String getSmsNotice() {
		return smsNotice;
	}
	public void setSmsNotice(String smsNotice) {
		this.smsNotice = smsNotice;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getParentUserId() {
		return parentUserId;
	}
	public void setParentUserId(int parentUserId) {
		this.parentUserId = parentUserId;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getActiveName() {
		return activeName;
	}
	public void setActiveName(String activeName) {
		this.activeName = activeName;
	}
	public String getCounrty() {
		return counrty;
	}
	public void setCounrty(String counrty) {
		this.counrty = counrty;
	}
	public String getIsBigCustomer() {
		return isBigCustomer;
	}
	public void setIsBigCustomer(String isBigCustomer) {
		this.isBigCustomer = isBigCustomer;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getRightlevel() {
		return rightlevel;
	}
	public void setRightlevel(String rightlevel) {
		this.rightlevel = rightlevel;
	}
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getNoticeType() {
		return noticeType;
	}
	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}
	
	
}
