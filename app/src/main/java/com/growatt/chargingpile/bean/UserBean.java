package com.growatt.chargingpile.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    private String enabled;
    private String agentCode;
    private String userLanguage;
    private String timeZone;
    public String password;
    public String id;
    private String mailNotice;
    private String phoneNum;
    private String lastLoginIp;
    private String accountName;
    private String approved;
    private String smsNotice;
    public String email;
    private int parentUserId;
    private String company;
    private String activeName;
    private String counrty;
    private String isBigCustomer;
    private String createDate;
    private String rightlevel;
    private String lastLoginTime;
    private String noticeType;
    private int isValiEmail;
    private int isValiPhone;
    private String area;
    private String registTime;
    private String country;
    private String zipCode;//邮政
    private String  agent;
    private String code;
    private String auth;
    private String city;
    private String  roleId;
    private String type;
    private String userId;
    private String parentId;
    private String nick;
    private String areaId;
    private String phone;
    private String provice;
    private String name;
    private String roleName;
    private String time;
    private String addr;
    private String cid;
    private String status;
    private String installEmail;
    private String installPhone;
    private String installAddress;
    private String installDate;
    private String installer;


    //1是否是浏览账户,0是注册用户
    private int authnum;



    public int getAuthnum() {
        return authnum;
    }

    public void setAuthnum(int auth) {
        this.authnum = auth;
    }

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


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRegistTime() {
        return registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getInstallEmail() {
        return installEmail;
    }

    public void setInstallEmail(String installEmail) {
        this.installEmail = installEmail;
    }

    public String getInstallPhone() {
        return installPhone;
    }

    public void setInstallPhone(String installPhone) {
        this.installPhone = installPhone;
    }

    public String getInstallAddress() {
        return installAddress;
    }

    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }


    public String getInstaller() {
        return installer;
    }

    public void setInstaller(String installer) {
        this.installer = installer;
    }
}
