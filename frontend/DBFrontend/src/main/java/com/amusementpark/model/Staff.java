package com.amusementpark.model;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class Staff {
    private final IntegerProperty staffId       = new SimpleIntegerProperty();
    private final StringProperty  firstName     = new SimpleStringProperty();
    private final StringProperty  lastName      = new SimpleStringProperty();
    private final StringProperty  title         = new SimpleStringProperty();
    private final StringProperty  email         = new SimpleStringProperty();
    private final StringProperty  phoneNumber   = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> salary = new SimpleObjectProperty<>();
    // reportsTo == null means this staff member is a top-level manager
    private final ObjectProperty<Integer> reportsTo = new SimpleObjectProperty<>();

    public Staff() {}

    public Staff(int staffId, String firstName, String lastName,
                 String title, String email, String phoneNumber,
                 BigDecimal salary, Integer reportsTo) {
        setStaffId(staffId);
        setFirstName(firstName);
        setLastName(lastName);
        setTitle(title);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setSalary(salary);
        setReportsTo(reportsTo);
    }

    // Convenience: full name for TableView display
    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    // --- StaffID ---
    public int getStaffId()                          { return staffId.get(); }
    public void setStaffId(int v)                    { staffId.set(v); }
    public IntegerProperty staffIdProperty()         { return staffId; }

    // --- FirstName ---
    public String getFirstName()                     { return firstName.get(); }
    public void setFirstName(String v)               { firstName.set(v); }
    public StringProperty firstNameProperty()        { return firstName; }

    // --- LastName ---
    public String getLastName()                      { return lastName.get(); }
    public void setLastName(String v)                { lastName.set(v); }
    public StringProperty lastNameProperty()         { return lastName; }

    // --- Title ---
    public String getTitle()                         { return title.get(); }
    public void setTitle(String v)                   { title.set(v); }
    public StringProperty titleProperty()            { return title; }

    // --- Email ---
    public String getEmail()                         { return email.get(); }
    public void setEmail(String v)                   { email.set(v); }
    public StringProperty emailProperty()            { return email; }

    // --- PhoneNumber ---
    public String getPhoneNumber()                   { return phoneNumber.get(); }
    public void setPhoneNumber(String v)             { phoneNumber.set(v); }
    public StringProperty phoneNumberProperty()      { return phoneNumber; }

    // --- Salary ---
    public BigDecimal getSalary()                    { return salary.get(); }
    public void setSalary(BigDecimal v)              { salary.set(v); }
    public ObjectProperty<BigDecimal> salaryProperty() { return salary; }

    // --- ReportsTo (nullable FK) ---
    public Integer getReportsTo()                    { return reportsTo.get(); }
    public void setReportsTo(Integer v)              { reportsTo.set(v); }
    public ObjectProperty<Integer> reportsToProperty() { return reportsTo; }

    @Override
    public String toString() {
        return getFullName() + " (" + getTitle() + ")";
    }
}
