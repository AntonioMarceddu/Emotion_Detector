package it.polito.emotiondetector;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class InformationController 
{
	/* JAVAFX stage. */
	private Stage stage;

	/* FXML buttons. */
	@FXML
	private Button CloseButton;

	/* FXML Textflow. */
	@FXML
	private TextFlow InformationTextFlow;

	public InformationController() 
	{
		stage = null;
	}

	/* Public method for setting the stage. */
	public void SetStage(Stage stage) 
	{
		this.stage = stage;
		WriteTextFlowContent();
	}

	/* Public method for window closing. */
	public void Close() 
	{
		stage.close();
	}

	/* Method for text writing. */
	private void WriteTextFlowContent() 
	{
		Text text1 = new Text("\nINTRODUCTION TO EMOTION DETECTOR\n\n");
		text1.setFill(Color.DEEPSKYBLUE);
		InformationTextFlow.getChildren().add(text1);

		Text text2 = new Text("Emotion Detector is a program that offers the possibility to perform facial expressions recognition directly from a camera, from an image or a file. It was developed by ");
		InformationTextFlow.getChildren().add(text2);
		
		Hyperlink hyperlink1 = new Hyperlink("Antonio Costantino Marceddu");
		hyperlink1.setBorder(Border.EMPTY);
		hyperlink1.setPadding(new Insets(0, 0, 0, 0));
		hyperlink1.setOnAction(h -> 
		{
			if (Desktop.isDesktopSupported()) 
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("https://www.linkedin.com/in/antonio-marceddu/"));
				} 
				catch (IOException | URISyntaxException e) 
				{
					Close();
				}
			}
		});
		InformationTextFlow.getChildren().add(hyperlink1);
		
		Text text3 = new Text(" (A.C.M.), in parallel with ");
		InformationTextFlow.getChildren().add(text3);		
		
		Hyperlink hyperlink2 = new Hyperlink("FEDC");
		hyperlink2.setBorder(Border.EMPTY);
		hyperlink2.setPadding(new Insets(0, 0, 0, 0));
		hyperlink2.setOnAction(h -> 
		{
			if (Desktop.isDesktopSupported()) 
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("https://github.com/AntonioMarceddu/Facial_Expressions_Databases_Classifier"));
				}
				catch (IOException | URISyntaxException e) 
				{
					Close();
				}
			}
		});
		InformationTextFlow.getChildren().add(hyperlink2);
		
		Text text4 = new Text(", using Java as the programming language, together with the ");
		InformationTextFlow.getChildren().add(text4);
		
		Hyperlink hyperlink3 = new Hyperlink("OpenCV");
		hyperlink3.setBorder(Border.EMPTY);
		hyperlink3.setPadding(new Insets(0, 0, 0, 0));
		hyperlink3.setOnAction(h -> 
		{
			if (Desktop.isDesktopSupported()) 
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("https://opencv.org/"));
				}
				catch (IOException | URISyntaxException e) 
				{
					Close();
				}
			}
		});
		InformationTextFlow.getChildren().add(hyperlink3);
		
		Text text5 = new Text(" and ");
		InformationTextFlow.getChildren().add(text5);
		
		Hyperlink hyperlink4 = new Hyperlink("DeepLearning4J");
		hyperlink4.setBorder(Border.EMPTY);
		hyperlink4.setPadding(new Insets(0, 0, 0, 0));
		hyperlink4.setOnAction(h -> 
		{
			if (Desktop.isDesktopSupported()) 
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("https://deeplearning4j.org/"));
				}
				catch (IOException | URISyntaxException e) 
				{
					Close();
				}
			}
		});
		InformationTextFlow.getChildren().add(hyperlink4);
		
		Text text6 = new Text(" libraries, the ");
		InformationTextFlow.getChildren().add(text6);
		
		Hyperlink hyperlink5 = new Hyperlink("JavaFX Maven Plugin");
		hyperlink5.setBorder(Border.EMPTY);
		hyperlink5.setPadding(new Insets(0, 0, 0, 0));
		hyperlink5.setOnAction(h -> 
		{
			if (Desktop.isDesktopSupported()) 
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("https://github.com/javafx-maven-plugin/javafx-maven-plugin"));
				}
				catch (IOException | URISyntaxException e) 
				{
					Close();
				}
			}
		});
		InformationTextFlow.getChildren().add(hyperlink5);
		
		Text text7 = new Text(" and the ");
		InformationTextFlow.getChildren().add(text7);
		
		Hyperlink hyperlink6 = new Hyperlink("Apache Maven");
		hyperlink6.setBorder(Border.EMPTY);
		hyperlink6.setPadding(new Insets(0, 0, 0, 0));
		hyperlink6.setOnAction(h -> 
		{
			if (Desktop.isDesktopSupported()) 
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("http://maven.apache.org/"));
				}
				catch (IOException | URISyntaxException e) 
				{
					Close();
				}
			}
		});
		InformationTextFlow.getChildren().add(hyperlink6);
		
		Text text8 = new Text(" tool.\r\n\n\n");
		InformationTextFlow.getChildren().add(text8);

		Text text9 = new Text("ADDITIONAL INFORMATION ABOUT THE PROGRAM\n\n");
		text9.setFill(Color.DEEPSKYBLUE);
		InformationTextFlow.getChildren().add(text9);

		Text text10 = new Text("As happened for FEDC, its development began in 2018 as part of a project for the Computer Vision course of the Politecnico di Torino. Initially, it was intended as a support tool for facial expressions recognition, which could be used to capture the user's face using a camera and test a neural network trained for that purpose. During the A.C.M. master's thesis, it was improved and corrected by adding a box containing the image that will actually be sent to the neural network, a function to acquire a screenshot of the program immediately after a prediction so as to document the results obtained and much more. Its improvement continued during a research work on the calibration of autonomous driving vehicles, in which the possibility of searching for facial expressions in the frames of a file and more was added.\r\n" 
			+ "During these two years, Emotion Detector has improved a lot and several functions have been added: so, the code was released under the MIT license and is available on ");
		InformationTextFlow.getChildren().add(text10);	

		Hyperlink hyperlink7 = new Hyperlink("GitHub");
		hyperlink7.setBorder(Border.EMPTY);
		hyperlink7.setPadding(new Insets(0, 0, 0, 0));
		hyperlink7.setOnAction(h -> 
		{
			if (Desktop.isDesktopSupported()) 
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("https://github.com/AntonioMarceddu/Emotion_Detector"));
				}
				catch (IOException | URISyntaxException e) 
				{
					Close();
				}
			}
		});
		InformationTextFlow.getChildren().add(hyperlink7);

		Text text11 = new Text(".\r\n\n\n");
		InformationTextFlow.getChildren().add(text11);

		Text text12 = new Text("UPDATES\n\n");
		text12.setFill(Color.DEEPSKYBLUE);
		InformationTextFlow.getChildren().add(text12);

		Text text13 = new Text("•	07/05/2020 - Version 1.0.0 released.\r\n");
		InformationTextFlow.getChildren().add(text13);		
	}
}
