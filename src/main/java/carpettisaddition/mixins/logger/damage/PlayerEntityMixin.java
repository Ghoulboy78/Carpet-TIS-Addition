package carpettisaddition.mixins.logger.damage;

import carpettisaddition.interfaces.ILivingEntity_damageLogger;
import carpettisaddition.logging.loggers.damage.DamageLogger;
import carpettisaddition.logging.loggers.damage.modifyreasons.ModifyReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world)
	{
		super(type, world);
	}

	// at the start of player damage calculation
	@Inject(
			method = "damage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"
			)
	)
	void onDamageStarted(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		DamageLogger.create(this, source, amount);
	}

	@Inject(
			method = "damage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
			)
	)
	void onDifficultyModifiedDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		((ILivingEntity_damageLogger)this).getDamageLogger().ifPresent(damageLogger -> {
			damageLogger.modifyDamage(amount, ModifyReason.DIFFICULTY);
		});
	}
}
