package org.agfjord;

import java.util.Vector;

public class ClassScope {
    private static java.lang.reflect.Field LIBRARIES = null;
    static {
        try {
			LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LIBRARIES.setAccessible(true);
    }
    public static String[] getLoadedLibraries(final ClassLoader loader) throws IllegalArgumentException, IllegalAccessException {
        final Vector<String> libraries = (Vector<String>) LIBRARIES.get(loader);
        return libraries.toArray(new String[] {});
    }
}