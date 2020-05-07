package it.polito.emotiondetector;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.application.Platform;
import javafx.scene.image.Image;

public class AnalyzeVideo implements Runnable
{ 
	private Controller controller;
	private ComputationGraph funcModel;
	private MultiLayerNetwork seqModel;
	private Mat frame = new Mat(), face = new Mat();
	private VideoCapture videoCapture;	
	private Image frameImage, faceImage, noFaceImage;
	private Utils utils;
	private int size, totalNumberOfFrames, frameNumber = 0, frameDivisor;
	private double period, value, previousValue = 0;
	private String fileName;
	
	public AnalyzeVideo(Controller controller, VideoCapture videoCapture, String fileName, double period, ComputationGraph model,  int size, Image noFaceImage, Utils utils) 
	{
		this.controller = controller;
		this.videoCapture = videoCapture;
		this.fileName = fileName;
		this.funcModel = model;
		this.period = period;
		this.size = size;
		this.noFaceImage = noFaceImage;
		this.utils = utils;
	}
	
	public AnalyzeVideo(Controller controller, VideoCapture videoCapture, String fileName, double period, MultiLayerNetwork seqModel, int size, Image noFaceImage, Utils utils)
	{
		this.controller = controller;
		this.videoCapture = videoCapture;
		this.fileName = fileName;
		this.seqModel = seqModel;
		this.period = period;
		this.size = size;
		this.noFaceImage = noFaceImage;
		this.utils = utils;
	}

	@Override
	public void run()
	{	
		// Get the total number of frames and calculate the frame divisor, rounded to the closest int.
		totalNumberOfFrames = (int) videoCapture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);		
		frameDivisor = (int) Math.round(period * videoCapture.get(Videoio.CV_CAP_PROP_FPS));
		
		// Analyze the entire file.
		while (videoCapture.read(frame) && (!Thread.currentThread().isInterrupted()))
		{
			frameNumber++;
			// Update the progress bar only if the increment is over 1%.
			value = (double) frameNumber / (double) totalNumberOfFrames;
			if ((value <= 1) && (value - previousValue >= 0.01)) 
			{
				Platform.runLater(() -> controller.updateProgressBar(value));
				previousValue = value;
			}	
			// Display and process the frame if not empty and if multiple of the period.
			if ((!frame.empty()) && (frameNumber % frameDivisor == 0))
			{
				// Convert the frame to mat and update the ImageStreamImageView.
				frameImage = Utils.mat2Image(frame);
				Platform.runLater(() ->	
				{
					controller.setImageStreamImageView(frameImage);
				});		
				// Look for faces in the image.
				face = utils.detectFace(frame);
				if (face != null)
				{
					// Convert the frame to mat and update the CapturedFaceImageView..
					faceImage = Utils.mat2Image(face);
					Platform.runLater(() ->	
					{
						controller.setCapturedFaceImageView(faceImage);
					});

					// Perform emotion detection.
					DetectEmotion ed;
					if (ModelLoader.sequential == true)
					{
						ed = new DetectEmotion(controller, seqModel, face, size, fileName, frameNumber);
					}
					else
					{
						ed = new DetectEmotion(controller, funcModel, face, size, fileName, frameNumber);
					}				
					ed.detectEmotion();
					// Release the acquired resources.
					face.release();
				}
				else
				{
					// Reset the graph values and update the CapturedFaceImageView.
					Platform.runLater(() ->	
					{
						controller.resetGraphValues();
						controller.setCapturedFaceImageView(noFaceImage);
					});
				}
				// Release the acquired resources.
				frame.release();
			}
			// Make the thread idle for a limited time so as not to clog the controller with requests.
			if (!Thread.currentThread().isInterrupted())
			{
				try 
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e) 
				{	
					Thread.currentThread().interrupt();
				}
			}			
		}
		if (Thread.currentThread().isInterrupted()) 
		{
			Platform.runLater(() -> 
			{
				controller.showAttentionDialog("Classification interrupted by the user.");
			});			
		}
		else
		{
			Platform.runLater(() -> {
				controller.updateProgressBar(1);
				controller.showInformationDialog("Video analysis finished.");
			}); 
		}
		// Enable the file buttons and the input slider
		Platform.runLater(() -> 
		{
			controller.disableFileButtons(true, false);
		});
		// Release the acquired resources.
		videoCapture.release();
		frame.release();
		face.release();
	}
}
