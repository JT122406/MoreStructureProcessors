package tech.jt_dev.moreprocessors.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;
import tech.jt_dev.moreprocessors.MoreStructureProcessors;
import tech.jt_dev.moreprocessors.PlatformHandler;
import tech.jt_dev.moreprocessors.processor.processors.*;

import java.util.function.Supplier;

/**
 * Class for registering all custom structure processors.
 * @see StructureProcessorType
 * @author Joseph T. McQuigg
 */
public class ProcessorRegister {

    public static final Supplier<StructureProcessorType<PlaceOnTopProcessor>> PLACE_ON_TOP_PROCESSOR = register("place_on_top", () -> PlaceOnTopProcessor.CODEC);
    public static final Supplier<StructureProcessorType<PlaceBelowProcessor>> PLACE_BELOW_PROCESSOR = register("place_below", () -> PlaceBelowProcessor.CODEC);
    public static final Supplier<StructureProcessorType<RandomCropAgeRuleProcessor>> RANDOM_CROP_AGE_PROCESSOR = register("random_crop_age", () -> RandomCropAgeRuleProcessor.CODEC);
    public static final Supplier<StructureProcessorType<RandomCropRandomAgeProcessor>> RANDOM_CROP_RANDOM_AGE_PROCESSOR = register("random_crop_random_age", () -> RandomCropRandomAgeProcessor.CODEC);
    public static final Supplier<StructureProcessorType<CompatRuleProcessor>> COMPAT_RULE = register("compat_rule", () -> CompatRuleProcessor.CODEC);
    public static final Supplier<StructureProcessorType<SameStateCompatRuleProcessor>> SAME_STATE_COMPAT_RULE = register("same_state_compat_rule", () -> SameStateCompatRuleProcessor.CODEC);
    public static final Supplier<StructureProcessorType<SameStateRuleProcessor>> SAME_STATE_RULE = register("same_state_rule", () -> SameStateRuleProcessor.CODEC);
    public static final Supplier<StructureProcessorType<FlowingFluidRuleProcessor>> FLOWING_FLUID_RULE = register("flowing_fluid_rule", () -> FlowingFluidRuleProcessor.CODEC);
    public static final Supplier<StructureProcessorType<BiomeRuleProcessor>> BIOME_RULE = register("biome_rule", () -> BiomeRuleProcessor.CODEC);
    public static final Supplier<StructureProcessorType<BiomeTagBasedProcessor>> BIOME_TAG_BASED_PROCESSOR = register("biome_tag_based", () -> BiomeTagBasedProcessor.CODEC);
    public static final Supplier<StructureProcessorType<DirectionalRuleProcessor>> DIRECTIONAL_RULE_PROCESSOR = register("directional_rule", () -> DirectionalRuleProcessor.CODEC);

    /**
     * Utility method for registering custom structure processor types.
     * @see PlatformHandler
     */
    private static <P extends StructureProcessor> Supplier<StructureProcessorType<P>> register(String id, @NotNull Supplier<MapCodec<P>> codec) {
        return PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.STRUCTURE_PROCESSOR, id, () -> codec::get);
    }


    public static void registerProcessors() {
        MoreStructureProcessors.LOGGER.info("Registering Structure Processors");
    }
}
