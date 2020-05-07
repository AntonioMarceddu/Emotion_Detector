package it.polito.emotiondetector;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** MODIFIED VERSION by Antonio Marceddu of 
 * https://github.com/opencv-java/face-detection
 * Original references of the author can be found below. */

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired file stream, the relative
 * controls and the face detection/tracking.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 1.1 (2015-11-10)
 * @since 1.0 (2014-01-10)
 * 		
 */

public class Controller 
{
	/* JAVAFX stage. */
	private Stage primaryStage = null;

	// FXML elements.
	@FXML
	private BarChart <String, Double> barChart;
	@FXML
	private BorderPane root;
	@FXML
	private Button cameraButton, openFileButton, detectEmotionButton;
	@FXML
	private CheckBox imagesCheckBox, csvCheckBox;
	@FXML
	private HBox periodHBox, progressBarHBox;
	@FXML
	private ImageView imageStreamImageView, capturedFaceImageView;
	@FXML
	private Label progressBarLabel, fileLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Slider inputSlider;
	@FXML
	private TextField periodTextField;

	// The thread that perform emotions detection on files.
	private Thread analyzeVideo;
	// A timer for acquiring the file stream.
	private ScheduledExecutorService timer;
	
	// The OpenCV object that performs the file capture.
	private VideoCapture cameraCapture = new VideoCapture();
	// Utils class.
	private Utils utils = new Utils();

	// Keras Sequential and Functional API model neural network.
	private MultiLayerNetwork seqModel; 
	private ComputationGraph funcModel; 
	// Mats containing the captured face, one will be displayed on the screen and the other will be used to make the prediction.
	private Mat faceToPredict = new Mat(), faceToDisplay = new Mat();
	// Series containing the emotions displayed in the graph.
	private XYChart.Series<String, Double> emotionSeries;
	// The noFace image.
	private Image noFaceImage;

	// Minimum face size for cascade classifier, an image counter and the image size as required by the neural network.
	private int imageCounter = 0, size = 48;
	// Flag to indicate if a human face was found.
	private boolean faceFound = false;
	// I/O Variables.
	private String outputDirectory, imageTimestamp, filePath = "";
	
	/* Public method for initializing the controller. */
	public void initializeController(Stage stage) 
	{
		// Set the stage.
		primaryStage = stage;

		// Initialize the graph.
		initGraph();
		
		// Try to load the Haar cascade classifier: if an error occurs, the program will be closed.
		if (!utils.initFaceCascade())
		{
			showErrorDialog("There was an error while loading the Haar cascade classifier.", true);
		}

		// Load the pre-trained neural network.
		ModelLoader modelLoader = new ModelLoader(this);
		Thread workerThread = new Thread(modelLoader);
		workerThread.start();

		// Initialize the input sliders.
		initInputSlider();
		
		// Limit the number of characters on the periodTextField.
		periodTextField.setTextFormatter(new TextFormatter<String>(change ->
		(change.getControlNewText().equals("")) || (change.getControlNewText().matches("^[0-9]{1,2}$")) || (change.getControlNewText().matches("^[0-9]{1,2}\\.")) || (change.getControlNewText().matches("^[0-9]{1,2}\\.[0-9]$")) ? change : null));

		// Disable the file options.
		enableOrDisableFileOptions(false);	
		
		// Define the timestamp for the .csv image file.
		imageTimestamp = Utils.getTimestamp();
		
		// Update the output directory.
		outputDirectory = System.getProperty("user.dir") + "\\" + "predictions";
		
		// Load the noFaceImage image.
		try
		{
			noFaceImage = new Image(getClass().getResource("facenotfound.png").toString());
		}
		catch (Exception e)
		{
			showErrorDialog("The image facenotfound.png was not found. Please download the program again.", true);
		}
	}

