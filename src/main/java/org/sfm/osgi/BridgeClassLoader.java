package org.sfm.osgi;
public class BridgeClassLoader extends ClassLoader {
  private final ClassLoader secondary;
 
  public BridgeClassLoader(ClassLoader primary, ClassLoader secondary) {
    super(primary);
    this.secondary = secondary;
  }
 
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    return secondary.loadClass(name);
  }
}