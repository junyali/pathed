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

	}
}
