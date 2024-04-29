package org.emile.client.ptif;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriter;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi;
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;

public class PyramidalTiff {

	private boolean isPyramidalTiff;


	public byte[] build(byte[] stream) throws IOException {

		TIFFImageWriterSpi imageWriterSpi = new TIFFImageWriterSpi();
		TIFFImageWriter imageWriter = (TIFFImageWriter) imageWriterSpi.createWriterInstance();

		File temp = File.createTempFile("temp", ".tmp");
		ImageOutputStream out = new FileImageOutputStream(temp);

		isPyramidalTiff = false;

		try {

			InputStream is = new ByteArrayInputStream(stream);
	
			ImageInputStream imageInputStream = ImageIO.createImageInputStream(is);
			if (imageInputStream != null && imageInputStream.length() != 0) {
				Iterator<ImageReader> iteratorIO = ImageIO.getImageReaders(imageInputStream);
				if (iteratorIO != null && iteratorIO.hasNext()) {
					ImageReader reader = iteratorIO.next();
					reader.setInput(imageInputStream);
					isPyramidalTiff = reader.getNumImages(true) > 2;
    	       // 	BufferedImage bufferedImage = reader.read(0);

				}
			}
			is.close();
			
			if (!isPyramidalTiff) {
				
				is = new ByteArrayInputStream(stream);
				BufferedImage image = ImageIO.read(is);
				
				imageWriter.setOutput(out);
				imageWriter.prepareWriteSequence(null);

				int width = 0;
				int height = 0;

				for (int i = 0; i < 4; i++) {

					width = new Double(image.getWidth() * 0.5).intValue();
					height = new Double(image.getHeight() * 0.5).intValue();

					
					if (height > 0)
						image = Scalr.resize(image, Scalr.Method.BALANCED, width, height);
					
					
					TIFFImageWriteParam imageWriteParam = (TIFFImageWriteParam) imageWriter.getDefaultWriteParam();
					if (256 > 0 && (image.getWidth() > 256 || image.getHeight() > 256)) {
						imageWriteParam.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
						imageWriteParam.setTiling(256, 256, 0, 0);
					}
			     
					
			            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			            imageWriteParam.setCompressionType("JPEG");
			            imageWriteParam.setCompressionQuality(1.0f);
			     
			        
 
					imageWriter.writeToSequence(new IIOImage(image, null, null), imageWriteParam);
					


				}

				imageWriter.endWriteSequence();
				out.close();

				stream = FileUtils.readFileToByteArray(temp);
				temp.deleteOnExit();
				is.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stream;

	}
}