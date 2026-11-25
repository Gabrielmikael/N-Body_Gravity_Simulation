import javafx.animation.AnimationTimer; //lets us redraw every frame
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public class ThreeDView extends Application implements SimulationListener {
    private Simulation simulation;
    private Group root;

    private Map<CelestialBody, Sphere> bodySpheres = new HashMap<>(); // each body and its sphere
    private Map<CelestialBody, Deque<Sphere>> trails = new HashMap<>(); // each body and its trail (a line of small spheres)

    private double scale = 1e-9; //scale of simulation
    private int maxTrailPoints = 600; // object path trail
    private int planetCount = 0; // how many planets got a sphere so far, used to pick earth/mars textures

    // set to true by the simulation on every step, volatile because Swing and JavaFX run on different threads
    private volatile boolean stepHappened = false;

    // Camera rotation
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS); //creates rotation at inital 0 degrees around x-axis
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS); //creates rotation at inital 0 degrees around y-axis

    private double mouseX; // values for mouse position
    private double mouseY;

    public ThreeDView(Simulation simulation) {
        this.simulation = simulation;
        simulation.addSimulationListener(this); //registers this view as an observer of the simulation
    }

    @Override
    public void start(Stage stage) {
        root = new Group();

        // Lighting for the 3D objects
        AmbientLight ambientLight = new AmbientLight(Color.color(0.6, 0.6, 0.6));
        //ambient light is throughout entire project

        PointLight pointLight = new PointLight(Color.WHITE); //point light is for a single place, "Adds directional shading and depth"
        pointLight.setTranslateX(-200);
        pointLight.setTranslateY(-200);
        pointLight.setTranslateZ(-300);

        root.getChildren().addAll(ambientLight, pointLight);
        root.getTransforms().addAll(rotateX, rotateY); //add rotations and illumination to simulation

        // Camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500); //helps with scrolling
        camera.setNearClip(0.1);// sets minimum distance
        camera.setFarClip(20000); //sets max distance

        // Scene
        SubScene subScene = new SubScene(root, 800, 540, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK); // background now belongs to the SubScene
        subScene.setCamera(camera); // the camera belongs to the SubScene, not the Scene

        StackPane subSceneHolder = new StackPane(subScene); // wrapper so the SubScene can be resized with the window
        subSceneHolder.setStyle("-fx-background-color: black;");
        subScene.widthProperty().bind(subSceneHolder.widthProperty()); // SubScene grows/shrinks with the window
        subScene.heightProperty().bind(subSceneHolder.heightProperty());

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(layout, 800, 600);
        scene.setFill(Color.BLACK);

        layout.setCenter(subSceneHolder); // The holder goes in the center instead of root

        // Camera rotation with mouse
        subScene.setOnMousePressed((MouseEvent e) -> {
            mouseX = e.getSceneX(); // remember where the mouse was when the click started
            mouseY = e.getSceneY();
        });

        subScene.setOnMouseDragged((MouseEvent e) -> {
            double deltaX = e.getSceneX() - mouseX; // how far the mouse moved since the last event
            double deltaY = e.getSceneY() - mouseY;

            rotateY.setAngle(rotateY.getAngle() + deltaX * 0.5); // dragging sideways spins around the y-axis (0.5 = rotation speed)
            rotateX.setAngle(rotateX.getAngle() - deltaY * 0.5); // dragging up/down spins around the x-axis

            mouseX = e.getSceneX(); // save the new mouse position for the next drag event
            mouseY = e.getSceneY();
        });

        // Zoom with scroll wheel
        subScene.setOnScroll(e -> {
            double zoom = camera.getTranslateZ() + e.getDeltaY() * 0.5; // scrolling moves the camera closer or farther
            camera.setTranslateZ(
                    Math.max(-5000, Math.min(-80, zoom)) // keeps the camera between 80 and 5000 away so you can't zoom through everything
            );
        });

        // Trail length slider
        Label trailLabel = new Label("Trail length: " + maxTrailPoints); // text next to the slider

        Slider trailSlider = new Slider(100, 2000, maxTrailPoints); // min 100, max 2000, starts at maxTrailPoints
        trailSlider.setShowTickMarks(true); // little lines under the slider
        trailSlider.setShowTickLabels(true); // numbers under the slider
        trailSlider.setMajorTickUnit(500); // a number every 500
        trailSlider.setBlockIncrement(100); // arrow keys move it by 100
        trailSlider.setPrefWidth(250);

        //obs: The property being observed (your slider's value). oldValue: The slider's previous value. newValue: The slider's updated value.
        trailSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            maxTrailPoints = newValue.intValue(); // slider value becomes the new trail length
            trailLabel.setText("Trail length: " + maxTrailPoints); // update the text (trails get cut to this length in updateBodies)
        });

        //Hbox = horizontal box
        HBox controls = new HBox(15, trailLabel, trailSlider); // puts the label and slider in a row, 15 pixels apart

        controls.setStyle(
                "-fx-background-color: black;" +
                        "-fx-padding: 12;" +
                        "-fx-alignment: center;"
        );

        trailLabel.setTextFill(Color.WHITE); // white text so it shows on the black background
        layout.setBottom(controls); // controls go at the bottom of the window

        // redraws every frame (about 60 times a second) so new bodies show up right away, even when the simulation is paused
        new AnimationTimer() {
            @Override
            public void handle(long now) { // javafx calls this every frame
                boolean addTrail = stepHappened; // true only if the simulation stepped since the last frame
                stepHappened = false; // reset it until the next step
                updateBodies(addTrail); // move the spheres, and only add a trail point if the simulation moved
            }
        }.start();

        stage.setTitle("3D N-Body Simulation");
        stage.setScene(scene);
        stage.show();
    }

    // SimulationListener method, the simulation calls this every time something changes
    @Override
    public void onSimulationStep() {
        stepHappened = true; // just remember it happened, the AnimationTimer above does the drawing
    }

    // Loads a picture from the textures folder, returns null if the file isn't there
    private Image loadTexture(String filename) {
        File file = new File(filename); // path is relative to where the program is run from

        if (!file.exists()) {
            System.out.println("Could not find: " + filename);
            return null; // no texture, the sphere just stays plain
        }

        return new Image(file.toURI().toString()); // loads the picture
    }

    // Adds a small sphere where the body is right now, so it leaves a path behind it
    private void addTrailPoint(CelestialBody body, Sphere sphere) {
        Sphere point = new Sphere(0.8, 6); // small sphere (radius 0.8, only 6 divisions so it is cheap to draw)
        point.setMaterial(new PhongMaterial(Color.LIGHTGRAY));

        point.setTranslateX(sphere.getTranslateX()); // same spot as the body's sphere
        point.setTranslateY(sphere.getTranslateY());
        point.setTranslateZ(sphere.getTranslateZ());

        root.getChildren().add(point); // put it on the screen
        trails.get(body).addLast(point); // newest point goes at the end of this body's trail
    }

    // Removes the oldest points of every trail that is longer than maxTrailPoints
    private void trimTrails() {
        for (Deque<Sphere> bodyTrail : trails.values()) {
            while (bodyTrail.size() > maxTrailPoints) {
                root.getChildren().remove(bodyTrail.removeFirst()); // oldest point is at the front, take it off the screen
            }
        }
    }

    // Makes spheres for new bodies, moves all the spheres to their physics position, and adds trail points
    private void updateBodies(boolean addTrailPoint) {
        List<CelestialBody> bodies = simulation.getBodies(); // already a copy, so it is safe if the Swing side adds/removes at the same time

        // Remove spheres and trails of bodies that are no longer in the simulation
        bodySpheres.keySet().removeIf(body -> {
            if (bodies.contains(body)) { // still in the simulation, keep it
                return false;
            }
            root.getChildren().remove(bodySpheres.get(body)); // take the body's sphere off the screen
            root.getChildren().removeAll(trails.remove(body)); // take its trail off the screen (and forget the trail)
            return true; // true = remove it from bodySpheres too
        });

        for (CelestialBody body : bodies) {
            Sphere sphere = bodySpheres.get(body); // null if this body doesn't have a sphere yet

            if (sphere == null) { // new body, so make a sphere for it
                sphere = new Sphere();
                PhongMaterial material = new PhongMaterial(); // material = how the sphere looks (texture, color)

                if (body instanceof Star) {
                    sphere.setRadius(22); // stars are bigger
                    material.setDiffuseMap(loadTexture("textures/sun.jpg"));
                } else { // only other kind of body is a Planet
                    sphere.setRadius(10);

                    if (planetCount == 0) {
                        material.setDiffuseMap(loadTexture("textures/earth.jpg")); // first planet is earth
                    } else if (planetCount == 1) {
                        material.setDiffuseMap(loadTexture("textures/mars.jpg")); // second planet is mars
                    } else {
                        material.setDiffuseColor(Color.DODGERBLUE); // no texture for planets added later, so a plain color
                    }
                    planetCount++;
                }

                material.setSpecularColor(Color.LIGHTGRAY); // the shiny highlight
                sphere.setMaterial(material);

                bodySpheres.put(body, sphere); // remember which sphere belongs to this body
                trails.put(body, new ArrayDeque<>()); // start an empty trail for it
                root.getChildren().add(sphere); // put the sphere on the screen
            }

            // Move the sphere to the body's physics position
            Vector3D position = body.getPosition();
            sphere.setTranslateX(position.getX() * scale); // scale shrinks meters down to screen units
            sphere.setTranslateY(position.getY() * scale);
            sphere.setTranslateZ(position.getZ() * scale);

            if (addTrailPoint) {
                addTrailPoint(body, sphere);
            }
        }

        trimTrails(); // cut trails down to maxTrailPoints (also makes the slider work)
    }
}