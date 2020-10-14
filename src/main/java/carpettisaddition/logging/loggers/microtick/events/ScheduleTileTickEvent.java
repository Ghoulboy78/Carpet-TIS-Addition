package carpettisaddition.logging.loggers.microtick.events;

import carpet.utils.Messenger;
import carpettisaddition.logging.loggers.microtick.types.EventType;
import carpettisaddition.logging.loggers.microtick.utils.MicroTickUtil;
import net.minecraft.block.Block;
import net.minecraft.text.BaseText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

import java.util.Objects;

public class ScheduleTileTickEvent extends BaseEvent
{
	private final Block block;
	private final BlockPos pos;
	private final int delay;
	private final TickPriority priority;

	public ScheduleTileTickEvent(Block block, BlockPos pos, int delay, TickPriority priority)
	{
		super(EventType.EVENT, "schedule_tile_tick");
		this.block = block;
		this.pos = pos;
		this.delay = delay;
		this.priority = priority;
	}

	@Override
	public BaseText toText()
	{
		return Messenger.c(
				MicroTickUtil.getTranslatedName(block),
				"q  Scheduled",
				"c  TileTick",
				String.format("^w Delay: %dgt\nPriority: %d (%s)", delay, priority.getIndex(), priority)
		);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof ScheduleTileTickEvent)) return false;
		if (!super.equals(o)) return false;
		ScheduleTileTickEvent that = (ScheduleTileTickEvent) o;
		return delay == that.delay &&
				Objects.equals(block, that.block) &&
				Objects.equals(pos, that.pos) &&
				priority == that.priority;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), block, pos, delay, priority);
	}
}