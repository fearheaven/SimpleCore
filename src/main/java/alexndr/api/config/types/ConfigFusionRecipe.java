package alexndr.api.config.types;

import java.util.List;


import com.google.common.collect.Lists;

public class ConfigFusionRecipe extends ConfigEntry 
{
	// private List<ConfigValue> valuesList = Lists.newArrayList();

	// default values
	private ConfigValue input1 = new ConfigValue("Input1").setDataType("@S")
			.setCurrentValue("input1");
	private ConfigValue input2 = new ConfigValue("Input2").setDataType("@S")
			.setCurrentValue("input2");
	private ConfigValue catalyst = new ConfigValue("Catalyst")
			.setDataType("@S").setCurrentValue("catalyst");
	private ConfigValue output = new ConfigValue("Output").setDataType("@S")
			.setCurrentValue("output");

	public ConfigFusionRecipe(String name, String category) {
		super(name, category);
		this.valuesList.addAll(Lists.newArrayList(input1, input2, catalyst,
				output));
		super.setValuesList(valuesList);
	}

	@Override
	public List<ConfigValue> getValuesList() {
		return valuesList;
	}

	@Override
	public void setValuesList(List<ConfigValue> valuesList) {
		this.valuesList = valuesList;
	}

	@Override
	public ConfigValue createNewValue(String valueName) {
		ConfigValue value = new ConfigValue(valueName);
		value.setActive();
		valuesList.add(value);
		return value;
	}

	@Override
	public ConfigEntry createNewValue(String valueName, String dataType,
			String currentValue, String defaultValue) {
		ConfigValue value = new ConfigValue(valueName);
		value.setActive();
		value.setDataType(dataType);
		value.setCurrentValue(currentValue);
		value.setDefaultValue(defaultValue);
		valuesList.add(value);
		return this;
	}

	@Override
	public ConfigValue getValueByName(String valueName) {
		for (ConfigValue value : this.valuesList) {
			if (value.getName().equals(valueName))
				return value;
		}
		return null;
	}

	public String getInput1() {
		return this.getValueByName(input1.getName()).getCurrentValue();
	}

	public void setInput1(ConfigValue input1) 
	{
		this.input1.setActive().setDataType("@S")
				.setCurrentValue(input1.getCurrentValue())
				.setDefaultValue(input1.getDefaultValue());
		this.getValueByName(input1.getName())
				.setCurrentValue(input1.getCurrentValue())
				.setDefaultValue(input1.getDefaultValue());
	}

	public String getInput2() {
		return this.getValueByName(input2.getName()).getCurrentValue();
	}

	public void setInput2(ConfigValue input2) 
	{
		this.input2.setActive().setDataType("@S")
				.setCurrentValue(input2.getCurrentValue())
				.setDefaultValue(input2.getDefaultValue());
		this.getValueByName(input2.getName())
				.setCurrentValue(input2.getCurrentValue())
				.setDefaultValue(input2.getDefaultValue());
	}

	public String getCatalyst() {
		return this.getValueByName(catalyst.getName()).getCurrentValue();
	}

	public void setCatalyst(ConfigValue catalyst) 
	{
		this.catalyst.setActive().setDataType("@S")
				.setCurrentValue(catalyst.getCurrentValue())
				.setDefaultValue(catalyst.getDefaultValue());
		this.getValueByName(catalyst.getName())
				.setCurrentValue(catalyst.getCurrentValue())
				.setDefaultValue(catalyst.getDefaultValue());
	}

	public String getOutput() {
		return this.getValueByName(output.getName()).getCurrentValue();
	}

	public void setOutput(ConfigValue output) 
	{
		this.output.setActive().setDataType("@S")
				.setCurrentValue(output.getCurrentValue())
				.setDefaultValue(output.getDefaultValue());
		this.getValueByName(output.getName())
				.setCurrentValue(output.getCurrentValue())
				.setDefaultValue(output.getDefaultValue());
	}

} // end class
