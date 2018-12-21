package sisaku;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main{
	public static void main(String args[]) throws IOException{

		double simTime = 1;//シミュレーション間隔 0.1
		double endTime = 400;//シミュレーション時間(招集しないなら406秒
		double lapseTime = 1;//経過時間

		int peaple = 10;

		int falsePeaple = peaple * 2;


		//long sleepTime = (long) (simTime * 1000);
		Random rnd = new Random();
		int[]iValue = new int[peaple];
		int[]jValue = new int[peaple];

		int [][] divisionField = new int[36][36];//データの格納
		for(int i = 0; i < 36; i++) {
			for(int j = 0; j < 36; j++) {
				divisionField[i][j] = 0;
			}
		}

		for(int k = 0; k < peaple; k ++) {
	    	  iValue[k] = rnd.nextInt(36);
	    	  jValue[k] = rnd.nextInt(36);
	    	  boolean flag = false;
	    	  divisionField[iValue[k]][jValue[k]] = 1;

	    	  do {
	              flag = false;
	              for (int j = k - 1; j >= 0; j--) {
	                  if (iValue[k] == iValue[j] && jValue[k] == jValue[j]) {
	                      flag = true;
	                      iValue[k] = rnd.nextInt(36);
	                	  jValue[k] = rnd.nextInt(36);
	                	  divisionField[iValue[k]][jValue[k]] = 1;
	                  }
	              }

	          } while (flag == true);

	    	  divisionField[iValue[k]][jValue[k]] = 1;
	    }


		int[][] area = new int[9][2];//エリア生成
		int x = 0, y = 0;
		for(int i = 0; i < 9; i++) {
			if(i == 0) {
				x = 10;
				y = 230;

			}
			if(i == 3 || i == 6) {
				x = 10;
				y += 240;

			}
			area[i][0] = x;
			area[i][1] = y;
			x += 240;

		}


		Drone[] drone = new Drone[9];//ドローン9台生成


		for(int i = 0; i < 9; i++) {//ドローンに値を割り当て
			drone[i] = new Drone(i + 50001, area[i][0], area[i][1]);
		}

		EdgeServer edgeServer = new EdgeServer();//インスタンス生成

		while(lapseTime < endTime) {

			for(int i = 0; i < 9; i++) {
				drone[i].move(simTime);
				drone[i].gDataGet(divisionField);

				System.out.println("ドローン"+(i+1)+":状態"+ drone[i].state+" x:"+ drone[i].x+"  y:"+drone[i].y+
						" 方向:"+drone[i].direction + " "+ drone[i].battery+" 招集状態"+drone[i].gatheringState +
						" " );
				falsePeaple -= drone[i].gDiscover;

				edgeServer.receiveData(area[i][0], area[i][1], lapseTime, peaple);



				/*if(drone[i].time.equals("sensingstart")) {
					String str =  String.valueOf(lapseTime) + " " + String.valueOf(drone[i].id);
					try {//ファイルへの書き込み
						FileWriter fw = new FileWriter("/Users/TKLab/Desktop/sennsingStartTime.txt",true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(str);
						bw.newLine();//改行
						bw.flush();
						bw.close();//ファイル閉鎖
					}catch(IOException e) {
						System.out.println("エラー");
					}

				}

				if(drone[i].time.equals("sensingEnd")) {
					String str =  String.valueOf(lapseTime) + " " + String.valueOf(drone[i].id);
					try {//ファイルへの書き込み
						FileWriter fw = new FileWriter("/Users/TKLab/Desktop/sennsingEndTime.txt",true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(str);
						bw.newLine();//改行
						bw.flush();
						bw.close();//ファイル閉鎖
					}catch(IOException e) {
						System.out.println("エラー");
					}

				}

				if(drone[i].time.equals("missionEnd")) {
					String str =  String.valueOf(lapseTime) + " " + String.valueOf(drone[i].id);
					try {//ファイルへの書き込み
						FileWriter fw = new FileWriter("/Users/TKLab/Desktop/missionEndTime.txt",true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(str);
						bw.newLine();//改行
						bw.flush();
						bw.close();//ファイル閉鎖
					}catch(IOException e) {
						System.out.println("エラー");
					}

				}

				if(drone[i].message.equals("end")) {
					String str =  String.valueOf(lapseTime) + " " + String.valueOf(drone[i].id);
					try {//ファイルへの書き込み
						FileWriter fw = new FileWriter("/Users/TKLab/Desktop/conventionSennsingEndTime.txt",true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(str);
						bw.newLine();//改行
						bw.flush();
						bw.close();//ファイル閉鎖
					}catch(IOException e) {
						System.out.println("エラー");
					}

				}*/
				System.out.println("");
			}

			System.out.println(falsePeaple);
			if(falsePeaple == 0) {
				String str =  String.valueOf(lapseTime) + " ";
				falsePeaple = 10000;
				try {//ファイルへの書き込み
					FileWriter fw = new FileWriter("/Users/TKLab/Desktop/detectionPeapleTime.txt",true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(str);
					bw.newLine();//改行
					bw.flush();
					bw.close();//ファイル閉鎖
				}catch(IOException e) {
					System.out.println("エラー");
				}
			}

			lapseTime += simTime;
			System.out.println("経過時間："+lapseTime);

			if(lapseTime == endTime) {
				System.exit(0);
			}

			/*try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}*/
		}


	}
}