package com.amusementpark.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Admin {
    private final IntegerProperty loginId       = new SimpleIntegerProperty();
    private final StringProperty  name          = new SimpleStringProperty();
    private final StringProperty  email         = new SimpleStringProperty();
    private final StringProperty  password      = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    public Admin() {}

    public Admin(int loginId, String name, String email, String password, LocalDateTime createdAt) {
        setLoginId(loginId);
        setName(name);
        setEmail(email);
        setPassword(password);
        setCreatedAt(createdAt);
    }

    // --- LoginID ---
    public int getLoginId()                        { return loginId.get(); }
    public void setLoginId(int v)                  { loginId.set(v); }
    public IntegerProperty loginIdProperty()       { return loginId; }

    // --- Name ---
    public String getName()                        { return name.get(); }
    public void setName(String v)                  { name.set(v); }
    public StringProperty nameProperty()           { return name; }

    // --- Email ---
    public String getEmail()                       { return email.get(); }
    public void setEmail(String v)                 { email.set(v); }
    public StringProperty emailProperty()          { return email; }

    // --- Password (kept for auth; never display in UI) ---
    public String getPassword()                    { return password.get(); }
    public void setPassword(String v)              { password.set(v); }
    public StringProperty passwordProperty()       { return password; }

    // --- CreatedAt ---
    public LocalDateTime getCreatedAt()            { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime v)      { createdAt.set(v); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
}
