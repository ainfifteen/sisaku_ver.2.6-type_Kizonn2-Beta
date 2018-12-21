package sisaku;
import java.io.IOException;
import java.util.ArrayList;

public class EdgeServer {
	final static int ID = 60000;;//エッジサーバのID
	double lapseTime;//経過時間
	String[] eachData;
	double[][] droneInfo = new double[9][5];
	double[] convenerCoordinate = new double[2];//招集元のXY座標
	double temporary_droneInfo[][] = new double[9][3];
	ArrayList<Double> convening_droneInfo = new ArrayList<Double>();
	int divisionArea[][] = new int [4][2];
	double dvisionAreaDistance[][][] = new double[2][4][4];
	int convening_Order[][] = new int [4][3];//招集命令


	String message;
	int initX, initY;;//初期O

	Udp udp;
	Udp udp2;
	Udp udp3;

	EdgeServer() throws IOException{
		udp = new Udp(ID, "224.0.0.2");//UDPインスタンスにID付与
		udp.makeMulticastSocket();//ソケット生成
		udp.startListener();//受信

		udp2 = new Udp(ID, "224.0.0.3");
		udp2.makeMulticastSocket() ;//ソケット生成
		udp2.startListener() ;

		udp3 = new Udp(ID, "224.0.0.4");
		udp3.makeMulticastSocket() ;//ソケット生成
		udp3.startListener() ;

	}

	void receiveData(int initX, int initY, double lapseTime, int peaple) throws IOException{//受信メソッド
		this.initX = initX;
		this.initY = initY;
		byte[] rcvData = udp.lisner.getData();//受信データ
		byte[] rcvDataSecond = udp2.lisner.getData();
		byte[] rcvDataThird = udp3.lisner.getData();

		if(rcvDataSecond != null) {
			String str2 = new String(rcvDataSecond,0,110);
			String[] cData = str2.split(" ", 0);

			/*for (int i = 0 ; i < cData.length ; i++){
			      System.out.println(i + "番目の要素 = :" + cData[i]);
			}*/

			int dronePort = Integer.parseInt(cData[1]);
			double coordinateX = Double.parseDouble(cData[4]);//X座標
			double coordinateY = Double.parseDouble(cData[6]);//Y座標
			double droneBattery = Double.parseDouble(cData[8]);
			double state = Double.parseDouble(cData[9]);

			for(int i = 0;i < 9; i++) {
				if(dronePort == 50001 + i) {//全てのドローンのパラメータ
					droneInfo[i][0] = dronePort;
					droneInfo[i][1] = coordinateX;//X軸
					droneInfo[i][2] = coordinateY;//Y軸
					droneInfo[i][3] = droneBattery;
					droneInfo[i][4] = state;
				}
			}

		}
			/*for(int i = 0; i < 9; i++) {
				for(int j = 0; j < 4; j++) {
					System.out.println(droneInfo[i][j]);
				}

			}*/

		if(rcvData != null) {
			String str = new String(rcvData,0,121);//byte型から文字に変換
			udp.lisner.resetData();

			System.out.println("(エッジサーバ受信データ) "+str);
			eachData = str.split(" ", 0);//受信データの分割

			/*for (int i = 0 ; i < eachData.length ; i++){
			      System.out.println(i + "番目の要素 = :" + eachData[i]);
			}*/

			/*try {//ファイルへの書き込み
				FileWriter fw = new FileWriter("/Users/TKLab/Desktop/data.txt",true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(str);
				bw.newLine();//改行
				bw.flush();
				bw.close();//ファイル閉鎖
			}catch(IOException e) {
				System.out.println("エラー");
			}*/

			udp.lisner.resetData();//バッファの中のデータをリセット
		}

	}

}