	/* Method to initialize the input sliders. */
	private void initInputSlider()
	{
		inputSlider.valueProperty().addListener(new ChangeListener<Number>() 
		{
			public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) 
			{
				boolean value = true;
				// File case.
				if (newVal.intValue() > 50)
				{
					value = false;
				}
				// Camera case.
				else
				{
					detectEmotionButton.setDisable(value);
				}
				// Enable or disable some items based on the input value.
				enableOrDisableCameraOptions(value);
				enableOrDisableFileOptions(!value);	
				// Remove the previously captured images.
				imageStreamImageView.setImage(null);
				capturedFaceImageView.setImage(null);		
				// Reset the graph values.
				resetGraphValues();
				// Reset the progress bar.
				updateProgressBar(0);		
			}
		});
	}

	/* Method to enable or disable the camera options. */
	private void enableOrDisableCameraOptions(boolean value)
	{
		cameraButton.setManaged(value);
		cameraButton.setVisible(value);
	}

	/* Method to enable or disable the file options. */
	private void enableOrDisableFileOptions(boolean value)
	{
		openFileButton.setManaged(value);
		openFileButton.setVisible(value);
		periodHBox.setManaged(value);
		periodHBox.setVisible(value);
		fileLabel.setManaged(value);
		fileLabel.setVisible(value);
	}

	/* Method to initialize the graph. */
	private void initGraph()
	{						
		// Initialize the series visualized in the graph.
		emotionSeries = new XYChart.Series<String, Double>();
		emotionSeries.setName("Emotions");

		emotionSeries.getData().add(new XYChart.Data<String, Double>("Anger", 0.0));
		emotionSeries.getData().add(new XYChart.Data<String, Double>("Contempt", 0.0));
		emotionSeries.getData().add(new XYChart.Data<String, Double>("Disgust", 0.0));
		emotionSeries.getData().add(new XYChart.Data<String, Double>("Fear", 0.0));
		emotionSeries.getData().add(new XYChart.Data<String, Double>("Happiness", 0.0));
		emotionSeries.getData().add(new XYChart.Data<String, Double>("Neutrality", 0.0));
		emotionSeries.getData().add(new XYChart.Data<String, Double>("Sadness", 0.0));
		emotionSeries.getData().add(new XYChart.Data<String, Double>("Surprise", 0.0));        

		// Hack-around for a bug with the auto-range of the CategoryAxis.
		((CategoryAxis) barChart.getXAxis()).setCategories(FXCollections.observableArrayList("Anger", "Contempt", "Disgust", "Fear", "Happiness", "Neutrality", "Sadness", "Surprise"));

		// Adds the series to the graph.
		barChart.getData().add(emotionSeries);
	}		

	/* Method to set the graph values and take a screenshot. */
	public void updateGraphValuesAndDocumentResults(double[] emotions, String FileName, int frame)
	{
		for (int i = 0; i < 8; i++)
		{
			emotionSeries.getData().get(i).setYValue(Math.floor(emotions[i] * 100) / 100);
		}	

		// Document the result taking a picture of the current screen or saving the result in a .csv file.
		if (imagesCheckBox.isSelected())
		{
			saveGUIScreenshot(FileName, frame); 
		}
		if (csvCheckBox.isSelected()) 
		{
			writePredictionsToCSVFile(emotions, FileName, frame);
		}			

		// Enable the camera button, the slider and the checkboxes if the relative option is active.
		if (frame == -1)
		{
			if (inputSlider.getValue() <= 50)
			{
				disableCameraButtons(false, true);
				inputSlider.setDisable(false);	
			}
			else
			{
				disableFileButtons(false, false);
			}
		}
	}
	
	/* Method to reset the graph values. */
	public void resetGraphValues()
	{
		for (int i = 0; i < 8; i++)
		{
			emotionSeries.getData().get(i).setYValue(0.0);
		}		
	}

	/* Public method to set the Keras Sequential model loaded by the ModelLoader class. */
	public void setModel(MultiLayerNetwork model)
	{
		seqModel = model;
	}	

	/* Public method to set the Keras Model (functional API) loaded by the ModelLoader class. */
	public void setModel(ComputationGraph model)
	{
		funcModel = model;
	}	
	
	/* Public method to set the imageStreamImageView. */
	public void setImageStreamImageView(Image image)
	{
		imageStreamImageView.setImage(image);
	}
	
	/* Public method to set the capturedFaceImageView. */
	public void setCapturedFaceImageView(Image image)
	{
		capturedFaceImageView.setImage(image);
	}

	/* Public method to enable or disable the camera buttons. */
	public void disableCameraButtons(boolean camera, boolean detection)
	{
		cameraButton.setDisable(camera);
		detectEmotionButton.setDisable(detection);
		disableCheckboxes(camera);
	}	

	/* Public method to enable or disable the file buttons and the input slider. */
	public void disableFileButtons(boolean video, boolean value)
	{
		openFileButton.setDisable(value);
		inputSlider.setDisable(value);		
		periodTextField.setDisable(value);		
		disableCheckboxes(value);
		// Update the content of the button in case a video is being analyzed.
		if(video)
		{
			if (value)
			{
				// Reset the progress bar and the graph values.
				updateProgressBar(0);									
				resetGraphValues();
				// Set the progress bar as visible.
				progressBarHBox.setVisible(true);	
				//Update the button content.
				detectEmotionButton.setText("Stop Detection");
			}
			else
			{
				detectEmotionButton.setText("Detect Emotions");
			}
		}
		else
		{
			detectEmotionButton.setDisable(value);
		}
	}
	
	/* Public method to enable or disable the checkboxes. */
	public void disableCheckboxes(boolean value)
	{
		imagesCheckBox.setDisable(value);
		csvCheckBox.setDisable(value);
	}	
	
	/* Public method for opening the window containing the program information. */
	public void openInformationWindow() 
	{
		try 
		{
			// Loading the FXML layout.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("InformationWindow.fxml"));
			// Create the reference to the BorderPane element.
			BorderPane element;
			element = (BorderPane) loader.load();
			// Scene creation.
			Scene scene = new Scene(element);
			// Create and visualization of the stage with the chosen title and with the scene previously created.
			Stage infoStage = new Stage();
			infoStage.setTitle("Informations");
			infoStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			infoStage.initModality(Modality.APPLICATION_MODAL);
			infoStage.setScene(scene);
			infoStage.show();

			// Loading the controller.
			InformationController controller = loader.getController();
			controller.SetStage(infoStage);
		} 
		catch (IOException e) 
		{
			showErrorDialog("An error occurred while opening the information window.", false);
		}
	}
	
	/* Method called by the "Detect Emotions" button. */
	@FXML
	protected void detectEmotions()
	{
		// Enters if the camera is active.
		if (inputSlider.getValue() <= 50)
		{
			// Enter if at least one face is available.
			if (!faceToPredict.empty())
			{
				// Block reading of new frames from camera and disable the slider.
				stopCamera(true);
				// Set the CapturedFaceImageView.
				setCapturedFaceImageView(Utils.mat2Image(faceToDisplay));
				// Reset the graph values.
				resetGraphValues();
				
				// Disable all the buttons until the end of the prediction.
				disableCameraButtons(true, true);

				// Create and start a new DetectEmotion thread.
				DetectEmotion ed;
				if (ModelLoader.sequential == true)
				{
					ed = new DetectEmotion(this, seqModel, faceToPredict, size, "", -1);
				}
				else
				{
					ed = new DetectEmotion(this, funcModel, faceToPredict, size, "", -1);
				}				
				Thread emotionDetector = new Thread(ed);
				emotionDetector.start();
			}
		}
		else
		{
			if (detectEmotionButton.getText().equals("Detect Emotions"))
			{
				String filePathLowerCase = filePath.toLowerCase();
				// Check if user has chosen an image file.
				if (filePathLowerCase.endsWith(".bmp") || filePathLowerCase.endsWith(".jpg") || filePathLowerCase.endsWith(".png") || filePathLowerCase.endsWith(".tiff"))
				{
					// Disable the file buttons.
					disableFileButtons(false, true);
								
					// Load the image, display it and and look for faces.
					Mat image = Imgcodecs.imread(filePath);
					setImageStreamImageView(Utils.mat2Image(image));
					Mat face = utils.detectFace(image);
					
					// Enter if a human face is found.
					if (face != null)
					{	
						// Set the CapturedFaceImageView.
						setCapturedFaceImageView(Utils.mat2Image(face));
						// Disable the slider.
						inputSlider.setDisable(true);
						// Disable the checkboxes.
						disableCheckboxes(true);
						// Reset the graph values.
						resetGraphValues();
						
						// Create and start a new DetectEmotion thread.
						DetectEmotion ed;
						if (ModelLoader.sequential == true)
						{
							ed = new DetectEmotion(this, seqModel, face, size, "", -1);
						}
						else
						{
							ed = new DetectEmotion(this, funcModel, face, size, "", -1);
						}				
						Thread emotionDetector = new Thread(ed);
						emotionDetector.start();
					}
					else
					{
						setCapturedFaceImageView(noFaceImage);
					}
					image.release();					
				}
				// Check if user has chosen a file.
				else if (filePathLowerCase.endsWith(".avi") || filePathLowerCase.endsWith(".mkv") || filePathLowerCase.endsWith(".mp4") || filePathLowerCase.endsWith(".mov"))
				{
					try 
					{
						// Check if the user has entered a number and this is equal to or greater than 0.1.
						double period = Double.parseDouble(periodTextField.getText());
						if (period >= 0.1)
						{
							// Verify that at least one checkbox is selected.
							if ((imagesCheckBox.isSelected()) || (csvCheckBox.isSelected()))
							{
								// Check if the file can be opened without problems.
								VideoCapture videoCapture = new VideoCapture();
								if (videoCapture.open(filePath))
								{	
									if (videoCapture.isOpened())
									{
										// Define a name for the file folder.
										String folderName = Utils.getFileName(filePath) + "_" + Utils.getTimestamp();
										// Disable the file buttons.
										disableFileButtons(true, true);
										
										// Create and start a new VideoAnalyzer thread.
										AnalyzeVideo va;
										if (ModelLoader.sequential == true)
										{
											va = new AnalyzeVideo(this, videoCapture, folderName, period, seqModel, size, noFaceImage, utils);
										}
										else
										{
											va = new AnalyzeVideo(this, videoCapture, folderName, period, funcModel, size, noFaceImage, utils);
										}							
										analyzeVideo = new Thread(va);
										analyzeVideo.start();
									}
									else
									{
										showErrorDialog("There was an error while starting the emotions detection.", true);
									}
								}
								else
								{
									showAttentionDialog("You must choose a valid file.");
								}
							}
							else
							{
								showInformationDialog("You must select at least one method to save the neural network predictions.");
							}
						}
						else
						{
							showInformationDialog("You must enter a number equal to or greater than 0.1.");
						}
					} 
					catch (NumberFormatException e) 
					{
						showAttentionDialog("You must enter a number to indicate the period of the neural network prediction request.");
					}
				}
				else
				{
					showAttentionDialog("You must choose a file.");
				}	
			}
			else
			{
				terminateVideoAnalyzer();
			}	
		}		
	}

	/**
	 * Camera methods.
	 */

	/* Method called by the "Start Camera" button. */
	@FXML
	protected void startCamera()
	{	
		if (!cameraCapture.isOpened())
		{
			// Disable the slider.
			inputSlider.setDisable(true);
			// Start the file capture.
			cameraCapture.open(0);
			// Reset the faceFound flag.
			faceFound = false;
			
			// Is the file stream available?
			if (cameraCapture.isOpened())
			{
				// Grab a frame every 33 ms (30 frames/sec).
				Runnable frameGrabber = new Runnable() 
				{					
					@Override
					public void run()
					{
						// Effectively grab, process and display a single frame.
						Image imageToShow = Utils.mat2Image(grabFrame());
						Platform.runLater(() -> 
						{
							setImageStreamImageView(imageToShow);
						});
					}
				};

				timer = Executors.newSingleThreadScheduledExecutor();
				timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// Update the button content.
				cameraButton.setText("Stop Camera");
			}
			else
			{
				showErrorDialog("An exception occurred while opening the camera connection.", false);
			}
		}
		else
		{
			// Stop the camera and enable the slider.
			stopCamera(false);
		}
	}

	/* Method that stops the acquisition from the camera, release all the resources and enable or disable the slider. */
	protected void stopCamera(boolean disableSlider)
	{
		if ((timer != null) && (!timer.isShutdown()))
		{
			try
			{
				// Stop the timer.
				timer.shutdown();
				timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				showErrorDialog("An exception occurred while stopping the capture of new frames: attempt to release the camera in progress.", false);
			}
		}

		// Release the camera if opened.
		if (cameraCapture.isOpened())
		{	
			cameraCapture.release();
		}
			
		// Update the button content.
		cameraButton.setText("Start Camera");
		
		// Enable or disable the slider.
		inputSlider.setDisable(disableSlider);
	}

	/* Method to obtain a frame from the opened file stream (if any). */ 
	private Mat grabFrame()
	{
		Mat frame = new Mat();

		// Check if the camera is active.
		if (cameraCapture.isOpened())
		{
			try
			{
				// Read the current frame and, if is not empty, process it.
				cameraCapture.read(frame);
				if (!frame.empty())
				{
					updateImages(frame);
				}
			}
			catch (Exception e)
			{
				showErrorDialog("An exception occurred while processing the image from the webcam.", true);
			}
		}

		return frame;
	}

	/* Method that update the image to be fed into the neural network and to be displayed. */
	protected void updateImages(Mat frame)
	{	
		// Look for faces in the image.
		Mat face = utils.detectFace(frame);
		
		// Enter if a human face is found.
		if (face != null)
		{
			// Prepares the image to be displayed.
			faceToDisplay = face.clone();
			faceToPredict = face.clone();			
			// Enable the button to make face detection. 
			if (faceFound == false)
			{
				faceFound = true;
				detectEmotionButton.setDisable(false);
			}
		}
	}

	/**
	 * File methods.
	 */

	/* Method called by the "Open File" button. */
	@FXML
	protected void openFile()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All supported files", "*.jpg", "*.mp4", "*.mov", "*.png"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image file", "*.jpg", "*.png"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video file", "*.mp4", "*.mov"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Joint Photographic Experts Group", "*.jpg"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Moving Picture Experts Group 4 (MPEG-4)", "*.mp4"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Portable Network Graphics", "*.png"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("QuickTime File Format", "*.mov"));
		File chosenFile = fileChooser.showOpenDialog(primaryStage);
		
		// Check if the user has selected a file.
		if (chosenFile != null)
		{
			filePath = chosenFile.getAbsolutePath();
			fileLabel.setText("Chosen file: " + filePath);
			// Enable the "Detect Emotions" button.
			detectEmotionButton.setDisable(false);
		}			
	}

	/* Public method for updating the emotions detection progress bar. */
	public void updateProgressBar(double value) 
	{
		progressBar.setProgress(value);
		progressBarLabel.setText(String.format("%.0f", value * 100) + "%");
	}
	
	/* Public method to terminate the video analyzer thread. */
	public void terminateVideoAnalyzer()
	{
		if (!detectEmotionButton.getText().equals("Detect Emotions"))
		{
			analyzeVideo.interrupt();
			// Set the progress bar as not visible.
			progressBarHBox.setVisible(false);		
		}
	}

	/**
	 * I/O methods.
	 */

	/* Method to the current prediction to the ./predictions folder. */
	private void saveGUIScreenshot(String directory, int frame)
	{
		try 
		{
			File imageName;
			// Enter here if it is a file frame.
			if (frame != -1)
			{
				File videoDirectory = createDirectory(outputDirectory + "\\" + "video", directory);
				imageName = new File(videoDirectory, frame + ".png");
			}
			// Otherwise if it is a single image enter here.
			else
			{
				File imagesDir = createDirectory(outputDirectory, "images");
				imageName = new File(imagesDir, imageCounter + ".png");
				while (imageName.exists())
				{
					imageCounter++;
					imageName = new File(imagesDir, imageCounter + ".png");
				}
			}
		
			// Save the screenshot.
			WritableImage screenshot = root.snapshot(new SnapshotParameters(), null);
			RenderedImage renderedImage = Utils.FXImageToAWTBufferedImage(screenshot);
			ImageIO.write(renderedImage, "png", imageName);		
		} 
		catch (IllegalStateException | IOException | SecurityException e) 
		{
			showErrorDialog("There was an error while saving the image to the predictions folder.", true);
		}
	}
	
	/* Method to write the predictions to the .csv file. */
	private void writePredictionsToCSVFile(double[] emotions, String FileName, int frame)
	{	
		// Create the csv directory if it doesn't exist.
		File csvDirectory = createDirectory(outputDirectory, "csv");
		String csvFileName = "images_" + imageTimestamp, imageName = "-", part = "Image";

		// Enters here if it is a video frame.
		if (frame != -1)
		{
			csvFileName = "video_" + FileName;
			imageName = frame + "";
			part = "Frame";
		}
		// Otherwise if it is a single image enter here.
		else
		{
			if (imagesCheckBox.isSelected())
			{
				imageName = imageCounter + ".png";
			}
		}		
		
		try
		{
			// Check if the file exists and, if it isn't, create it.
			boolean exist = false;
			File csvFile = new File(csvDirectory.getAbsolutePath(), csvFileName + ".csv");
			if (csvFile.exists())
			{
				exist = true;
			}
			FileWriter csvWriter = new FileWriter(csvFile, true);
			PrintWriter predictionsCSVFile = new PrintWriter(csvWriter);
			
			// Add header if the file did not exist.
			if (!exist)
			{
				predictionsCSVFile.println(part + ";Anger;Contempt;Disgust;Fear;Happiness;Neutrality;Sadness;Surprise");
			}			
			// Write the predictions.
			predictionsCSVFile.println(
				imageName + ";" +
				String.format("%.8f", emotions[0]) + ";" + 
				String.format("%.8f", emotions[1]) + ";" + 
				String.format("%.8f", emotions[2]) + ";" + 
				String.format("%.8f", emotions[3]) + ";" + 
				String.format("%.8f", emotions[4]) + ";" + 
				String.format("%.8f", emotions[5]) + ";" + 
				String.format("%.8f", emotions[6]) + ";" + 
				String.format("%.8f", emotions[7])
			);		
			
			// Release the acquired resources.
			predictionsCSVFile.flush();
			predictionsCSVFile.close();
			csvWriter.close();
		} 
		catch (IOException | SecurityException e) 
		{
			showErrorDialog("There was an error while saving the prediction inside the CSV file.", true);
		}
	}

	/* Method to create a directory. */
	private File createDirectory(String outerDirectory, String directory)
	{
		try 
		{
			File newDirectory = new File(outerDirectory, directory);
			if (!newDirectory.exists())
			{
				newDirectory.mkdirs();
			}
			return newDirectory;
		}
		catch (SecurityException e)
		{
			showErrorDialog("There was an error while creating a directory.", true);
		}		
		return null;
	}
	
	/**
	 * Dialog methods.
	 */

	/* Method to create an information dialog. */
	public void showInformationDialog(String message) 
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Emotion Detector");
		alert.setHeaderText("Information");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/* Method to create a warning dialog. */
	public void showAttentionDialog(String message) 
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Emotion Detector");
		alert.setHeaderText("Attention");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/* Public method to create an error dialog and, optionally, closes the program. */
	public void showErrorDialog(String error, boolean closeProgram)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Emotion Detector");
		alert.setHeaderText("Error");
		alert.setContentText(error);
		alert.showAndWait();
		// Optionally, closes the program.
		if (closeProgram) 
		{
			terminateVideoAnalyzer();
			Platform.exit();
		}		
	}
}