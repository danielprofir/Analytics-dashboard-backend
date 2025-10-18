package com.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a passenger from the Titanic dataset.
 * Contains demographic and ticket information for analytical queries.
 */
@Entity
@Table(name = "passengers")
public class Passenger {
    
    @Id
    @Column(name = "passenger_id")
    private Integer passengerId;
    
    @Column(name = "survived")
    private Integer survived;
    
    @Column(name = "pclass")
    private Integer pclass;
    
    @Column(name = "name", length = 255)
    private String name;
    
    @Column(name = "sex", length = 10)
    private String sex;
    
    @Column(name = "age")
    private Double age;
    
    @Column(name = "sibsp")
    private Integer sibsp;
    
    @Column(name = "parch")
    private Integer parch;
    
    @Column(name = "ticket", length = 50)
    private String ticket;
    
    @Column(name = "fare")
    private Double fare;
    
    @Column(name = "cabin", length = 50)
    private String cabin;
    
    @Column(name = "embarked", length = 1)
    private String embarked;
    
    // Constructors
    public Passenger() {}
    
    // Getters and Setters
    public Integer getPassengerId() {
        return passengerId;
    }
    
    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }
    
    public Integer getSurvived() {
        return survived;
    }
    
    public void setSurvived(Integer survived) {
        this.survived = survived;
    }
    
    public Integer getPclass() {
        return pclass;
    }
    
    public void setPclass(Integer pclass) {
        this.pclass = pclass;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSex() {
        return sex;
    }
    
    public void setSex(String sex) {
        this.sex = sex;
    }
    
    public Double getAge() {
        return age;
    }
    
    public void setAge(Double age) {
        this.age = age;
    }
    
    public Integer getSibsp() {
        return sibsp;
    }
    
    public void setSibsp(Integer sibsp) {
        this.sibsp = sibsp;
    }
    
    public Integer getParch() {
        return parch;
    }
    
    public void setParch(Integer parch) {
        this.parch = parch;
    }
    
    public String getTicket() {
        return ticket;
    }
    
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    
    public Double getFare() {
        return fare;
    }
    
    public void setFare(Double fare) {
        this.fare = fare;
    }
    
    public String getCabin() {
        return cabin;
    }
    
    public void setCabin(String cabin) {
        this.cabin = cabin;
    }
    
    public String getEmbarked() {
        return embarked;
    }
    
    public void setEmbarked(String embarked) {
        this.embarked = embarked;
    }
}