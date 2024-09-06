import io.socket.emitter.Emitter;
import jsclub.codefest2024.sdk.Hero;
import jsclub.codefest2024.sdk.algorithm.PathUtils;
import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.GameMap;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Weapon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jsclub.codefest2024.sdk.model.enemies.Enemy;
import jsclub.codefest2024.sdk.model.equipments.Armor;
import jsclub.codefest2024.sdk.model.equipments.HealingItem;

public class Main {

	private static final String SERVER_URL = "https://cf-server.jsclub.dev";
	private static final String GAME_ID = "122236";
	private static final String PLAYER_NAME = "konan";

	static int step = 660;
	static int middle_state = 630;
	static int last_state = 600;

	public static void main(String[] args) throws IOException {
		Hero hero = new Hero(GAME_ID, PLAYER_NAME);

		Emitter.Listener onMapUpdate = new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				try {
					//khoi tao gamemap
					GameMap gameMap = hero.getGameMap();
					gameMap.updateOnUpdateMap(args[0]);
					
					//lay thong tin cua minh
					Player player = gameMap.getCurrentPlayer();
					//tong hop tat ca vi tri can tranh ne
					List<Node> allRestrictedNode = getAllRestrictedNode(gameMap);

					//lay tat ca cac chest va vi tri cac chest
					List<Node> chestNodeList = getAllChestNode(gameMap);
 
					//nhat tat ca armor 
					considerPickingArmor();

					//nhat bullet
					considerPickingBullet();

					//lay vi tri nguoi choi hien tai
					Node currentNode = new Node(player.getX(), player.getY());

					if (step > middle_state) {
						hero.move("u");
						do_first_state();
						System.out.println("gd1");
					} else if (step > last_state) {
						hero.move("r");
						do_middle_state();
						System.out.println("gd2");
					} else {
						hero.move("d");
						do_last_state();
						System.out.println("gd3");
					}

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				step--;
			}
		};

		hero.setOnMapUpdate(onMapUpdate);
		hero.start(SERVER_URL);
	}

	static void do_first_state() {
		//
	}

	static void do_middle_state() {

	}

	static void do_last_state() {

	}

	static void considerPickingArmor() {
		
	}

	static void considerPickingBullet() {

	}

	static List<Node> getAllRestrictedNode(GameMap gameMap) {
		//lay thong tin cua nhung nguoi choi khac
		List<Player> otherPlayers = gameMap.getOtherPlayerInfo();

		//lay chuong ngai vat khong the pha huy, tat ca cac bay
		List<Obstacle> restricedList = gameMap.getListIndestructibleObstacles();
		restricedList.addAll(gameMap.getListTraps());

		//lay tat ca enemy
		List<Enemy> listEnemy = gameMap.getListEnemies();

		//tong hop tat ca vi tri can tranh ne
		List<Node> restrictedNodes = new ArrayList<>();
		for (Player p : otherPlayers) {
			if (p.getIsAlive()) {
				restrictedNodes.add(new Node(p.getX(), p.getY()));
			}
		}

		for (Enemy e : listEnemy) {
			restrictedNodes.add(new Node(e.getX(), e.getY()));
		}

		for (Obstacle o : restricedList) {
			restrictedNodes.add(new Node(o.getX(), o.getY()));
		}

		return restrictedNodes;
	}
	static List<Node> getAllChestNode(GameMap gameMap) {
		//lay tat ca cac chest va vi tri cac chest
		List<Obstacle> chestList = gameMap.getListChests();
		List<Node> chestNodeList = new ArrayList<>();
		for (Obstacle o : chestList) {
			chestNodeList.add(new Node(o.getX(), o.getY()));
		}
		return chestNodeList;
	}
	static List<Node> getAllGunNode(GameMap gameMap) {
		//lay tat ca cac gun
		List<Weapon> gunList = gameMap.getAllGun();
		List<Node> gunNodeList = new ArrayList<>();
		for (Weapon o : gunList) {
			gunNodeList.add(new Node(o.getX(), o.getY()));
		}
		return gunNodeList;
	}
	static List<Node> getAllThrowableNode(GameMap gameMap) {
		//lay tat ca cac throwable
		List<Weapon> throwableList = gameMap.getAllThrowable();
		List<Node> throwableNodeList = new ArrayList<>();
		for (Weapon o : throwableList) {
			throwableNodeList.add(new Node(o.getX(), o.getY()));
		}
		return throwableNodeList;
	}
	static List<Node> getAllMeleeNode(GameMap gameMap) {
		//lay tat ca cac melee
		List<Weapon> meleeList = gameMap.getAllMelee();
		List<Node> meleeNodeList = new ArrayList<>();
		for (Weapon o : meleeList) {
			meleeNodeList.add(new Node(o.getX(), o.getY()));
		}
		return meleeNodeList;
	}
	static List<Node> getAllArmorNode(GameMap gameMap) {
		//lay tat ca cac armor
		List<Armor> armorList = gameMap.getListArmors();
		List<Node> armorNodeList = new ArrayList<>();
		for (Armor o : armorList) {
			armorNodeList.add(new Node(o.getX(), o.getY()));
		}
		return armorNodeList;
	}
	static List<Node> getAllHealingNode(GameMap gameMap) {
		//lay tat ca cac healing
		List<HealingItem> healingList = gameMap.getListHealingItems();
		List<Node> healingNodeList = new ArrayList<>();
		for (HealingItem o : healingList) {
			healingNodeList.add(new Node(o.getX(), o.getY()));
		}
		return healingNodeList;
	}
	
	//tạo 1 hàm dựa vào độ dài chuỗi của hàm getShortestPath() để tìm vị trí đồ gần nhất,
	//trả về con đường đi tới đồ gần nhất, tọa độ của đồ gần nhất(Node).
	static Object[] getShortestPathToItems(GameMap gameMap){
		Object[] res = new Object[2];
		//tạo list tính độ dài đến từng items
		List<String> shortestPathToAllItems = new ArrayList<>();
		//lấy tất cả vị trí đồ vật theo Node
		List<Node> allItemsNode = new ArrayList<>();
		allItemsNode.addAll(getAllRestrictedNode(gameMap));
		allItemsNode.addAll(getAllChestNode(gameMap));
		allItemsNode.addAll(getAllGunNode(gameMap));
		allItemsNode.addAll(getAllThrowableNode(gameMap));
		allItemsNode.addAll(getAllArmorNode(gameMap));
		
		return res;
	}
	

}
