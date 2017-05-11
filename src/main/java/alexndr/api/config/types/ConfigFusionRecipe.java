package alexndr.api.config.types;

import alexndr.api.config.ConfigHelper;
import net.minecraftforge.common.config.Configuration;

public class ConfigFusionRecipe extends ConfigEntry 
{
	// private List<ConfigValue> valuesList = Lists.newArrayList();

	// default values
	private String input1 = new String();
	private String input2 = new String();
	private String catalyst = new String();
	private String output = new String();
	protected Integer index;

	public ConfigFusionRecipe(String name, String category, int number) 
	{
		super(name, ConfigHelper.CATEGORY_RECIPE, true);
		this.index = new Integer(number);
		 
		// special subcategory:
		subcategory = ConfigHelper.CATEGORY_RECIPE + Configuration.CATEGORY_SPLITTER 
						+  "CustomRecipes" + Configuration.CATEGORY_SPLITTER 
						+ "CustomRecipe#" + index.toString() 
						+ Configuration.CATEGORY_SPLITTER + name;
	}

	@Override
	public void GetConfig(Configuration config) 
	{
		super.GetConfig(config);
		input1 = config.getString("input1", subcategory, input1, 
								 "input1 (ore dict string or item name)");
		input2 = config.getString("input2", subcategory, input2, 
				 "input2 (ore dict string or item name)");
		catalyst = config.getString("catalyst", subcategory, catalyst, 
				 "catalyst (ore dict string or item name)");
		output = config.getString("output", subcategory, output, 
				 "output (ore dict string or item name)");
	} // end GetConfig()

	public String getInput1() {
		return this.input1;
	}

	public ConfigFusionRecipe setInput1(String input1) 
	{
		this.input1 = input1;
		return this;
	}

	public String getInput2() {
		return this.input2;
	}

	public ConfigFusionRecipe setInput2(String input2) 
	{
		this.input2 = input2;
		return this;
	}

	public String getCatalyst() {
		return this.catalyst;
	}

	public ConfigFusionRecipe setCatalyst(String catalyst) 
	{
		this.catalyst = catalyst;
		return this;
	}

	public String getOutput() {
		return this.output;
	}

	public ConfigFusionRecipe setOutput(String output) 
	{
		this.output = output;
		return this;
	}
	
	public ConfigFusionRecipe setAll(String input1, String input2, String catalyst, String output)
	{
		this.input1 = input1;
		this.input2 = input2;
		this.catalyst = catalyst;
		this.output = output;
		return this;
	} 

} // end class
