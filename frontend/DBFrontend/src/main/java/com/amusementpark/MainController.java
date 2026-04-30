package com.amusementpark;

import com.amusementpark.AlertHelper;
import com.amusementpark.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // ── Sidebar nav buttons ──────────────────────────────────────────────
    @FXML private Button navDashboard;
    @FXML private Button navStaff;
    @FXML private Button navRides;
    @FXML private Button navBowling;
    @FXML private Button navCinema;
    @FXML private Button navCRM;
    @FXML private Button navVendors;

    // ── Sidebar footer ───────────────────────────────────────────────────
    @FXML private Label adminNameLabel;
    @FXML private Label adminEmailLabel;

    // ── Dynamic content area ─────────────────────────────────────────────
    @FXML private StackPane contentArea;

    private List<Button> navButtons;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navButtons = List.of(navDashboard, navStaff, navRides, navBowling, navCinema, navCRM, navVendors);

        // Populate sidebar admin info from session
        var admin = SessionManager.getInstance().getCurrentAdmin();
        if (admin != null) {
            adminNameLabel.setText(admin.getName());
            adminEmailLabel.setText(admin.getEmail());
        }

        // Start on Dashboard
        showDashboard();
    }

    // ── Nav handlers ─────────────────────────────────────────────────────

    @FXML private void showDashboard() { loadView("/fxml/Dashboard.fxml", navDashboard); }
    @FXML private void showStaff()     { loadView("/fxml/StaffView.fxml", navStaff);    }

    // Placeholder views for future drops — shows a "Coming Soon" label
    @FXML private void showRides()    { loadPlaceholder("🎢  Rides",       navRides);   }
    @FXML private void showBowling()  { loadPlaceholder("🎳  Bowling",     navBowling); }
    @FXML private void showCinema()   { loadPlaceholder("🎬  Cinema",      navCinema);  }
    @FXML private void showCRM()      { loadPlaceholder("💳  CRM",         navCRM);     }
    @FXML private void showVendors()  { loadPlaceholder("🍔  Vendor Hub",  navVendors); }

    @FXML
    private void handleLogout() {
        boolean confirmed = AlertHelper.showConfirm(
            "Sign Out",
            "Are you sure you want to sign out of the admin console?"
        );
        if (!confirmed) return;

        SessionManager.getInstance().logout();

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Login.fxml")
            );
            Scene scene = new Scene(loader.load(), 1280, 800);
            scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
            );
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Apex Park — Admin Console");
        } catch (IOException ex) {
            AlertHelper.showError("Logout Error", "Failed to return to login screen: " + ex.getMessage());
        }
    }

    // ── View loading ─────────────────────────────────────────────────────

    /**
     * Loads an FXML into the content area and marks the triggering
     * nav button as active. All other nav buttons revert to default.
     */
    private void loadView(String fxmlPath, Button activeBtn) {
        try {
            URL url = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(url);
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
            setActiveNav(activeBtn);
        } catch (IOException ex) {
            AlertHelper.showError("Navigation Error", "Could not load view: " + fxmlPath + "\n" + ex.getMessage());
        }
    }

    /**
     * For modules not yet implemented — shows a styled placeholder
     * so navigation doesn't break the shell.
     */
    private void loadPlaceholder(String title, Button activeBtn) {
        Label lbl = new Label(title + "\n\nThis module is coming in a future release.");
        lbl.setStyle(
            "-fx-font-size: 18px; -fx-text-fill: -ap-text-muted; " +
            "-fx-text-alignment: CENTER; -fx-alignment: CENTER;"
        );
        lbl.setWrapText(true);
        contentArea.getChildren().setAll(lbl);
        setActiveNav(activeBtn);
    }

    private void setActiveNav(Button active) {
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        if (!active.getStyleClass().contains("nav-btn-active")) {
            active.getStyleClass().add("nav-btn-active");
        }
    }
}
