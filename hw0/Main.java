public class Main {
	public static void main(String[] args) {
		int[] a = new int[]{1, 2, 3, 4};
		int[] b = new int[]{-1,-2,-3, 4, 5, 6};
		System.out.println(max(a));
		System.out.println(threeSum(b));
		System.out.println(threeSumDistinct(b));
	}

	public static int max(int[] a) {
		int max = 0;
		for (int i = 0; i < a.length; i++){
			if (a[i] > max){
				max = a[i];
			}


		}
		return max;
	}

	public static boolean threeSum(int[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int c = 0; c < a.length; c++) {
				for (int b = 0; b < a.length; b++){
					int sum = a[i];
					int sum1 = a[c] + a[b];
					if (sum == -sum1){
						return true;
					}
				}

			}

		}
		return false;
	}
	public static boolean threeSumDistinct(int[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int c = 1; c < a.length; c++) {
				for (int b = 2; b < a.length; b++){
					int sum = a[i];
					int sum1 = a[c] + a[b];
					if (sum == -sum1){
						return true;
					}
				}

			}

		}
		return false;
	}
}