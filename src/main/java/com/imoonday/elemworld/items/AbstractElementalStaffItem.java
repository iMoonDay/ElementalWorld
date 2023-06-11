package com.imoonday.elemworld.items;

import com.google.common.collect.ImmutableMap;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEnchantments;
import com.imoonday.elemworld.init.EWSounds;
import com.imoonday.elemworld.interfaces.BaseElement;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractElementalStaffItem extends Item implements BaseElement {

    private static final Map<Predicate<LivingEntity>, ItemStack[]> LOOTABLES = new HashMap<>();

    public AbstractElementalStaffItem(int maxDamage) {
        this(new FabricItemSettings().maxCount(1).maxDamage(maxDamage));
    }

    public AbstractElementalStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (getUseTicks(stack, remainingUseTicks) < getMinUseTime(stack)) {
            return;
        }
        if (remainingUseTicks != 0 && isContinuousLaunch(stack)) {
            return;
        }
        boolean sneaking = isSneaking(user);
        onValidUsing(stack, world, user, sneaking);
        int amount = getDamageAmount(sneaking);
        if (amount > 0) {
            stack.damage(amount, user, p -> p.sendToolBreakStatus(user.getActiveHand()));
        }
        SoundEvent soundEvent = getSoundEvent(sneaking);
        if (soundEvent != null) {
            world.playSound(null, user.getBlockPos(), soundEvent, SoundCategory.VOICE);
        }
        if (!isContinuousLaunch(stack) && user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, 20);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (isContinuousLaunch(stack)) {
            onStoppedUsing(stack, world, user, 0);
        }
        return super.finishUsing(stack, world, user);
    }

    public boolean isSneaking(LivingEntity user) {
        boolean sneaking = user.isSneaking();
        if (user instanceof PlayerEntity player && player.getAbilities().flying && sneaking) {
            sneaking = false;
        }
        return sneaking;
    }

    protected int getUseTicks(ItemStack stack, int remainingUseTicks) {
        return getMaxUseTime(stack) - remainingUseTicks;
    }

    public static boolean isInstantLaunch(ItemStack stack) {
        return EnchantmentHelper.getLevel(EWEnchantments.INSTANT_LAUNCH, stack) > 0;
    }

    public static boolean isContinuousLaunch(ItemStack stack) {
        return EnchantmentHelper.getLevel(EWEnchantments.CONTINUOUS_LAUNCH, stack) > 0;
    }

    public int getDamageAmount(boolean sneaking) {
        return sneaking ? 2 : 1;
    }

    protected void onValidUsing(ItemStack stack, World world, LivingEntity user, boolean sneaking) {
        if (sneaking) {
            addEffects(stack, world, user);
        } else {
            spawnProjectiles(user, stack);
        }
    }

    protected void spawnProjectiles(LivingEntity user, ItemStack stack) {
        AbstractElementalEnergyBallEntity energyBall = createEnergyBall(user, stack);
        if (energyBall != null && !user.world.isClient) {
            user.world.spawnEntity(energyBall);
        }
    }

    protected abstract AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack);

    protected int getMinUseTime(ItemStack stack) {
        return isInstantLaunch(stack) ? 0 : 10;
    }

    protected abstract void addEffects(ItemStack stack, World world, LivingEntity user);

    @Nullable
    protected SoundEvent getSoundEvent(boolean isSneaking) {
        return isSneaking ? SoundEvents.ENTITY_GENERIC_DRINK : EWSounds.USE_STAFF;
    }

    public Map<Identifier, Float> getLootables(Map<Identifier, Float> lootables) {
        return new HashMap<>();
    }

    protected Map<Predicate<LivingEntity>, ItemStack[]> addLootables(Map<Predicate<LivingEntity>, ItemStack[]> lootables) {
        return new HashMap<>();
    }

    public static ImmutableMap<Predicate<LivingEntity>, ItemStack[]> getLootables() {
        return ImmutableMap.copyOf(LOOTABLES);
    }

    public static <T extends AbstractElementalStaffItem> void registerLootables(T staff) {
        LOOTABLES.putAll(staff.addLootables(new HashMap<>()));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return isContinuousLaunch(stack) ? getMinUseTime(stack) + 1 : 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

}
