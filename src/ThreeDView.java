
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ThreeDView extends Application implements SimulationListener {

    private Simulation simulation;
    private Group root;

    private Map<CelestialBody, Sphere> bodySpheres = new HashMap<>();
    private Map<CelestialBody, Color> bodyColors = new HashMap<>();
    private Map<CelestialBody, Deque<Sphere>> trails = new HashMap<>();

    private final double scale = 1e-9;
    private final int maxTrailPoints = 400;

    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private double mouseX;
    private double mouseY;

    public ThreeDView(Simulation simulation) {
        this.simulation = simulation;
        simulation.addSimulationListener(this);
    }

    @Override
    public void start(Stage stage) {

        root = new Group();

        // Allow the user to rotate the entire 3D view
        root.getTransforms().addAll(rotateX, rotateY);

        Scene scene = new Scene(root, 800, 600, true);

        // Camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        scene.setCamera(camera);
        scene.setFill(Color.BLACK);

        // Mouse controls: click and drag to rotate
        scene.setOnMousePressed((MouseEvent e) -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        scene.setOnMouseDragged((MouseEvent e) -> {
            double deltaX = e.getSceneX() - mouseX;
            double deltaY = e.getSceneY() - mouseY;

            rotateY.setAngle(rotateY.getAngle() + deltaX * 0.5);
            rotateX.setAngle(rotateX.getAngle() - deltaY * 0.5);

            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        // Scroll to zoom
        scene.setOnScroll(e -> {
            double zoom = camera.getTranslateZ() + e.getDeltaY() * 0.5;
            camera.setTranslateZ(Math.max(-2000, Math.min(-120, zoom)));
        });

        updateBodies(true);

        stage.setTitle("3D N-Body Simulation");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void onSimulationStep() {
        Platform.runLater(() -> updateBodies(true));
    }

    private void updateBodies(boolean addTrailPoint) {

        Set<CelestialBody> currentBodies =
                new HashSet<>(simulation.getBodies());

        // Remove bodies that are no longer in the simulation
        bodySpheres.keySet().removeIf(body -> {
            if (!currentBodies.contains(body)) {
                root.getChildren().remove(bodySpheres.get(body));
                bodyColors.remove(body);
                return true;
            }
            return false;
        });

        // Remove their orbital trails too
        trails.keySet().removeIf(body -> {
            if (!currentBodies.contains(body)) {
                root.getChildren().removeAll(trails.get(body));
                return true;
            }
            return false;
        });

        int planetIndex = 0;

        for (CelestialBody body : simulation.getBodies()) {

            Sphere sphere = bodySpheres.get(body);

            if (sphere == null) {

                sphere = new Sphere();

                PhongMaterial material = new PhongMaterial();
                Color color;

                if (body instanceof Star) {
                    sphere.setRadius(22);
                    color = Color.rgb(255, 190, 55);
                } else if (body instanceof Planet) {

                    sphere.setRadius(10);

                    // First planet: Earth-like blue
                    // Second planet: Mars-like red
                    if (planetIndex == 0) {
                        color = Color.rgb(45, 110, 210);
                    } else if (planetIndex == 1) {
                        color = Color.rgb(190, 75, 45);
                    } else {
                        color = Color.LIGHTGRAY;
                    }

                    planetIndex++;

                } else {
                    sphere.setRadius(10);
                    color = Color.WHITE;
                }

                material.setDiffuseColor(color);
                material.setSpecularColor(Color.WHITE);

                sphere.setMaterial(material);

                bodySpheres.put(body, sphere);
                bodyColors.put(body, color);
                trails.put(body, new ArrayDeque<>());

                root.getChildren().add(sphere);
            } else if (body instanceof Planet) {
                planetIndex++;
            }

            // Convert physics coordinates to visualization coordinates
            Vector3D position = body.getPosition();

            double x = position.getX() * scale;
            double y = position.getY() * scale;
            double z = position.getZ() * scale;

            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);

            // Add a point to the orbital trail
            if (addTrailPoint) {
                addTrailPoint(body, x, y, z);
            }
        }
    }

    private void addTrailPoint(
            CelestialBody body, double x, double y, double z) {

        Deque<Sphere> bodyTrail = trails.get(body);

        Sphere point = new Sphere(0.8, 6);

        Color bodyColor = bodyColors.get(body);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(new Color(
                bodyColor.getRed(),
                bodyColor.getGreen(),
                bodyColor.getBlue(),
                0.55
        ));

        point.setMaterial(material);

        point.setTranslateX(x);
        point.setTranslateY(y);
        point.setTranslateZ(z);

        root.getChildren().add(point);
        bodyTrail.addLast(point);

        // Keep the trail from growing indefinitely
        if (bodyTrail.size() > maxTrailPoints) {
            Sphere oldestPoint = bodyTrail.removeFirst();
            root.getChildren().remove(oldestPoint);
        }
    }
}
