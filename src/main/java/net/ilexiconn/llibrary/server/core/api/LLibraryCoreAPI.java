package net.ilexiconn.llibrary.server.core.api;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Handles calls from LLibrary Core to LLibrary
 */
public interface LLibraryCoreAPI {
    @OnlyIn(Dist.CLIENT)
    void addRemoteLocalizations(String language, Map<String, String> properties);

    @OnlyIn(Dist.CLIENT)
    void provideStackContext(@Nonnull ItemStack stack);

    @OnlyIn(Dist.CLIENT)
    void providePerspectiveContext(@Nonnull ItemCameraTransforms.TransformType transform);

    long getTickRate();

    class Fallback implements LLibraryCoreAPI {
        @Override
        public void addRemoteLocalizations(String language, Map<String, String> properties) {
        }

        @Override
        public void provideStackContext(@Nonnull ItemStack stack) {
        }

        @Override
        public void providePerspectiveContext(@Nonnull ItemCameraTransforms.TransformType transform) {
        }

        @Override
        public long getTickRate() {
            return 50;
        }
    }
}
