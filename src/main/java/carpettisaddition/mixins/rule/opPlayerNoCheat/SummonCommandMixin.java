package carpettisaddition.mixins.rule.opPlayerNoCheat;

import carpettisaddition.helpers.CommandPermissionHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Stop pasting schematic in SMP
@Mixin(SummonCommand.class)
public abstract class SummonCommandMixin
{
	@SuppressWarnings("UnresolvedMixinReference")
	@Redirect(
			method = "method_13693",  // lambda method
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/command/ServerCommandSource;hasPermissionLevel(I)Z"
			),
			allow = 1
	)
	private static boolean checkIfAllowCheating(ServerCommandSource source, int level)
	{
		return CommandPermissionHelper.canCheat(source, level);
	}
}
