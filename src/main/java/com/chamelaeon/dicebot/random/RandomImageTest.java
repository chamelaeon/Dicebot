package com.chamelaeon.dicebot.random;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.chamelaeon.dicebot.Statistics;
import com.chamelaeon.dicebot.random.Random.MersenneTwisterRandom;

public class RandomImageTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Statistics statistics = new Statistics();
		MersenneTwisterRandom random = new MersenneTwisterRandom(statistics);
		
		int height = 512;
		int width = 512;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		
		for (int y = 0; y < height; y++) {
		    for (int x=0; x < width; x++){
		        if (random.getRoll(2) == 2) {
		        	image.setRGB(x, y, 0);
		        } else{
		        	image.setRGB(x, y, Integer.MAX_VALUE);
		        }
		    }
		}
		
		ImageIO.write(image, "PNG", new File("random.png"));
	}

}
