SimpleCore
==========

Repository for the SimpleCore API by AleXndrTheGr8st


Roadmap of Future releases
--------------------------

### Soonish:

- revise to support 1.11 with CompatLayer changes
- add support for config GUI to all 4
- restructure dependencies so that alloy blocks/items can be added without
requiring Fusion installed (because other industrial mods have alloy furnaces)
- ~~port Machines to 1.10.2~~ DONE/ALPHA RELEASED
- ~~add support for JEI to Fusion Furnace~~ DONE
- ~~re-write Fusion furnace/Nether Furnace to use IItemHandler~~ DONE
- ~~re-factor IItemHandler-based stuff to be useful in Machines, too.~~ PROBABLY DONE
- ~~needed for Aesthetics: add SimpleDoor, SimpleDoorItem, SimpleBars, SimpleStairs to SimpleCoreAPI.~~ DONE
- ~~port Aesthetics to 1.10.2~~ BETA RELEASED
- ~~add French localization from fearheaven to Aesthetics~~ DONE
- remove config options for properties that should not be configured. STARTED 

### Later/other:

- add ModTweaker support for Fusion Furnace recipes.
- port akkkamaddi's Additions (see that thread later on)
- add optional recipes for other mods' alloy furnaces.
- re-write config system to use JSON instead of XML
- remove ISidedInventory support and wrappers and go pure capability.
- re-visit SimpleBucket code and models. They probably aren't quite right.
- ~~explore non-consumed catalyst option~~ Explored on Forum. Not popular idea.
- ~~add a Zinc plugin (Sinhika's Simple Zinc?) with Brass alloy for use with
TiC, among others~~ BEING DONE BY THEOLDONE.
- add more language files (depends on contributors)
  + try to add Spanish
  + try to add Japanese
- add other new features
  + metal tanks
  + metal shields

License
-------

This mod is free software: you can redistribute it and/or modify it under the
terms of the GNU Lesser General Public License as published by the Free
Software Foundation, either version 3 of the License, or any later version.

This mod is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
details.

You should have received a copy of the GNU Lesser General Public License along
with this source code.  If not, see <http://www.gnu.org/licenses/>.
