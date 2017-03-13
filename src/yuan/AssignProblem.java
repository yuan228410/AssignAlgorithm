package yuan;

import java.util.Arrays;

public class AssignProblem {
	/*
	 * 费用矩阵costMatrix,由于要改变costMatrix的值，clone方法只能对基本类型;
	 * pnum即为几个人，也是costMatrix的行数，wnum是几个任务，也是costMatrix的列数
	 * 返回值：没两个数字为一组，表示i,j。如返回[1,1,2,0]表示costMatrix[1][1]、costMatrix[2][0]费用最低
	 */
	public static void main(String[] args) {
		
		int[][] cost=new int[][]{{4,8,7,5,12},{7,9,17,14,10},{6,9,12,8,7},{6,7,14,6,10},{6,9,12,10,6}};
		System.out.println(Arrays.toString(process(cost, 4, 4)));

cost=new int[][]{{12,7,9,7,9},{8,9,6,6,6},{7,17,12,14,12},{15,14,6,6,10},{4,10,7,10,6}};
		System.out.println(Arrays.toString(process(cost, 5, 5)));
		
	}
	public static int[] process(int[][] costMatrix, int pnum, int wnum) {
		if (pnum < 1 || pnum < wnum)
			return null; // test n>=m
		int[][] costC = new int[pnum][]; // clone 一份costMatrix
		for (int i = 0; i < pnum; i++) {
			costC[i] = costMatrix[i].clone();
		}
		// 每行减去最小的元素
		int[] lzero = new int[pnum + 1]; // 记录每行0的个数,lzero[pnum]记录0最少的行标
		lzero[pnum] = -1;
		int i, j;
		for (i = 0; i < pnum; i++) {
			int lmin = costC[i][0]; // 记录每行最小的
			for (j = 1; j < wnum; j++)
				lmin = (lmin > costC[i][j]) ? costC[i][j] : lmin;
			for (j = 0; j < wnum; j++) {
				costC[i][j] -= lmin;
				lzero[i] += (costC[i][j] == 0) ? 1 : 0;
			}
		}
		for (j = 0; j < wnum; j++) {
			int cmin = costC[0][j]; // 记录每列最小的
			for (i = 1; i < pnum; i++)
				cmin = (cmin > costC[i][j]) ? costC[i][j] : cmin;
			if (cmin == 0)
				continue;
			for (i = 0; i < pnum; i++) {
				costC[i][j] -= cmin;
				lzero[i] += (costC[i][j] == 0) ? 1 : 0;
			}
		}
		int[] result;
		int whilenum = 0;
		while (true) {
			boolean[] lzerob = new boolean[pnum]; // 记录某行是否查找过
			result = new int[pnum * 2]; // 记录0元素所在的行列
			Arrays.fill(result, -1);

			if (awpIsSolution(costC, pnum, wnum, lzero.clone(), lzerob, result))
				break;
			// 下面调整矩阵
			int[] coverLC = new int[pnum + wnum]; // 要被标记的行列，0-pnum-1为行，pnum以后为列
			Arrays.fill(coverLC, -1);
			// 没有找到合适0元素的行做标记
			for (i = 0; i < pnum; i++)
				if (lzerob[i] == false)
					coverLC[i] = i;
			// 对已经标记的行上的0元素所在的列做标记
			for (i = 0; i < pnum; i++)
				if (coverLC[i] != -1) {
					for (j = 0; j < wnum; j++) {
						if (costC[coverLC[i]][j] == 0)
							coverLC[pnum + j] = j;
					}
				}
			// 对已经标记的列上的已经选中的0元素所在的行做标记
			for (j = 0; j < wnum; j++) {
				if (coverLC[pnum + j] != -1) {
					for (i = 0; i < result.length && result[i] != -1; i += 2) {
						if (result[i + 1] == j)
							coverLC[result[i]] = result[i];
					}
				}
			}
			// 确定能找出新最小值的区域，直线覆盖掉没有打勾的行，打勾的列,最终coverLC[x]!=-1就是能选择的数
			for (i = 0; i < wnum; i++) {
				if (coverLC[pnum + i] != -1)
					coverLC[pnum + i] = -1;
				else
					coverLC[pnum + i] = i;
			}
			// 从区域中找出最小元素
			int nmin = -1;
			for (i = 0; i < pnum; i++) {
				if (coverLC[i] == -1)
					continue;
				for (j = 0; j < wnum; j++) {
					if (coverLC[pnum + j] == -1)
						continue;
					if (nmin == -1)
						nmin = costC[i][j];
					else
						nmin = nmin > costC[i][j] ? costC[i][j] : nmin;
				}
			}
			// 打勾的列加上nmin,打勾的行减去nmin,记录0个数的数组作相应变化
			for (j = 0; j < wnum; j++) {
				if (coverLC[pnum + j] == -1) {
					for (i = 0; i < pnum; i++) {
						if (costC[i][j] == 0)
							lzero[i] -= 1;
						costC[i][j] += nmin;
					}
				}
			}
			for (i = 0; i < pnum; i++) {
				if (coverLC[i] != -1) {
					for (j = 0; j < wnum; j++) {
						costC[i][j] -= nmin;
						if (costC[i][j] == 0)
							lzero[i] += 1;
					}
				}
			}

			whilenum++;
			if (whilenum == 100) {
				System.out.println("100次之内矩阵调整没有找到");
				return null;
			}

		}
		return result;
	}

