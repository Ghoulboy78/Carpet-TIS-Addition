package carpettisaddition.mixins.logger.damage;

import carpettisaddition.logging.loggers.damage.DamageLogger;
import carpettisaddition.logging.loggers.damage.interfaces.ILivingEntity;
import carpettisaddition.logging.loggers.damage.modifyreasons.ModifyReason;
import carpettisaddition.logging.loggers.damage.modifyreasons.StatusEffectModifyReason;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

// some same mixins
public abstract class LivingEntityAndPlayerEntityMixins
{
	@Mixin({LivingEntity.class, PlayerEntity.class})
	public static class ApplyDamageMixin
	{
		@Inject(
				method = "applyDamage",
				at = @At(
						value = "INVOKE_ASSIGN",
						target = "Ljava/lang/Math;max(FF)F"
				)
		)
		void onAbsorptionReducedDamage(DamageSource source, float amount, CallbackInfo ci)
		{
			((ILivingEntity) this).getDamageLogger().ifPresent(damageLogger -> damageLogger.modifyDamage(
					amount, new StatusEffectModifyReason(StatusEffects.ABSORPTION)
			));
		}

		// at the end of damage calculation
		@Inject(method = "applyDamage", at = @At("RETURN"))
		void onDamageApplyDone(DamageSource source, float amount, CallbackInfo ci)
		{
			if (DamageLogger.isLoggerActivated())
			{
				LivingEntity entity = (LivingEntity) (Object) this;
				Optional<DamageLogger> logger = ((ILivingEntity) this).getDamageLogger();
				if (entity.isInvulnerableTo(source))
				{
					amount = 0.0F;
					logger.ifPresent(damageLogger -> damageLogger.modifyDamage(0.0F, ModifyReason.IMMUNE));
				}
				float finalAmount = amount;
				logger.ifPresent(damageLogger -> damageLogger.flush(finalAmount, entity.getHealth()));
			}
		}
	}

	@Mixin({LivingEntity.class, PlayerEntity.class, ServerPlayerEntity.class})
	public static class DamageMixin
	{
		@Inject(method = "damage", at = @At(value = "RETURN"))
		void onDamageEnded(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
		{
			if (DamageLogger.isLoggerActivated())
			{
				LivingEntity entity = (LivingEntity) (Object) this;
				if (!cir.getReturnValue())
				{
					// return false means actually received no damage for some reason,
					// logger.flush after regular damage calculation might not be called so here's a backup call
					((ILivingEntity) this).getDamageLogger().ifPresent(damageLogger -> damageLogger.flush(0.0F, entity.getHealth()));
				}
				((ILivingEntity) this).setDamageLogger(null);
			}
		}
	}
}
