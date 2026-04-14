package io.github.junyali.pathed.datagen;

import io.github.junyali.pathed.Pathed;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class PathedLanguageProvider extends LanguageProvider {
	public PathedLanguageProvider(PackOutput packOutput, String locale) {
		super(packOutput, Pathed.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		// Paths
		add("path.pathed.human.name", "Human");
		add("path.pathed.human.description", "Your ordinary Minecraft experience");
		add("path.pathed.human.tool_description", "");

		add("path.pathed.blademaster.name", "Blademaster");
		add("path.pathed.blademaster.description", "Born for question. Your blade is your answer to everything; strike first and ask questions later.");
		add("path.pathed.blademaster.tool_description", "Forged for one hand alone.");

		add("path.pathed.spelunker.name", "Spelunker");
		add("path.pathed.spelunker.description", "A master of the deep. Thrive underground as the surface holds little for you.");
		add("path.pathed.spelunker.tool_description", "It remembers every cave it has touched.");

		add("path.pathed.lumberjack.name", "Lumberjack");
		add("path.pathed.lumberjack.description", "The forest bends to your will. Your axe in hand, no tree stands long.");
		add("path.pathed.lumberjack.tool_description", "The trees knew it was coming.");

		add("path.pathed.excavator.name", "Excavator");
		add("path.pathed.excavator.description", "Reshape the world one scoop at a time, the land is all yours.");
		add("path.pathed.excavator.tool_description", "Every world begins with a hole.");

		add("path.pathed.cultivator.name", "Cultivator");
		add("path.pathed.cultivator.description", "Patient and resourceful. You coax life from the earth itself and replenish for more.");
		add("path.pathed.cultivator.tool_description", "A seedy place.");

		// GUI
		add("pathed.gui.choose_path.title", "Choose your Path");
		add("pathed.gui.choose_path.button.select", "Select");
		add("pathed.gui.choose_path.starting_kit", "Starting Kit");

		add("pathed.gui.path_menu.title", "Your Path");
		add("pathed.gui.path_menu.no_path", "No Path Selected");
		add("pathed.gui.path_menu.level", "Level %s");
		add("pathed.gui.path_menu.button.skill_tree", "Skill Tree");
		add("pathed.gui.path_menu.button.stats", "Stats");

		// KEYMAPPINGS

		add("key.categories.pathed", "Pathed");
		add("key.pathed.open_path_menu", "Open Path Menu");

		// Items
		add("item.pathed.blademaster_tool", "The First Edge");
		add("item.pathed.spelunker_tool", "Deepseeker");
		add("item.pathed.lumberjack_tool", "Grovebreaker");
		add("item.pathed.excavator_tool", "Earth Sunderer");
		add("item.pathed.cultivator_tool", "Rootwarden");
	}
}