	/*
	 * 测试矩阵costC是否有解，已经通过变换或者调整得到的矩阵
	 */
	public static boolean awpIsSolution(int[][] costC, int pnum, int wnum,
			int[] lzero, boolean[] lzerob, int[] result) {
		int i, j, resulti = 0;
		for (int p = 0; p < pnum; p++) { // 开始按照匈牙利法划去0所在的行列
			// 查找0元素个数最少的行
			for (i = 0; i < pnum; i++) {
				if (lzerob[i] || lzero[i] < 1)
					continue; // 如果某行已经查找过或者没有0元素，可能被划去了
				if (lzero[pnum] != -1 && lzero[i] < lzero[lzero[pnum]])
					lzero[pnum] = i;
				else if (lzero[pnum] == -1)
					lzero[pnum] = i;
			}
			// 没有找到足够的不在同一行同一列的0元素，需要对矩阵进行调整，如果lzero[pnum]有值，则说明该行一定能找到
			if (lzero[pnum] == -1) {
				return false;
			}
			// 划去找到的行中没有被覆盖的0元素所在的行列
			for (j = 0; j < wnum; j++) {
				if (costC[lzero[pnum]][j] != 0)
					continue;
				// 第一次找0元素最少的行
				if (resulti == 0) {
					result[resulti++] = lzero[pnum];
					result[resulti++] = j;
					lzerob[lzero[pnum]] = true; // 找到第lzero[pnum]行，第j列0元素
					// 划去所在的行列时 lzero做相应的变化
					for (i = 0; i < pnum; i++) {
						if (i != lzero[pnum] && costC[i][j] == 0)
							lzero[i] -= 1;
					}
					lzero[pnum] = -1;
					break;
				}
				// 找到的0元素是否被划去
				for (i = 0; i < resulti	&& (lzero[pnum] != result[i] && j != result[i + 1]); i += 2)
					;
				// 如果被划去则找该行下一个0元素
				if (i < resulti)
					continue;
				result[resulti++] = lzero[pnum];
				result[resulti++] = j;
				lzerob[lzero[pnum]] = true;
				for (i = 0; i < pnum; i++) {
					if (i != lzero[pnum] && costC[i][j] == 0)
						lzero[i] -= 1;
				}
				lzero[pnum] = -1;
				break;
			}
		}
		return true;
	}
}