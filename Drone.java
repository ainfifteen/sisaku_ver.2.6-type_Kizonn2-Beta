package sisaku;

public class Drone {

	final static int EDGE_SERVER = 60000;

	final static int WAIT = 0;//初期
	final static int GO = 1;//移動
	final static int SENSING = 2;//通常状態
	final static int GATHERING = 3;//招集
	final static int BACK = 4;//帰還
	final static int END = 5;//終了
	final static int gWAIT = 6;//招集待ち状態

	final static short NULL = 0;
	final static short NORTH = 1;
	final static short EAST = 2;
	final static short SOUTH = 3;
	final static short WEST = 4;

	final static int gDNULL = 0;
	final static short gNORTH = 1;
	final static short gEAST = 2;
	final static short gSOUTH = 3;
	final static short gWEST = 4;

	final static double CONSUMPTION = 0.06;//1秒間の電池消費

	int id;//ドローンのID
	double x,y,z;
	double battery;//バッテリー
	int state;//ドローンの状態
	int gatheringState;
	int initState;
	int initX, initY;;//初期値
	double gInitX;
	double gInitY;
	double meetingPlaceX;
	double meetingPlaceY;
	int oneBlock;//1区画
	int gOneBlock;
	int discover;//データの格納
	int gDiscover;
	String message;
	String time;

	short direction;//方向
	short gDirection;//招集時の方向
	double speed;//スピード
	double firstMove;//初期移動
	double firstGatheringMove;
	double endGatheringMove;
	double arrivalTime;//到着時間
	double arrivalGatheringTime;
	double endGatheringTime;
	double lapseTime;//経過時間

	String meetingPlace[] = new String [2];

	Udp udp;
	Udp udp2;
	Udp udp3;


	Drone(int id, int initX, int initY){//コンストラクタ
		this.id = id;
		this.initX = initX;
		this.initY = initY;
		x = 0.0;
		y = 0.0;
		battery = 100.0;
		lapseTime = 0.0;
		state = WAIT;
		direction = NULL;
		gDirection = NULL;
		speed = 10;
		oneBlock = 30;
		gOneBlock = 20;
		firstMove = Math.sqrt(Math.pow(initX - x, 2) + Math.pow(initY - y, 2));
		arrivalTime = firstMove / speed;
		message = "Normal ";
		time = " ";

		udp = new Udp(id, "224.0.0.2");
		udp.makeMulticastSocket() ;//ソケット生成
		udp.startListener() ;//受信

		udp2 = new Udp(id, "224.0.0.3");
		udp2.makeMulticastSocket() ;//ソケット生成
		udp2.startListener() ;//受信

		udp3 = new Udp(id, "224.0.0.4");
		udp3.makeMulticastSocket() ;//ソケット生成
		udp3.startListener() ;
	}

	void move(double simTime) {//移動メソッド

		lapseTime += simTime;


		if(state != END) {
			battery -= CONSUMPTION * simTime;
		}

		if(battery < 10.0) state = BACK;//10%以下で帰還


		udp2.sendData(id, x, y, battery, state, EDGE_SERVER);
		udp2.lisner.resetData();



		switch(state) {

		case WAIT:
			 state = GO;
			 break;

		case GO:
			message = "Normal ";
			double goTheta = Math.atan2(initY, initX);//角度
			double goDistance = speed * simTime;
			x += goDistance * Math.cos(goTheta);
			y += goDistance * Math.sin(goTheta);


			if(lapseTime >= arrivalTime){
				x = initX;
				y = initY;
				time = "sensingstart";
				direction = SOUTH;
				lapseTime = 0.0;
				state = SENSING;
			}
			break;

		case SENSING:
			time = " ";
			message = "Normal ";
			//convenerRecieveData();
			if(lapseTime >= 2.0) {
				switch(direction) {
				case NORTH://上へ
					y += 20;
					if(y >= initY) direction = EAST;
					break;

				case EAST://右へ
					x += 20;
					if(y >= initY) direction = SOUTH;
					else direction = NORTH;
					break;

				case SOUTH://下へ
					y -= 20;
					if(y <= initY - 220) direction = EAST;
					break;

				case WEST: break;//左へ
				default: break;

				}

				//judgRecieveData();

			    lapseTime = 0.0;//経過時間

			}

			if(x >= initX + 220 && y >= initY) {
				time = "sensingEnd";
				state = BACK;
				direction = NULL;
			}

			break;


		case BACK:
			time = " ";
			message = " ";
			System.out.println("帰還中");
			double backTheta = Math.atan2(y, x);
			double backDistance = speed * simTime;
			x -= backDistance * Math.cos(backTheta);
			y -= backDistance * Math.sin(backTheta);

			if(x <= 0 && y <= 0) {
				x = 0;
				y = 0;
				time = "missionEnd";
				state = END;
				direction = NULL;
			}
			break;
		case END:
			time = " ";

		default:
			break;
		}

	 }

	void gDataGet(int[][] divisionArea){//招集時データ収集メソッド
		if(state == SENSING) {
				gDiscover = divisionArea[(int)(x / gOneBlock) ][(int)(y / gOneBlock) ];//データ抽出
				udp3.sendData(id, message, gDiscover, x, y, battery, EDGE_SERVER);//エッジに送信
				udp3.lisner.resetData();

		}

	}

}