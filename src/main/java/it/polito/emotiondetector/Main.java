package it.polito.emotiondetector;

import org.apache.log4j.BasicConfigurator;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application
{

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			// Load of the FXML layout.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
			BorderPane rootElement = (BorderPane) loader.load();
			// Creation of the scene.
			Scene scene = new Scene(rootElement, 1366, 768);
			// Creation of the stage.
			primaryStage.setTitle("Emotion Detector");
			primaryStage.setScene(scene);
			setIcon(primaryStage);
			// Show the GUI.
			primaryStage.show();
			
			// Log4j settings.
			BasicConfigurator.configure();
			
			// Creation and initialization of the Controller.
			Controller controller = loader.getController();
			controller.initializeController(primaryStage);
			
			// Management of the closing event.
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() 
			{
				public void handle(WindowEvent we)
				{
					controller.stopCamera(true);
					controller.terminateVideoAnalyzer();
				}
			}));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		// Load the OpenCV Library.
		nu.pattern.OpenCV.loadShared();
		//System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
	
	// Method for setting the application icon.
	private void setIcon(Stage primaryStage)
	{
		try
		{
			Image icon = new Image(getClass().getResource("icon.png").toString());
			primaryStage.getIcons().add(icon);
		}
		// N.B. it is not important to handle this exception.
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
