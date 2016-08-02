package alexndr.api.core;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author AleXndrTheGr8st
 */
public class APIInfo {
	public static final String ID = "simplecore";
	public static final String NAME = "SimpleCore API";
	public static final String DESCRIPTION = "An API for easy creation of plugins such as SimpleOres, Fusion and others.";
	public static final String VERSION = "@MODVERSION@";
	public static final String ACCEPTED_VERSIONS = "[1.9.4,1.10.2]";
	public static final String URL = "https://minecraft.curseforge.com/projects/simplecore-api";
	public static final List<String> AUTHORS = Lists.newArrayList("AleXndrTheGr8st");
	public static final String CREDITS = "Created by AleXndrTheGr8st.";
	public static final String LOGO = "assets/simplecore/logos/SimpleCore.png";
	public static final String PARENT = "";
	public static final boolean USEDEPENDENCYINFO = true;
	public static final String DEPENDENCIES = "required-after:Forge@[12.17.0.1950,)";

	// use mcmod.info instead, so that tools that look at the jar file such as MultiMC
	// and possibly Curse Client can get modinfo -- Sinhika
	// deleted out-of-date code
} // end class
