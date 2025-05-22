package tech.jt_dev.moreprocessors.processor.processors;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.jt_dev.moreprocessors.processor.ProcessorRegister;
import tech.jt_dev.moreprocessors.processor.processors.rules.StateLessProcessorRule;

import java.util.List;

public class DirectionalRuleProcessor extends StructureProcessor {

    public static final MapCodec<DirectionalRuleProcessor> CODEC = StateLessProcessorRule.CODEC
            .listOf()
            .fieldOf("rules")
            .xmap(DirectionalRuleProcessor::new, arg -> arg.rules);

    private final ImmutableList<StateLessProcessorRule> rules;

    public DirectionalRuleProcessor(List<? extends StateLessProcessorRule> rules) {
        this.rules = ImmutableList.copyOf(rules);
    }

    @Override
    public @Nullable StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader level, @NotNull BlockPos offset, @NotNull BlockPos pos, StructureTemplate.@NotNull StructureBlockInfo blockInfo, StructureTemplate.@NotNull StructureBlockInfo relativeBlockInfo, @NotNull StructurePlaceSettings settings) {
        RandomSource randomSource = RandomSource.create(Mth.getSeed(relativeBlockInfo.pos()));
        BlockState blockState = level.getBlockState(relativeBlockInfo.pos());

        for (StateLessProcessorRule processorRule : this.rules)
            if (processorRule.test(relativeBlockInfo.state(), blockState, blockInfo.pos(), relativeBlockInfo.pos(), pos, randomSource) && processorRule.getOutputBlock().defaultBlockState().hasProperty(BlockStateProperties.FACING))
                return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), processorRule.getOutputBlock().defaultBlockState().setValue(BlockStateProperties.FACING, blockState.getValue(BlockStateProperties.FACING)), processorRule.getOutputTag(randomSource, relativeBlockInfo.nbt()));

        return relativeBlockInfo;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return ProcessorRegister.DIRECTIONAL_RULE_PROCESSOR.get();
    }
}
