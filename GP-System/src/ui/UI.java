package ui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.EventQueue;
import java.awt.Point;

import com.formdev.flatlaf.FlatLightLaf;

import engine.Engine;
import ui.structures.ViewPanel;
import ui.views.AssignDoctorView;
import ui.views.CreateVisitView;
import ui.views.HomeView;
import ui.views.ListBookingsView;
import ui.views.LoginView;
import ui.views.ListPatientsView;
import ui.views.RegisterView;
import ui.views.VisitDetailsEditView;
import ui.views.VisitDetailsView;

public class UI extends JFrame {
    public enum View {
        LOGIN, SIGNUP, HOME, LIST, PATIENTS, VISITDETAILS, VISITDETAILSEDIT, CREATEVISIT, ASSIGNDOCTOR
    }

    private View currentViewLocation = View.LOGIN;
    private ViewPanel currentView;

    public Engine engine;

    public UI() {
        super("Doctor Interface");

        System.out.println("[ui] intialized");

        engine = new Engine();
    }

    public void start() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("[ui] failed to set look and feel");
            System.err.println("[ui] Exception: " + e.getMessage());
        }

        this.setTitle("Doctor Interface");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(new Point(100, 100));

        EventQueue.invokeLater(() -> {
            this.pack();
            this.setSize(800, 400);
            this.loadView(this.currentViewLocation);
            this.setVisible(true);
        });
    }

    public void loadView(View view) {
        new Thread(() -> {
            System.out.println("[ui] loading view: " + view);

            if (currentViewLocation == view && currentView != null) {
                return;
            }

            ViewPanel newView = null;

            try {
                switch (view) {
                    case LOGIN:
                        newView = new LoginView(this, this.engine);
                        break;
                    case SIGNUP:
                        newView = new RegisterView(this, this.engine);
                        break;
                    case HOME:
                        newView = new HomeView(this, this.engine);
                        break;
                    case LIST:
                        newView = new ListBookingsView(this, this.engine);
                        break;
                    case PATIENTS:
                        newView = new ListPatientsView(this, this.engine);
                        break;
                    case VISITDETAILS:
                        newView = new VisitDetailsView(this, this.engine);
                        break;
                    case VISITDETAILSEDIT:
                        newView = new VisitDetailsEditView(this, this.engine);
                        break;
                    case CREATEVISIT:
                        newView = new CreateVisitView(this, this.engine);
                        break;
                    case ASSIGNDOCTOR:
                        newView = new AssignDoctorView(this, this.engine);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println("[ui] failed to load " + currentViewLocation + " view");
                System.err.println("[ui] Exception: " + e.getMessage());
            }

            if (currentView != null) {
                currentView.destroy();
                this.remove(currentView);
            }

            currentViewLocation = view;
            currentView = newView;

            if (newView != null) {
                newView.render();
                this.add(newView);
                newView.revalidate();
                newView.repaint();
            }
        }).start();
    }
}
