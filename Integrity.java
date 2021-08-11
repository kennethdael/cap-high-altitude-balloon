package us.wicap.kenneth_robert_dael.high_altitude_balloon;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface Integrity
{

	int fileCount = 16 * 1024;
	int size = 1024 * 1024, words = size / Integer.BYTES;
	int deadbeef = 0xDEAD_BEEF;

	static void main(String[] args)
	{
		if (args.length != 2)
		{
			help();
			return;
		}
		String path = args[1];
		File dir = new File(path);
		if (!dir.isDirectory())
		{
			System.err.println("The path must be a valid directory");
			return;
		}
		switch (args[0])
		{
		case "make":
			make(dir);
			break;
		case "check":
			check(dir);
			break;
		default:
			help();
			break;
		}
	}

	static void help()
	{
		System.err.println("Only two arguments, the mode and the path");
		System.err.println("Example: make /path/to/device");
		System.err.println("Example: check D:/");
	}

	static void make(File dir)
	{
		byte[] data;
		{
			ByteArrayOutputStream back = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(back);
			for (int wordNumber = 0; wordNumber < words; wordNumber++)
			try
			{
				out.writeInt(deadbeef);
			}
			catch (IOException ioe)
			{
				System.err.println("Failed to write to a byte array...");
				return;
			}
			data = back.toByteArray();
		}
		new Util.Periodic(fileCount, Util.printer("  Writing file ")).stream().forEach(fileNumber ->
		{
			File file = new File(dir, Util.toHexStringPadded(fileNumber, 8));
			try
			(
				OutputStream out = new FileOutputStream(file);
			)
			{
				out.write(data);
			}
			catch (IOException io)
			{
				System.err.println("Error writing file " + fileNumber + "(" + file.getAbsolutePath() + ")");
				io.printStackTrace();
			}
		});
		System.out.println();
	}

	static void check(File dir)
	{
		AtomicInteger allBits = new AtomicInteger();
		new Util.Periodic(fileCount, Util.printer("  Checking file ")).stream().forEach(fileNumber ->
		{
			File file = new File(dir, Util.toHexStringPadded(fileNumber, 8));
			int fileBits = 0;
			try
			(
				DataInputStream in = new DataInputStream(new FileInputStream(file));
			)
			{
				for (int wordNumber = 0; wordNumber < words; wordNumber++)
				{
					int integer = in.readInt();
					if (integer != deadbeef)
					{
						int bits = Util.countSetBits(integer);
						System.err.println("File " + fileNumber + "(" + file.getAbsolutePath() + "): word offset " + wordNumber + ": " + bits + "bits flipped: " + Util.toHexStringPadded(integer, 8));
						fileBits += bits;
					}
				}
				System.out.println(fileBits + " bits flipped in file " + fileNumber + "(" + file.getAbsolutePath() + ")");
			}
			catch (IOException io)
			{
				System.err.println("Error reading file " + fileNumber + "(" + file.getAbsolutePath() + ")");
				io.printStackTrace();
			}
			allBits.getAndAdd(fileBits);
		});
		System.out.println(allBits.get() + " bits flipped");
	}

}
