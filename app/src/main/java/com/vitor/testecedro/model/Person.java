package com.vitor.testecedro.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "email",
        "name",
        "password"
})
@DatabaseTable(tableName = "person")
public class Person {

    @DatabaseField(id = true)
    private Integer id;
    @JsonProperty("email")
    @DatabaseField(columnName = "email")
    private String email;
    @JsonProperty("name")
    @DatabaseField(columnName = "name")
    private String name;
    @JsonProperty("password")
    @DatabaseField(columnName = "password")
    private String password;

    public Person() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // from Firebase databases
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

}