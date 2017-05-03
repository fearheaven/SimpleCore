package alexndr.api.config;

import alexndr.api.config.types.ConfigBlock;

/**
 * Standard configuration-related methods for all Simple* block classes.
 * @author Sinhika
 *
 */
public interface IConfigureBlockHelper<T>
{
    /**
     * Returns the ConfigBlock used by this block.
     * @return ConfigBlock
     */
    public ConfigBlock getConfigEntry();
    
    /**
     * Sets the ConfigBlock to be used for this block.
     * @param entry ConfigBlock
     * @return SimpleBlock
     */
    public T setConfigEntry(ConfigBlock entry);
    
    /**
     * Adds a tooltip to the block. Must be unlocalised, so needs to be present in a localization file.
     * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
     * @return SimpleBlock
     */
    public T addToolTip(String toolTip);
    
    public void setAdditionalProperties();

} // end interface
