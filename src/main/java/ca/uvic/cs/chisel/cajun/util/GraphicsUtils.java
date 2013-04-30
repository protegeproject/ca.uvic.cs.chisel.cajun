/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.HashMap;

import javax.swing.GrayFilter;

/**
 * Some useful methods for working with colors and images.
 *
 * @author Rob Lintern
 * @author Chris Callendar
 */
public class GraphicsUtils {

	private static final HashMap<String, Color> COLORS = new HashMap<String, Color>(13);

	static {
		COLORS.put("black", Color.black);
		COLORS.put("blue", Color.blue);
		COLORS.put("cyan", Color.cyan);
		COLORS.put("darkgray", Color.darkGray);
		COLORS.put("gray", Color.gray);
		COLORS.put("green", Color.green);
		COLORS.put("lightgray", Color.lightGray);
		COLORS.put("magenta", Color.magenta);
		COLORS.put("orange", Color.orange);
		COLORS.put("pink", Color.pink);
		COLORS.put("red", Color.red);
		COLORS.put("white", Color.white);
		COLORS.put("yellow", Color.yellow);
	}

	/**
	 * Attempts to convert the given string to a color.
	 * The string can either be one of the 13 main colors:<br>
	 * {black, blue, cyan, darkGray, gray, green, lightGray, magenta, orange, pink, red, white, yellow}<br>
	 * or a hex value:<br>#00FF00 or 00FF00
	 * @param value the string to parse
	 * @param defaultColor the default color to return if the value can't be parsed
	 * @return Color or null
	 */
	public static Color stringToColor(String value, Color defaultColor) {
		Color c = GraphicsUtils.stringToColor(value);
		return (c == null ? defaultColor : c);
	}

	/**
	 * Attempts to convert the given string to a color.
	 * The string can either be one of the 13 main colors:<br>
	 * {black, blue, cyan, darkGray, gray, green, lightGray, magenta, orange, pink, red, white, yellow}<br>
	 * or a hex value:<br>#00FF00 or 00FF00
	 * @param value the string to parse
	 * @return Color or null if the string couldn't be parsed
	 */
	public static Color stringToColor(String value) {
		Color c = null;
		if (value != null) {
			String key = value.toLowerCase();
			if (COLORS.containsKey(key)) {
				c = COLORS.get(key);
			} else {
				c = GraphicsUtils.hexStringToColor(value);
			}
			if (c == null) {
				c = rgbStringToColor(value);
			}
		}
		return c;
	}

	/**
	 * Attempts to parse an RGB triplet or an RGBA quadruplet from the string.
	 * It searches through each character and tries to pull out 3 or 4 integers (between 0-255)
	 * to use for red, green, blue, and alpha.
	 * @param value the string to parse
	 * @return the {@link Color} or null
	 */
	public static Color rgbStringToColor(String value) {
		Color c = null;
		if ((value != null) && (value.length() > 0)) {
			int[] rgba = { -1, -1, -1, 255 };
			StringBuffer buffer = new StringBuffer(3);;
			boolean inNumber = false;
			int index = 0;
			try {
				if (Character.isDigit(value.charAt(value.length() - 1))) {
					// handles the case where the last character is a number
					value = value + " ";
				}
				for (int i = 0; (i < value.length()) && (index < rgba.length); i++) {
					char ch = value.charAt(i);
					if (Character.isDigit(ch)) {
						inNumber = true;
						buffer.append(ch);
					} else if (inNumber) {
						inNumber = false;
						int num = Integer.parseInt(buffer.toString());
						num = Math.max(0, Math.min(255, num));
						rgba[index++] = num;
						buffer = new StringBuffer(3);
					}
				}
				if (index >= 3) {
					c = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
				}
			} catch (NumberFormatException ignore) {
			}
		}
		return c;
	}

	/**
	 * Parses a hex color string (e.g. #FF0000 or FF0000).
	 * If the color value couldn't be parsed correctly, null will be returned.
	 * @param value	the hex color value to parse
	 * @return the color represented by the string, or null if the string was malformed
	 */
	public static Color hexStringToColor(String value) {
		Color c = null;
		if (value != null) {
			value = value.trim();
			if (value.startsWith("#")) {
				value = value.substring(1);
			}
			try {
				c = new Color(Integer.parseInt(value, 16));
			} catch (NumberFormatException ignore) {
				c = null;
			}
		}
		return c;
	}

	/**
	 * Compares the given color to the 12 main colors and returns
	 * the name (e.g. "red") if it is one of them.  Otherwise the hex value
	 * like #FF0000 is returned.
	 * @param color
	 * @return String "red" or "#ff0000"
	 */
	public static String colorToString(Color color) {
		if (color != null) {
			// check the main colors
			for (String name : COLORS.keySet()) {
				Color c = COLORS.get(name);
				if (c.equals(color)) {
					return name;
				}
			}
			// return the hex value
			return GraphicsUtils.colorToHexString(color);
		}
		return "";
	}

	/**
	 * Converts a color into a String like 255,0,0.
	 * @param color	Color to convert into a String.
	 * @return String color RGB String.
	 */
	public static String colorToRGBString(Color color) {
		String rgb = "";
		if (color != null) {
			rgb = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
		}
		return rgb;
	}

