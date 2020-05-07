package it.polito.emotiondetector;

import java.io.IOException;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ModelLoader implements Runnable
{ 
	//true: load Keras Sequential model; false: load Keras Model (functional API).
	public static final boolean sequential = false;
	
	private final ProgressBar pb;
	private Stage stage;
	private Controller controller;
	
	@Override
	public void run() 
	{		
		Platform.runLater(() -> 
		{
			try 
			{
				// Load and set the pretrained neural network.
				if (sequential == true)
				{
					MultiLayerNetwork nnModel = KerasModelImport.importKerasSequentialModelAndWeights(".\\lib\\seqModel.h5");
					controller.setModel(nnModel);
				}
				else
				{
					ComputationGraph nnModel = KerasModelImport.importKerasModelAndWeights(".\\lib\\funcModel.h5");
					controller.setModel(nnModel);	
				}
				// Update the progress.
				pb.setProgress(100.0);
				// Close the stage.
				stage.close();	
			}
			catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) 
			{
				Platform.runLater(() -> 
				{			
					controller.showErrorDialog("An error occurred while loading the neural network model. Make sure that the seqModel.h5 file (if you used the Keras Sequential model) or the funcModel.h5 file (if you used the Keras Functional API model) are in the lib folder and restart the program.", true);
				});
			}
		});
	}	

	public ModelLoader(Controller controller)
	{
		this.controller = controller;	
		// Create a new stage.
		stage = new Stage();
		// Remove the title bar.
		stage.initStyle(StageStyle.UNDECORATED);
		
		//Create a new group and a new scene.
		Group root = new Group();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		
		// Create a new label.
		Label label = new Label();
		label.setText("Loading the neural network model...");
		
		// Create a new progressbar.	         
		pb = new ProgressBar(0);

		// Create a new vbox where to place the label and the progressbar.
		final VBox vb = new VBox();
		vb.setPrefSize(250, 50);
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().add(label);
		vb.getChildren().add(pb);
		scene.setRoot(vb);
		// Shows the stage.
		stage.show();   
	}
}