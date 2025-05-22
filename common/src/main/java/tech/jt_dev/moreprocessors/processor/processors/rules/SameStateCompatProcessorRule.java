package tech.jt_dev.moreprocessors.processor.processors.rules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import javax.annotation.Nullable;

public class SameStateCompatProcessorRule {

    public static final Passthrough DEFAULT_BLOCK_ENTITY_MODIFIER = Passthrough.INSTANCE;
    public static final Codec<SameStateCompatProcessorRule> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            RuleTest.CODEC.fieldOf("input_predicate").forGetter(arg -> arg.inputPredicate),
                            RuleTest.CODEC.fieldOf("location_predicate").forGetter(arg -> arg.locPredicate),
                            PosRuleTest.CODEC.optionalFieldOf("position_predicate", PosAlwaysTrueTest.INSTANCE).forGetter(arg -> arg.posPredicate),
                            ResourceLocation.CODEC.fieldOf("output_location").forGetter(arg -> arg.outputLocation),
                            RuleBlockEntityModifier.CODEC.optionalFieldOf("block_entity_modifier", DEFAULT_BLOCK_ENTITY_MODIFIER).forGetter(arg -> arg.blockEntityModifier)
                    )
                    .apply(instance, SameStateCompatProcessorRule::new)
    );

    private final RuleTest inputPredicate;
    private final RuleTest locPredicate;
    private final PosRuleTest posPredicate;
    private final ResourceLocation outputLocation;
    private final RuleBlockEntityModifier blockEntityModifier;

    public SameStateCompatProcessorRule(RuleTest inputPredicate, RuleTest locPredicate, PosRuleTest posPredicate, ResourceLocation outputLocation, RuleBlockEntityModifier blockEntityModifier) {
        this.inputPredicate = inputPredicate;
        this.locPredicate = locPredicate;
        this.posPredicate = posPredicate;
        this.outputLocation = outputLocation;
        this.blockEntityModifier = blockEntityModifier;
    }

    public SameStateCompatProcessorRule(RuleTest inputPredicate, RuleTest locPredicate, ResourceLocation outputLocation) {
        this(inputPredicate, locPredicate, PosAlwaysTrueTest.INSTANCE, outputLocation, DEFAULT_BLOCK_ENTITY_MODIFIER);
    }

    public SameStateCompatProcessorRule(RuleTest inputPredicate, ResourceLocation outputLocation) {
        this(inputPredicate, AlwaysTrueTest.INSTANCE, PosAlwaysTrueTest.INSTANCE, outputLocation, DEFAULT_BLOCK_ENTITY_MODIFIER);
    }

    public SameStateCompatProcessorRule(RuleTest inputPredicate, RuleTest locPredicate, Block outputBlock) {
        this(inputPredicate, locPredicate, BuiltInRegistries.BLOCK.getKey(outputBlock));
    }

    public SameStateCompatProcessorRule(RuleTest inputPredicate, Block outputBlock) {
        this(inputPredicate, BuiltInRegistries.BLOCK.getKey(outputBlock));
    }

    public boolean test(BlockState inputState, BlockState existingState, BlockPos localPos, BlockPos relativePos, BlockPos structurePos, RandomSource random) {
        return BuiltInRegistries.BLOCK.containsKey(outputLocation)
                && this.inputPredicate.test(inputState, random)
                && this.locPredicate.test(existingState, random)
                && this.posPredicate.test(localPos, relativePos, structurePos, random);
    }

    public Block getOutputBlock() {
        return BuiltInRegistries.BLOCK.get(outputLocation).orElseThrow().value();
    }


    @Nullable
    public CompoundTag getOutputTag(RandomSource random, @Nullable CompoundTag tag) {
        return this.blockEntityModifier.apply(random, tag);
    }
}
