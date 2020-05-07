package it.polito.emotiondetector;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/** MODIFIED VERSION by Antonio Marceddu of 
 * https://github.com/opencv-java/face-detection
 * Original references of the author can be found below. */

/**
 * Provide general purpose methods for handling OpenCV-JavaFX data conversion.
 * Moreover, expose some "low level" methods for matching few JavaFX behavior.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @author <a href="http://max-z.de">Maximilian Zuleger</a>
 * @version 1.0 (2016-09-17)
 * @since 1.0
 * 
 */
public class Utils
{
	private int absoluteFaceSize = 0;
	
	// Face cascade classifier.
	private CascadeClassifier faceCascade = new CascadeClassifier();
	
	public boolean initFaceCascade()
	{
		return faceCascade.load(".\\lib\\haarcascade_frontalface_alt.xml");
	}
	
	/* Public method an Image object in the corresponding BufferedImage. */
	public static BufferedImage FXImageToAWTBufferedImage(Image img)
	{
		return SwingFXUtils.fromFXImage(img, null);
	}
	
	
	/* Public method for converting a Mat object (OpenCV) in the corresponding Image for JavaFX. */
	public static Image mat2Image(Mat frame)
	{
		return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
	}

	/* Support method for the mat2image() method. */
	private static BufferedImage matToBufferedImage(Mat original)
	{
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);
		
		if (original.channels() > 1)
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		else
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return image;
	}
	
	/* Method to retrieve the file name from a path. */
	public static String getFileName(String fileName) 
	{
		if (fileName.contains("\\") && fileName.contains(".") ) 
		{
			return fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lastIndexOf('.'));
		}
		return fileName;
	}
	
	public static String getTimestamp()
	{
		Timestamp ts = new Timestamp(System.currentTimeMillis());		
		return new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(ts);
	}
	
	/* Public method for performing face detection. */
	public Mat detectFace(Mat frame)
	{
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();

		// Do the operations with the grayscale version of the image in order to improve recognition performance.
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

		// compute minimum face size (20% of the frame height, in our case).
		if (this.absoluteFaceSize == 0)
		{
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0)
			{
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}

		// Searches for faces in image.
		faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
		Rect[] facesArray = faces.toArray();

		// Release the acquired resources.
		faces.release();
		grayFrame.release();
		
		// If at least one face is detected, the photo will be cropped to this one: the others will be lost.
		if (facesArray.length > 0)
		{
			return frame.submat(new Rect(facesArray[0].x, facesArray[0].y, facesArray[0].width, facesArray[0].width));
		}
		
		return null;
	}
}