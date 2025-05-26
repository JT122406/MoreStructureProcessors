package tech.jt_dev.moreprocessors.processor.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import tech.jt_dev.moreprocessors.processor.ProcessorRegister;

import java.util.List;

/**
 * Processor that randomly adds a block with an age property to the structure when
 * the ground block is present with an air block above it.
 * Chance is met
 * This processor selects a random crop block from a weighted list
 * and sets its age to a random value based on the age property of the block.
 * Then replaces the air block above the ground block with the crop block.
 *
 * @see StructureProcessor
 */
public class RandomCropRandomAgeProcessor extends StructureProcessor {

    public static final MapCodec<RandomCropRandomAgeProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SimpleWeightedRandomList.wrappedCodec(BuiltInRegistries.BLOCK.byNameCodec()).fieldOf("crops").forGetter((block) -> block.crops),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("ground").forGetter(block -> block.ground),
            Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter(block -> block.chance)
    ).apply(instance, RandomCropRandomAgeProcessor::new));

    private final SimpleWeightedRandomList<Block> crops;
    private final Block ground;
    private final float chance;

    public RandomCropRandomAgeProcessor(SimpleWeightedRandomList<Block> crops, Block ground, float chance) {
        this.crops = crops;
        this.ground = ground;
        this.chance = chance;
    }

    public RandomCropRandomAgeProcessor(SimpleWeightedRandomList<Block> crops, Block ground) {
        this(crops, ground, 1);
    }

    @Override
    public @NotNull List<StructureTemplate.StructureBlockInfo> finalizeProcessing(@NotNull ServerLevelAccessor serverLevel, @NotNull BlockPos offset, @NotNull BlockPos pos, @NotNull List<StructureTemplate.StructureBlockInfo> originalBlockInfos, @NotNull List<StructureTemplate.StructureBlockInfo> processedBlockInfos, @NotNull StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> newInfo = new java.util.ArrayList<>(List.copyOf(processedBlockInfos));

        processedBlockInfos.stream().filter(structureBlockInfo -> structureBlockInfo.state().is(ground)).forEach(below -> {
            BlockPos belowPos = below.pos();
            newInfo.stream().filter(structureBlockInfo -> structureBlockInfo.pos().equals(belowPos.above())).findFirst().ifPresent(spot -> {
                if (spot.state().isAir() && serverLevel.getRandom().nextFloat() < chance) {
                    newInfo.remove(spot);
                    Block crop = crops.getRandomValue(serverLevel.getRandom()).get();
                    crop.defaultBlockState().getProperties().stream().filter(property -> property.getName().equals("age")).findFirst().ifPresent(property ->
                            newInfo.add(new StructureTemplate.StructureBlockInfo(spot.pos(), crop.defaultBlockState().setValue((IntegerProperty) property, settings.getRandom(belowPos).nextInt(((IntegerProperty) property).getPossibleValues().size())), spot.nbt())));
                }
            });
        });

        return newInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return ProcessorRegister.RANDOM_CROP_RANDOM_AGE_PROCESSOR.get();
    }
}
