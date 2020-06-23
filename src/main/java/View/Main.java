package View;

import ModelPackage.System.database.DBManager;
import ModelPackage.System.database.HibernateUtil;
import View.Controllers.BackAbleController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    private static Stage window;
    private static Scene scene;
    private static double xOffset;
    private static double yOffset;

    public static void main(String[] args) {
        HibernateUtil.startUtil();
        DBManager.initialLoad();
        try {
            launch(args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        loadLogo();
        try {
            scene = new Scene(loadFXML("MainPage"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        moveSceneOnMouse(scene, stage);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        window.setOnCloseRequest(e-> close());
        stage.show();
    }

    private void loadLogo() {
        window.getIcons().add(new Image(Main.class.getClassLoader()
                .getResource("./Images/logo3.1.png").toString()));
    }

    public static void moveSceneOnMouse(Scene scene, Stage stage) {
        scene.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        scene.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }

    public static void minimize(){
        window.setIconified(true);
    }

    public static void close(){
        window.close();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        window.setScene(scene);
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("./fxmls/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Parent loadFXML(String fxml, String... backFxml) throws IOException {
        FXMLLoader loader = getFXMLLoader(fxml);
        Parent parent = loader.load();
        BackAbleController controller = loader.getController();
        controller.setBackFxmlS(Arrays.asList(backFxml));
        return parent;
    }

    public static Parent loadFXML(String fxml, List<String> bax) throws IOException {
        FXMLLoader loader = getFXMLLoader(fxml);
        Parent parent = loader.load();
        BackAbleController controller = loader.getController();
        controller.setBackFxmlS(bax);
        return parent;
    }

    public static FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(Main.class.getClassLoader().getResource("./fxmls/" + fxml + ".fxml"));
    }

    @Override
    public void stop() throws Exception {
        HibernateUtil.shutdown();
        super.stop();
    }

    public static FadeTransition makeFade(Node node, double from, double to, int duration) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
        fadeTransition.setToValue(to);
        fadeTransition.setFromValue(from);
        return fadeTransition;
    }
}
