package com.imoonday.elemworld.items;

import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEnchantments;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractElementalStaffItem extends Item implements BaseElement {

    public AbstractElementalStaffItem(int maxDamage) {
        this(new FabricItemSettings().maxCount(1).maxDamage(maxDamage));
    }

    public AbstractElementalStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int useTicks = getMaxUseTime(stack) - remainingUseTicks;
        boolean instantLaunch = EnchantmentHelper.getLevel(EWEnchantments.INSTANT_LAUNCH, stack) > 0;
        if (useTicks < getMinUseTime() && !instantLaunch) {
            return;
        }
        boolean sneaking = user.isSneaking();
        if (sneaking) {
            addEffects(stack, world, user);
        } else {
            spawnProjectiles(user, stack);
        }
        stack.damage(sneaking ? 2 : 1, user, p -> p.sendToolBreakStatus(user.getActiveHand()));
        SoundEvent soundEvent = getSoundEvent(sneaking);
        if (soundEvent != null) {
            world.playSound(null, user.getBlockPos(), soundEvent, SoundCategory.VOICE);
        }
    }

    public void spawnProjectiles(LivingEntity user, ItemStack stack) {
        AbstractElementalEnergyBallEntity energyBall = createEnergyBall(user, stack);
        if (energyBall != null) {
            user.world.spawnEntity(energyBall);
        }
    }

    public abstract AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack);

    protected int getMinUseTime() {
        return 10;
    }

    protected abstract void addEffects(ItemStack stack, World world, LivingEntity user);

    @Nullable
    protected abstract SoundEvent getSoundEvent(boolean isSneaking);

    public abstract Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables);

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    protected void forEachLivingEntity(World world, LivingEntity user, double range, Consumer<LivingEntity> livingEntityConsumer, Runnable elseToDo) {
        if (!world.isClient) {
            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(range), LivingEntity::isAlive);
            if (entities.isEmpty()) {
                elseToDo.run();
            } else {
                for (LivingEntity entity : entities) {
                    livingEntityConsumer.accept(entity);
                    getBaseElement().addEffect(entity, user);
                }
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

}
