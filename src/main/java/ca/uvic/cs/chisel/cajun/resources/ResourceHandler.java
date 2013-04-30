package ca.uvic.cs.chisel.cajun.resources;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * 
 *
 * @author Chris Callendar
 * @since  30-Oct-07
 */
public class ResourceHandler {


    /**
     * Tries to loads an Icon with the given name that is located in the
     * icons directory of this project.
     * If the icon cannot be loaded, null is returned.
     */
    public static ImageIcon getIcon(String name) {
    	return getIcon("/" + name, ResourceHandler.class);
    }
    
    /**
     * Tries to load an icon with the given name and path using the given class.
     * @param path the relative or absolute path of the icon
     * @param cls the class to use as a ClassLoader
     * @return the Icon or null
     */
    public static ImageIcon getIcon(String path, Class<?> cls) {
        ImageIcon icon = null;
        if (cls == null) {
        	cls = ResourceHandler.class;
        }
        if (path != null) {
	        URL url = cls.getResource(path);
	        icon = (url != null) ? new ImageIcon(url) : new ImageIcon(path);
        }
        return icon;
    }

    /**
     * Loads an image from the "icons" directory and returns it.
     * @param name the name of the image relative to the icons directory (e.g. "icon_close.gif")
     * @return Image or null if not found
     */
    public static Image getIconAsImage(String name) {
    	ImageIcon icon = getIcon(name);
    	return (icon != null ? icon.getImage() : null);
    }

    public static Image getIconAsImage(String path, Class<?> cls) {
    	ImageIcon icon = getIcon(path, cls);
    	return (icon != null ? icon.getImage() : null);
    }
	
}
