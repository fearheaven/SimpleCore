
package alexndr.api.helpers.game;

/**
 * Standard configuration-related methods for all Simple* item classes.
 * @author Sinhika
 *
 * @param T subclass of Item
 * @param U subclass of ConfigEntry
 */
public interface IConfigureItemHelper<T, U>
{
    /**
     * Returns the config entry used by this item.
     * @return config entry
     */
    public U getConfigEntry();
    
    /**
     * Sets the config entry to be used for this item.
     * @param entry config entry U
     * @return T
     */
    public T setConfigEntry(U entry);
    
    /**
     * Adds a tooltip to the item. Must be unlocalised, so needs to be present in a localization file.
     * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
     * @return T
     */
    public T addToolTip(String toolTip);
    
    public void setAdditionalProperties();


} // end interface
