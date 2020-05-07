package it.polito.emotiondetector;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.datavec.image.loader.NativeImageLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javafx.application.Platform;

public class DetectEmotion implements Runnable
{ 
	private Controller controller;
	private ComputationGraph funcModel;
	private MultiLayerNetwork seqModel;
	private Mat image;
	private int size, frame;
	String FileName;
	
	/* Constructors. */
	public DetectEmotion(Controller controller, ComputationGraph model, Mat image, int size, String FileName, int frame) 
	{
		this.controller = controller;
		this.funcModel = model;
		this.image = image;
		this.size = size;
		this.FileName = FileName;
		this.frame = frame;
	}
	
	public DetectEmotion(Controller controller, MultiLayerNetwork seqModel, Mat image, int size, String FileName, int frame)
	{
		this.controller = controller;
		this.seqModel = seqModel;
		this.image = image;
		this.size = size;
		this.FileName = FileName;
		this.frame = frame;
	}
	
	@Override
	public void run()
	{
		detectEmotion();		
	}
	
	/* Public method for performing emotion detection. */
	public void detectEmotion()
	{
		try
		{
			// Process the image.
			processImage();			
			// Adjust the image to be used by the neural network.
            NativeImageLoader loader = new NativeImageLoader(size, size, 1);
            INDArray input = loader.asMatrix(image);
	    	// Prediction of the facial expression.
			if (ModelLoader.sequential == true)
			{
				INDArray output = seqModel.output(input);
				// Update the graph values and enable the "Detect Emotion" button.	
				Platform.runLater(() -> controller.updateGraphValuesAndDocumentResults(output.toDoubleVector(), FileName, frame));
			}
			else
			{
				INDArray[] output = funcModel.output(input);
				// Update the graph values and enable the "Detect Emotion" button.
				Platform.runLater(() -> controller.updateGraphValuesAndDocumentResults(output[0].toDoubleVector(), FileName, frame));
			}
		}
		catch (Exception e)
		{		
			e.printStackTrace();
			Platform.runLater(() -> 
			{
				controller.showErrorDialog("There was an error during the emotion detection process.", true);
			});
		}	
		finally
		{
			// Release the resources.
			image.release();
		}
	}
	
	/* Method to process the image in order to be a good input for the neural network. */
	private void processImage()
	{
		// Convert the image to grayscale.
		Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
		
		// Resize and change the image format.
		Imgproc.resize(image, image, new Size(size, size));
		image.convertTo(image, CvType.CV_32F);		
		
		// Z-score standardization of the image.
		MatOfDouble mean = new MatOfDouble();
		MatOfDouble std = new MatOfDouble();
		Core.meanStdDev(image, mean, std);	

		double[] meanArray = mean.toArray();
		double[] stdArray = std.toArray();

		Core.subtract(image, new Mat(image.size(), CvType.CV_32F, new Scalar(meanArray[0])), image);
		Core.divide(image, new Mat(image.size(), CvType.CV_32F, new Scalar(stdArray[0])), image, CvType.CV_32F);
	}
}
