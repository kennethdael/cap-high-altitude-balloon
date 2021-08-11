package us.wicap.kenneth_robert_dael.high_altitude_balloon;

import java.util.Scanner;

public interface Interactive
{

	static void main(String[] args)
	{
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		while (true)
		{
			System.out.print("Enter mode (make or check) or exit to quit:");
			String op = in.nextLine();
			switch (op)
			{
			case "exit":
				System.out.println("Quitting");
				return;
			default:
				System.out.print("Enter path:");
				String path = in.nextLine();
				Integrity.main(new String[] { op, path, });
			}
		}
	}

}
