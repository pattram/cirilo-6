
package voodoosoft.jroots.core;


public interface IDirtyFlagModifier
{
   public void setDirtyFlag(boolean dirtyFlag);
   
   public void addDirtyFlagListener(IDirtyFlagListener listener);
   
   public void removeDirtyFlagListener(IDirtyFlagListener listener);
}
