This is a plugin that contains several features that I think would improve the classic survival experience. This plugin is only functional in Spigot 1.20.1.

### Features
**Agriculture:**
- Crops now grow at roughly half their usual speed unless it is raining. However, with a Sniffer nearby they only grow roughly 20% slower than vanilla.
- Carrots and Potatoes will now only drop two of themselves when fully grown
- Wheat and Beetroot will now only drop one of themselves and two of their respective seeds when they are fully grown
- Bonemealing Wheat, Beetroot, Carrots and Potatoes no longer instantly grows them. However, if they are bonemealed when initially placed, they will always yield one extra drop and seed when harvested at full growth.
- Fortune hoes are now the only tool that can cause crops to drop more of itself now.
- Added Suspicious Stew recipe for Pitcher Plant -> Grants Luck I for 5 minutes
- Animals will have a much longer breeding cooldown after being bred
- Animals will no longer breed if the area around them is too crowded
- Honey Bottles now cure the Wither effect in addition to the Poison effect
- Every compostable item will now always increase the compost's level. The amount of compost generated is dependent on the item.
- Right clicking a filled water cauldron with a fire/campfire underneath it with soup/stew ingredients will place the ingredients within the cauldron
- If you place all the necessary ingredients for a soup within that cauldron, it will play a distinct sound, indicating that the soup has been cooked (Rabbit Stew requires both mushrooms instead of only one)
- Right clicking a cauldron full of soup with a bowl will grant you that soup in its bowl form, allowing it to be eaten. This can be done up to three times.

**Ores and Mining:**
- Natural ore generation for iron, gold, diamonds and ancient debris have been reduced
- Large Coal deposits can now generate slightly under the surface in Badlands/Mesa biomes
- Large Iron deposits can now generate slightly under the ocean floor in cold oceans
- Large Copper deposits can now generate slightly under the surface in very mountainous biomes
- The Drill Block can now be crafted using 7 Copper Blocks, 1 Redstone Dust and 1 Diamond Pickaxe. When placed and fed coal as fuel, it starts digging a 3x3 tunnel in the direction it is facing. However, it can not break certain blocks that are too 'hard' (Example: Obsidian, Bedrock, etc.).

**Villagers:**
- Villagers will no longer grant huge discounts to players that have cured them. They can still grant small discounts from curing that will eventually spread to other villagers that they talk to. (This prevents trades that are far too cheap and also encourages the completion of raids)
- Wandering Traders will now always sell either a Stopwatch (When held shows your current speed in km/h) or a Metal Detector (Detects any ores under you when held)

**Minecarts:**
- Minecarts can now be linked together using by Sneak + Right Clicking on them with chains. The first minecart clicked will pull the second one that was clicked.
- If a minecart travels over a powered rail with a copper block under it, its max speed increases by 3x. If it travels over a normal powered rail, its max speed goes back to normal. Minecarts tend to derail at turns or ramps if they are going too fast so make sure they are slowed down when approaching one.
- If a minecart travels over an unpowered rail with chiseled deepslate under it, it will stop much faster than usual. Furnace Minecarts also stop burning as well.
- Furnace minecarts can now travel much faster. They also automatically load chunks, so use a few of them to allow minecart trains to go through unloaded chunks.
- 4 Regular Rails can now be crafted with 7 Iron Nuggets and 1 Stick. This is a much more efficient craft compared to the vanilla craft.

**Gear and Loot:**
- Suspicious sand can now generate on the desert biome's surface. Upon brushing, it can reveal either junk or rarely treasure such as diamonds or emeralds
- Large patches of suspicious sand can now generate on the desert biome's surface that when brushed reveal bones. These patches reveal that somewhere under them lies a fossil.
- Suspicious sand and gravel can now generate in rivers which can reveal gold upon being brushed
- The mending enchantment has been changed. Upon picking up an experience orb, gear with mending will no longer automatically repair itself. Rather, it will reduce its repair cost at an anvil.
- Elder Guardians now have a 1/3 chance of dropping a Mending Book
- Netherite gear can now be repaired at an anvil with diamonds
- A Turtle Helmet can now be upgraded as a netherite upgrade into a Strider Helmet. The Strider Helmet grants 20 seconds of extra water breathing and 5 seconds of extra fire resistance.

**Mobs:**
- After a player mines Ancient Debris for the first time, piglins and piglin brutes will begin to naturally spawn in the overworld around them
- Phantoms will no longer naturally drop phantom membranes
- After a player enters the end, phantoms that naturally spawn around them will be slightly bigger and can drop phantom membranes (to repair elytra with)
- After a player enters the end, the naturally spawning piglins around them will always spawn with a piece of netherite armor (that can't be dropped)
- The Ender Dragon now takes 80% less damage from block explosions
- Once reaching 0 health, the Ender Dragon will reach its second phase, creating a massive lightning storm and healing to full. 
- The Ender Dragon gains three new 'attacks' during its second phase that it can use periodically:
  - It will channel lightning on itself and strike the tallest block at each player, dealing heavy damage if it does manage to hit the player
  - It can summon a fireball that moves rapidly towards the player and explodes (Similar to Ghast but with heavier damage)
  - It can cloak every player in darkness and call on a few Endermen to start targeting each player


**Horses:**
- Maximum horse stats increased, especially speed (Up to 21.5 blocks/s)
- Horse breeding and stats now work on a completely new system. Every naturally spawned horse has two randomly chosen alleles for each gene (health, speed and jumpheight). Its actual speed/health/jump height is the average value between its two alleles.
- When two horses breed, the foul inherits two of the four alleles from its parents with a slight chance of positive or negative mutation.
- Added Saddle 'n' Horseshoe which halves the fall damage a horse takes when worn. Can be crafted by combining a saddle and iron block at a smithing table.

**Seasons:**
- Added Seasons to the game. Every world starts in Spring, with each season lasting 30 in game days.
- Crops grow the fastest in Spring, and the slowest in Winter
- Grass and Foliage colour changes in the autumn (Upon reconnect)
- Weather now starts at the start of the day and lasts until the next morning.
- Rain and Storms are more common in the Summer but can't occur during Winter
- Snowy days and Blizzards can happen in Winter in which snow starts to fall in biomes where it usually doesn't
- Blizzards allow snow to pile up substantially and become powder snow
- Blizzards cause players out in the open to freeze and take damage. This effect is reduced by wearing leather armor.
- This snow will gradually melt in Spring
- Windy days can occur in the Spring and Autumn where petals/leaves fall from the sky (Purely visual)
- A Weather Radio can be bought from an Expert Cartographer Villager which will display the Date, Season and tomorrow's weather forecast

**Miscellaneous:**
- When a lightning rod above a copper block is struck by lightning, the energy will pass through up to 32 copper blocks in any direction. It will not pass through weathered copper but it can pass through waxed copper blocks
- Any furnaces, blast furnaces or smokers adjacent to an energized copper block will be powered for 60 seconds and will smelt items 2x as fast as usual for the duration
- Up to 9 furnaces/blast furnaces/smokers can be powered by a single lightning strike