	/**
	 * Converts a color into a String like "255,0,0,255" (red,green,blue,alpha).
	 * @param color	Color to convert into a String.
	 * @return String color RGB String or the empty string
	 */
	public static String colorToRGBAString(Color color) {
		String rgba = "";
		if (color != null) {
			rgba = color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha();
		}
		return rgba;
	}

	/**
	 * Converts a color into a String like #RRGGBB.
	 * @param color	Color to convert into a String.
	 * @return String color String.
	 */
	public static String colorToHexString(Color color) {
		String colorStr = "";
		if (color != null) {
			colorStr = Integer.toHexString(color.getRGB());
			colorStr = "#" + colorStr.substring(2);	// chop off the alpha value
		}
		return colorStr;
	}

	/**
	 * Fades the color by multiplying the saturation value by 0.5 and the brightness by 1.3.
	 * @param color
	 * @return Color
	 */
	public static Color fadeColor(Color color) {
		return fadeColor(color, 0.5f, 1.3f);
	}

	/**
	 * Fades the color by multiplying the sFactor to the color's saturation, and
	 * multiplying bFactor by the color's brightness.
	 * @param color
	 * @param sFactor the saturation factor
	 * @param bFactor the brightness factor
	 * @return Color
	 */
	public static Color fadeColor(Color color, float sFactor, float bFactor) {
		float[] hsb = new float[3];
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		Color.RGBtoHSB(r, g, b, hsb);
		// we want a colour with the same hue, slightly less saturation, and slightly more brightness
		hsb[1] = hsb[1] * sFactor; // doesn't work so well with colors close to grey, because no colour to saturate
		hsb[2] = Math.min(1.0f, hsb[2] * bFactor); // if brightness goes above 1.0 we get a different hue!?
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	/**
	 * Creates a faded image
	 */
	public static Image createFadedImage(Image i) {
		ImageFilter filter = new RGBImageFilter() {
			public int filterRGB(int x, int y, int rgb) {
				Color color = new Color (rgb);
				float alpha = 0.60f;
				//float[] hsb = new float[3];
				//Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
				//hsb[1] = f; // doesn't work so well with colors close to grey, because no colour to saturate
				//hsb[2] = 85f; // if brightness goes above 1.0 we get a different hue!?
				//Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
				Color newColor = new Color (color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, alpha);
				return newColor.getRGB();
			}
		};
		ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
		Image fadedImage = Toolkit.getDefaultToolkit().createImage(prod);
		return fadedImage;
	}

	public static Image createGrayedImage(Image i, final boolean b, final int p){
		ImageFilter filter = new GrayFilter (b, p);
		ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
		Image grayImage = Toolkit.getDefaultToolkit().createImage(prod);
		return grayImage;
	}

	/**
	 * This is a weaker version of {@link Color#brighter()}
	 * Uses a factor closer to 1.
	 * @see Color#brighter()
	 * @return brighter Color
	 */
    public static Color brighter(Color c) {
		float[] hsb = new float[3];
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		Color.RGBtoHSB(r, g, b, hsb);
		//System.out.println("Saturation: " + hsb[1] + "   ->   " + (hsb[1] * 1.05f));
		//System.out.println("Brightness: " + hsb[2] + "   ->   " + (hsb[2] * 1.05f));
		return Color.getHSBColor(hsb[0], Math.min(1f, hsb[1] * 1.3f), Math.min(1f, hsb[2] * 1.05f));
    }

	/**
	 * This is a weaker version of {@link Color#brighter()}
	 * Uses a factor closer to 1.
	 * @see Color#darker()
	 * @return darker Color
	 */
    public static Color darker(Color c) {
		float[] hsb = new float[3];
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		Color.RGBtoHSB(r, g, b, hsb);
		//System.out.println("Saturation: " + hsb[1] + "   ->   " + (hsb[1] * 0.95f));
		//System.out.println("Brightness: " + hsb[2] + "   ->   " + (hsb[2] * 0.95f));
		return Color.getHSBColor(hsb[0], hsb[1] * 0.7f, hsb[2] * 0.95f);
    }

	/**
	 * @param bgColor
	 * @return Black or white, depeding on the "darkness" of bgColor.
	 */
	public static Color getTextColor(Color bgColor) {
		Color textColor = Color.black;
//		float[] hsbVals = null;
//		hsbVals = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), hsbVals);
//		if (hsbVals[2] <= 0.8f) {
//			textColor = Color.white; // if the background is dark then make the text color white
//		}

		// use white if the sum of red (0-255), green and blue is small
		if (bgColor != null) {
			int rgb = bgColor.getRed() + bgColor.getGreen() + bgColor.getBlue();
			if (rgb < 400) {
				textColor = Color.white;
			}
		}
		return textColor;
	}
	
	public static Color colorOf(Paint paint) {
		if (paint instanceof Color) {
			return (Color) paint;
		} else if (paint instanceof GradientPaint) {
			GradientPaint gp = (GradientPaint) paint;
			return gp.getColor2();
		}
		return Color.black;
	}

	public static Color invertColor(Color color) {
		if (color != null) {
			return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
		}
		return Color.black;
	}

}
