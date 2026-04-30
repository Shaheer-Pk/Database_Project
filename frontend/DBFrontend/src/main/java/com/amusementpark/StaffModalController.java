package com.amusementpark;

import com.amusementpark.model.Staff;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class StaffModalController implements Initializable {

    @FXML private Label      modalTitle;
    @FXML private Label      modalSubtitle;
    @FXML private TextField  firstNameField;
    @FXML private TextField  lastNameField;
    @FXML private TextField  titleField;
    @FXML private TextField  emailField;
    @FXML private TextField  phoneField;
    @FXML private TextField  salaryField;
    @FXML private ComboBox<Staff> reportsToCombo;
    @FXML private Label      validationLabel;
    @FXML private Button     saveButton;

    // Callbacks set by StaffController
    private Consumer<Staff> onSave;
    private Runnable        onCancel;

    // null = Add mode; non-null = Edit mode
    private Staff existingStaff;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureComboBox();
    }

    /**
     * Called by StaffController before the dialog is shown.
     *
     * @param existing    The staff to edit, or null for Add mode.
     * @param allStaff    Full staff list for the "Reports To" dropdown.
     */
    public void setMode(Staff existing, List<Staff> allStaff) {
        this.existingStaff = existing;

        // Populate manager ComboBox — exclude self in edit mode
        List<Staff> eligibleManagers = (existing == null)
            ? allStaff
            : allStaff.stream()
                .filter(s -> s.getStaffId() != existing.getStaffId())
                .toList();
        reportsToCombo.getItems().setAll(eligibleManagers);

        if (existing == null) {
            // ── Add mode ──────────────────────────────────────────────────
            modalTitle.setText("Add Staff Member");
            modalSubtitle.setText("Fill in all required fields (*)");
        } else {
            // ── Edit mode ─────────────────────────────────────────────────
            modalTitle.setText("Edit Staff Member");
            modalSubtitle.setText("Update the details for " + existing.getFullName());
            saveButton.setText("Save Changes");

            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            titleField.setText(existing.getTitle());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhoneNumber() != null ? existing.getPhoneNumber() : "");
            salaryField.setText(existing.getSalary() != null ? existing.getSalary().toPlainString() : "");

            // Pre-select the current manager in the ComboBox
            if (existing.getReportsTo() != null) {
                eligibleManagers.stream()
                    .filter(s -> s.getStaffId() == existing.getReportsTo())
                    .findFirst()
                    .ifPresent(reportsToCombo::setValue);
            }
        }
    }

    public void setOnSave(Consumer<Staff> callback)  { this.onSave   = callback; }
    public void setOnCancel(Runnable callback)        { this.onCancel = callback; }

    // ── Save handler ─────────────────────────────────────────────────────

    @FXML
    private void handleSave() {
        validationLabel.setText("");

        // ── Validation ────────────────────────────────────────────────────
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String title     = titleField.getText().trim();
        String email     = emailField.getText().trim();
        String phone     = phoneField.getText().trim();
        String salaryStr = salaryField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || title.isEmpty() || email.isEmpty()) {
            validationLabel.setText("First name, last name, title, and email are required.");
            return;
        }

        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$")) {
            validationLabel.setText("Please enter a valid email address.");
            return;
        }

        if (salaryStr.isEmpty()) {
            validationLabel.setText("Salary is required.");
            return;
        }

        BigDecimal salary;
        try {
            salary = new BigDecimal(salaryStr);
            if (salary.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            validationLabel.setText("Salary must be a valid positive number.");
            return;
        }

        // ── Build Staff object ────────────────────────────────────────────
        Staff result = (existingStaff != null) ? existingStaff : new Staff();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setTitle(title);
        result.setEmail(email);
        result.setPhoneNumber(phone.isEmpty() ? null : phone);
        result.setSalary(salary);

        Staff selectedManager = reportsToCombo.getValue();
        result.setReportsTo(selectedManager != null ? selectedManager.getStaffId() : null);

        // ── Notify StaffController ────────────────────────────────────────
        if (onSave != null) onSave.accept(result);
    }

    @FXML
    private void handleCancel() {
        if (onCancel != null) onCancel.run();
    }

    // ── ComboBox display configuration ───────────────────────────────────

    private void configureComboBox() {
        StringConverter<Staff> converter = new StringConverter<>() {
            @Override public String toString(Staff s)   { return s == null ? "" : s.getFullName() + " — " + s.getTitle(); }
            @Override public Staff fromString(String s) { return null; } // not needed
        };
        reportsToCombo.setConverter(converter);
        reportsToCombo.setPromptText("— None (Top-level) —");
    }
}
