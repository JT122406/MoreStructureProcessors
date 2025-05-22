package tech.jt_dev.moreprocessors.processor.processors;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.jt_dev.moreprocessors.processor.ProcessorRegister;
import tech.jt_dev.moreprocessors.processor.processors.rules.StateLessProcessorRule;

import java.util.List;

/**
 * Processor that sets the age of a crop block to a random value
 * @see StructureProcessor
 * @author Joseph T. McQuigg
 */
public class RandomCropAgeRuleProcessor extends StructureProcessor {

    public static final MapCodec<RandomCropAgeRuleProcessor> CODEC = StateLessProcessorRule.CODEC
            .listOf()
            .fieldOf("rules")
            .xmap(RandomCropAgeRuleProcessor::new, arg -> arg.rules);

    private final ImmutableList<StateLessProcessorRule> rules;

    public RandomCropAgeRuleProcessor(List<? extends StateLessProcessorRule> rules) {
        this.rules = ImmutableList.copyOf(rules);
    }

    @Override
    public @Nullable StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader level, @NotNull BlockPos offset, @NotNull BlockPos pos, StructureTemplate.@NotNull StructureBlockInfo blockInfo, StructureTemplate.@NotNull StructureBlockInfo relativeBlockInfo, @NotNull StructurePlaceSettings settings) {
        BlockPos relPos = relativeBlockInfo.pos();
        RandomSource randomSource = RandomSource.create(Mth.getSeed(relPos));
        BlockState blockState = level.getBlockState(relPos);

        for (StateLessProcessorRule processorRule : rules)
            if (processorRule.test(relativeBlockInfo.state(), blockState, blockInfo.pos(), relativeBlockInfo.pos(), pos, randomSource) && processorRule.getOutputBlock() instanceof CropBlock crop)
                return new StructureTemplate.StructureBlockInfo(relPos, crop.getStateForAge(settings.getRandom(relPos).nextInt(crop.getMaxAge())), relativeBlockInfo.nbt());

        return relativeBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return ProcessorRegister.RANDOM_CROP_AGE_PROCESSOR.get();
    }
}
