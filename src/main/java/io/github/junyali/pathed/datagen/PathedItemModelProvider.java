package io.github.junyali.pathed.datagen;

import io.github.junyali.pathed.Pathed;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class PathedItemModelProvider extends ItemModelProvider {
	public PathedItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
		super(packOutput, Pathed.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {

	}
}
