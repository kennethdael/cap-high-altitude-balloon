package us.wicap.kenneth_robert_dael.high_altitude_balloon;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.*;

public interface Util
{

	static String toHexStringPadded(int number, int length)
	{
		String r = Integer.toHexString(number);
		while (r.length() < length)
		{
			r = "0" + r;
		}
		return r;
	}

	static int countSetBits(int integer)
	{
		int count = 0;
		while (integer > 0)
		{
			count += integer & 1;
			integer >>= 1;
		}
		return count;
	}

	static BiFunction<Integer, Integer, String> printer(String prefix)
	{
		return (index, max) -> String.format(prefix+"%s 0f %s (%.2f%%)", index, max, 100.0D * (double) index / max);
	}

	class Periodic
	{
		public final int items;
		public final AtomicInteger count = new AtomicInteger(0);
		final BiFunction<Integer, Integer, String> print;
		public Periodic(int items, BiFunction<Integer, Integer, String> print)
		{
			this.items = items;
			this.print = print;
			Thread thread = new Thread(this::run);
			thread.setDaemon(true);
			thread.start();
		}
		private void run()
		{
			int countGotten = 0;
			do
			{
				try
				{
					int countGottenNow;
					do
					{
						Thread.sleep(1000);
						countGottenNow = count.get();
					}
					while (countGottenNow == countGotten);
					countGotten = countGottenNow;
				}
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
					break;
				}
				System.out.println(print.apply(countGotten, items));
			}
			while (countGotten < items);
		}
		
		public int get()
		{
			return count.get();
		}
		public IntStream stream()
		{
			return StreamSupport.intStream(new Spliterator.OfInt() {
				final AtomicInteger splits = new AtomicInteger(1);
				@Override
				public long estimateSize() {
					return (items - count.get()) / splits.get();
				}
				@Override
				public int characteristics() {
					return Spliterator.ORDERED | Spliterator.SORTED | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL;
				}
				@Override
				public OfInt trySplit() {
					splits.getAndIncrement();
					return this;
				}
				@Override
				public boolean tryAdvance(IntConsumer action) {
					int gotten = count.getAndIncrement();
					if (gotten >= items)
						return false;
					action.accept(gotten);
					return true;
				}
				@Override
				public Comparator<Integer> getComparator()
				{
					return null;
				}
			}, true);
		}
	}

}
