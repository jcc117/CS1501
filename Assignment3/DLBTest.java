public class DLBTest
{
	public static void main(String[] args)
	{
		String one = "abcdefg";
		String two = "hijklmnop";
		String three = "aqrtstv";
		String four = "atuvwxyz";
		DLBMod dlb = new DLBMod();
		dlb.add(one,1);
		dlb.add(two,2);
		dlb.add(three, 3);
		dlb.add(four, 4);
		StringBuilder one1 = new StringBuilder(one);
		StringBuilder two2 = new StringBuilder(two);
		StringBuilder three3 = new StringBuilder(three);
		StringBuilder four4 = new StringBuilder(four);
		System.out.println(dlb.searchPrefix(one1));
		System.out.println(dlb.searchPrefix(two2));
		System.out.println(dlb.searchPrefix(three3));
		System.out.println(dlb.searchPrefix(four4));
	}
}