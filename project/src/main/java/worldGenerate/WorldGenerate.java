package worldGenerate;

import com.pFrame.Pixel;
import com.pFrame.Position;
import com.pFrame.pgraphic.PGraphicItem;
import com.pFrame.pgraphic.PGraphicScene;
import com.pFrame.pgraphic.PGraphicView;
import com.pFrame.pwidget.PHeadWidget;
import mazeGenerator.MazeGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class WorldGenerate {
    private final int width;
    private final int height;
    private final int max_rooms_generate_trys;
    private final int room_max_height;
    private final int room_min_height;
    private final int room_max_width;
    private final int room_min_width;
    private MazeGenerator mazeGenerator;
    PGraphicScene scene;
    PGraphicItem item;

    private int[][] world;
    private final ArrayList<Room> roomsArray;
    private final ArrayList<Position> oddNodes;
    private Position start;
    private Room aim;

    public Room getAim() {
        return aim;
    }

    public Position getStart() {
        return start;
    }

    public ArrayList<Room> getRoomsArray() {
        return this.roomsArray;
    }

    public ArrayList<Position> getOddNodes() {
        return this.oddNodes;
    }

    public WorldGenerate(int width, int height, int max_rooms_generate_trys, int room_max_width, int room_min_width,
                         int room_max_height, int room_min_height) {
        this.width = width;
        this.height = height;
        this.max_rooms_generate_trys = max_rooms_generate_trys;
        this.room_max_height = room_max_height;
        this.room_max_width = room_max_width;
        this.room_min_height = room_min_height;
        this.room_min_width = room_min_width;

        roomsArray = new ArrayList<>();
        oddNodes = new ArrayList<>();
        this.world = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                world[i][j] = 0;
            }
        }
    }

    public int[][] generate() {
        this.maze_generate();
        this.oddDotRemove();
        this.rooms_generate();
        this.rooms_link();
        this.generateStartAndEndNode();
        return this.world;
    }

    private void generateStartAndEndNode() {
        Room curRoom = roomsArray.get(0);
        Position curPos = oddNodes.get(0);
        int maxDistance = 0;
        for (Room room : roomsArray)
            for (Position pos : oddNodes) {
                if (((pos.getX() - room.pos.getX()) * (pos.getX() - room.pos.getX())
                        + (pos.getY() - room.pos.getY()) * (pos.getY() - room.pos.getY())) > maxDistance) {
                    curPos = pos;
                    curRoom = room;
                    maxDistance = ((pos.getX() - room.pos.getX()) * (pos.getX() - room.pos.getX())
                            + (pos.getY() - room.pos.getY()) * (pos.getY() - room.pos.getY()));
                }
            }
        this.start = curPos;
        this.aim = curRoom;
        world[this.start.getX()][start.getY()] = 5;
        world[aim.pos.getX()][aim.pos.getY()] = 6;

        for (Position position : oddNodes) {
            world[position.getX()][position.getY()] = 5;
        }
    }

    private Room getUnLinkedRoom() {
        for (Room room : roomsArray) {
            if (!room.linkToRoad)
                return room;
        }
        return null;
    }

    private void rooms_link() {
        Room current = getUnLinkedRoom();
        int failed_count = 0;
        while (current != null) {
            ArrayList<LinkNode> linkNodes = this.findLinkNode(current);
            if (linkNodes.size() == 0) {
                this.roomLarger(current);
            } else {
                for (LinkNode node : linkNodes) {
                    if (node.linkRoad && !current.linkToRoad) {
                        world[node.node.getX()][node.node.getY()] = 4;
                        current.linkToRoad = true;
                    }
                    if (node.linkRoad && node.linkedRoom != null
                            && !current.linkedRooms.contains(node.linkedRoom)) {
                        world[node.node.getX()][node.node.getY()] = 4;
                        current.linkToRoad = true;
                        current.linkedRooms.add(node.linkedRoom);
                    } else if (node.linkedRoom != null && node.linkedRoom.linkToRoad
                            && !current.linkedRooms.contains(node.linkedRoom)) {
                        world[node.node.getX()][node.node.getY()] = 4;
                        current.linkedRooms.add(node.linkedRoom);
                        current.linkToRoad = true;
                    }
                }
            }
            if (!current.linkToRoad) {
                roomsArray.remove(current);
                roomsArray.add(current);
                failed_count++;
                current = getUnLinkedRoom();
                if (failed_count >= roomsArray.size()) {
                    roomLarger(current);
                }
            } else {
                failed_count = 0;
                current = getUnLinkedRoom();
            }
        }
    }

    private void roomLarger(Room room) {
        if (isOutBound(Position.getPosition(room.pos.getX() - 1, room.pos.getY() - 1))) {
            if (isOutBound(Position.getPosition(room.pos.getX() + room.height, room.pos.getY() + room.width))) {
                if (isOutBound(Position.getPosition(room.pos.getX() + room.height, room.pos.getY() - 1))) {
                    if (isOutBound(Position.getPosition(room.pos.getX() - 1, room.pos.getY() + room.width))) {

                    } else {
                        room.height++;
                        room.width++;
                        room.pos = Position.getPosition(room.pos.getX() - 1, room.pos.getY());
                    }
                } else {
                    room.height++;
                    room.width++;
                    room.pos = Position.getPosition(room.pos.getX(), room.pos.getY() - 1);
                }

            } else {
                room.height = room.height + 1;
                room.width = room.width + 1;
            }
        } else {
            room.pos = Position.getPosition(room.pos.getX() - 1, room.pos.getY() - 1);
            room.height++;
            room.width++;
        }
        for (int i = 0; i < room.height; i++) {
            for (int j = 0; j < room.width; j++) {
                if (world[room.pos.getX() + i][room.pos.getY() + j] == 0) {
                    world[room.pos.getX() + i][room.pos.getY() + j] = 3;
                }
            }
        }
    }

    private void oddDotRemove() {
        int count = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (world[i][j] == 1)
                    count++;
            }
        int remove_count = count * 2 / 4;
        Queue<Position> p = new LinkedList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ArrayList<Position> list = this.nonemptyPositionAround(Position.getPosition(i, j));
                if (list.size() == 1 && isNonEmptyOrOutBound(Position.getPosition(i, j))) {
                    p.add(Position.getPosition(i, j));
                }
            }
        }
        while (remove_count > 0) {
            Position position = p.poll();
            if (isNonEmptyOrOutBound(position) && this.nonemptyPositionAround(position).size() <= 1) {
                if (this.nonemptyPositionAround(position).size() < 1) {
                    tagOnePixel(position, 0);
                    remove_count--;
                } else {
                    p.add(nonemptyPositionAround(position).get(0));
                    tagOnePixel(position, 0);
                    remove_count--;
                }
            }
        }

        this.oddNodes.clear();
        this.oddNodes.addAll(p);
    }

    private ArrayList<LinkNode> findLinkNode(Room room) {
        ArrayList<LinkNode> res = new ArrayList<>();
        Position roomPos = leftPosition(upPosition(room.pos));
        int roomWidth = room.width + 2;
        int roomHeight = room.height + 2;
        for (int a = 0; a < roomHeight; a++) {
            for (int b = 0; b < roomWidth; b++) {
                if (a == 0 || a == roomHeight - 1 || b == 0 || b == roomWidth - 1) {
                    if (isOutBound(Position.getPosition(roomPos.getX() + a, roomPos.getY() + b))
                            || isNonEmptyOrOutBound(Position.getPosition(roomPos.getX() + a, roomPos.getY() + b))) {

                    } else {
                        ArrayList<Position> nonemptyList = nonemptyPositionAround(
                                Position.getPosition(roomPos.getX() + a, roomPos.getY() + b));
                        if (nonemptyList.size() >= 2) {
                            LinkNode node = new LinkNode(Position.getPosition(roomPos.getX() + a, roomPos.getY() + b),
                                    null, false);
                            boolean nonemptyListContainsSelfRoom = false;
                            for (Position nonemptyNode : nonemptyList) {
                                if (world[nonemptyNode.getX()][nonemptyNode.getY()] == 1) {
                                    node.linkRoad = true;
                                } else if (world[nonemptyNode.getX()][nonemptyNode.getY()] == 2
                                        || world[nonemptyNode.getX()][nonemptyNode.getY()] == 3) {
                                    if (positionInRoom(Position.getPosition(nonemptyNode.getX(), nonemptyNode.getY()),
                                            room)) {
                                        nonemptyListContainsSelfRoom = true;
                                    } else {
                                        for (Room r : roomsArray) {
                                            if (positionInRoom(
                                                    Position.getPosition(nonemptyNode.getX(), nonemptyNode.getY()),
                                                    r)) {
                                                node.linkedRoom = r;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (nonemptyListContainsSelfRoom)
                                res.add(node);
                        }
                    }
                }
            }
        }
        return res;
    }

    private boolean positionInRoom(Position p, Room room) {
        return p.getX() >= room.pos.getX() && p.getX() < (room.pos.getX() + room.height) && p.getY() >= room.pos.getY()
                && p.getY() < (room.pos.getY() + room.width);
    }

    private void rooms_generate() {
        Random random = new Random(82734994);
        // 进行至多max_rooms_generate_trys次的room生成尝试
        for (int i = 0; i < this.max_rooms_generate_trys; i++) {

            // 生成随机大小和位置的rooms
            Position pos = Position.getPosition(random.nextInt(this.height), random.nextInt(this.width));
            int random_width = random.nextInt(this.room_min_width, this.room_max_width);
            int random_height = random.nextInt(this.room_min_height, this.room_max_height);
            // 对生成的room合法性检查
            boolean vaild = true;
            for (int a = 0; a < random_height; a++) {
                for (int b = 0; b < random_width; b++) {
                    if (isOutBound(Position.getPosition(pos.getX() + a, pos.getY() + b))) {
                        vaild = false;
                        break;
                    } else if (!isEmptyOrOutBound(Position.getPosition(pos.getX() + a, pos.getY() + b))
                            || !isEmptyOrOutBound(Position.getPosition(pos.getX() + a - 1, pos.getY() + b))
                            || !isEmptyOrOutBound(Position.getPosition(pos.getX() + a + 1, pos.getY() + b))
                            || !isEmptyOrOutBound(Position.getPosition(pos.getX() + a, pos.getY() + b - 1))
                            || !isEmptyOrOutBound(Position.getPosition(pos.getX() + a, pos.getY() + b + 1))) {
                        vaild = false;
                        break;
                    }
                }
                if (!vaild) {
                    break;
                }
            }

            // 如果room没有越界或者和其它room重合，加入roomsArray;
            if (vaild) {
                for (int a = 0; a < random_height; a++) {
                    for (int b = 0; b < random_width; b++) {
                        world[pos.getX() + a][pos.getY() + b] = 2;
                    }
                }
                roomsArray.add(new Room(pos, random_width, random_height));
            }
        }
    }

    private void maze_generate() {
        mazeGenerator = new MazeGenerator(height);

        mazeGenerator.generateMaze();
        this.world = mazeGenerator.getMaze();
    }

    ArrayList<Position> nonemptyPositionAround(Position p) {
        ArrayList<Position> list = new ArrayList<>();
        if (isNonEmptyOrOutBound(leftPosition(p)))
            list.add(leftPosition(p));
        if (isNonEmptyOrOutBound(upPosition(p)))
            list.add(upPosition(p));
        if (isNonEmptyOrOutBound(downPosition(p)))
            list.add(downPosition(p));
        if (isNonEmptyOrOutBound(rightPosition(p)))
            list.add(rightPosition(p));
        return list;
    }

    ArrayList<Position> emptyPositionAround(Position p) {
        ArrayList<Position> list = new ArrayList<>();
        if (isEmptyOrOutBound(leftPosition(p)))
            list.add(leftPosition(p));
        if (isEmptyOrOutBound(upPosition(p)))
            list.add(upPosition(p));
        if (isEmptyOrOutBound(downPosition(p)))
            list.add(downPosition(p));
        if (isEmptyOrOutBound(rightPosition(p)))
            list.add(rightPosition(p));
        return list;
    }

    boolean isEmpty(Position p) {
        if (isOutBound(p))
            return false;
        else
            return (this.world[p.getX()][p.getY()] == 0);
    }

    void tagOnePixel(Position p, int x) {
        world[p.getX()][p.getY()] = x;
    }

    boolean isEmptyAround(Position p) {
        return isEmptyOrOutBound(upPosition(p)) && isEmptyOrOutBound(downPosition(p))
                && isEmptyOrOutBound(leftPosition(p)) && isEmptyOrOutBound(rightPosition(p));
    }

    Position upPosition(Position p) {
        return Position.getPosition(p.getX() - 1, p.getY());
    }

    Position downPosition(Position p) {
        return Position.getPosition(p.getX() + 1, p.getY());
    }

    Position leftPosition(Position p) {
        return Position.getPosition(p.getX(), p.getY() - 1);
    }

    Position rightPosition(Position p) {
        return Position.getPosition(p.getX(), p.getY() + 1);
    }

    Position upPosition(Position p, int x) {
        return Position.getPosition(p.getX() - x, p.getY());
    }

    Position downPosition(Position p, int x) {
        return Position.getPosition(p.getX() + x, p.getY());
    }

    Position leftPosition(Position p, int x) {
        return Position.getPosition(p.getX(), p.getY() - x);
    }

    Position rightPosition(Position p, int x) {
        return Position.getPosition(p.getX(), p.getY() + x);
    }

    boolean isEmptyOrOutBound(Position p) {
        if (isOutBound(p)) {
            return true;
        } else {
            return (this.world[p.getX()][p.getY()] == 0);
        }
    }

    boolean isNonEmptyOrOutBound(Position p) {

        return !isOutBound(p) && (this.world[p.getX()][p.getY()] != 0);
    }

    boolean isOutBound(Position p) {
        return (p.getX() >= this.height) || (p.getX() < 0) || (p.getY() >= this.width) || (p.getY() < 0);
    }

    public class Room {
        public Position pos;
        public int width;
        public int height;
        boolean linkToRoad = false;
        ArrayList<Room> linkedRooms = new ArrayList<>();

        Room(Position pos, int width, int height) {
            this.pos = pos;
            this.width = width;
            this.height = height;
        }
    }

    class LinkNode {
        Position node;
        Room linkedRoom;
        boolean linkRoad;

        LinkNode(Position node, Room linkedRoom, boolean linkRoad) {
            this.node = node;
            this.linkedRoom = linkedRoom;
            this.linkRoad = linkRoad;
        }
    }

    public PGraphicItem toPGraphicItem() {
        if (world == null)
            return null;
        else {
            int width = world[0].length;
            int height = world.length;
            Pixel[][] pixels = Pixel.emptyPixels(width, height);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    switch (world[i][j]) {
                        case 0 -> pixels[i][j] = Pixel.getPixel(Color.BLACK, (char) 0xf0);
                        case 1 -> pixels[i][j] = Pixel.getPixel(Color.CYAN, (char) 0xf0);
                        case 2 -> pixels[i][j] = Pixel.getPixel(Color.GRAY, (char) 0xf0);
                        case 3 -> pixels[i][j] = Pixel.getPixel(Color.LIGHT_GRAY, (char) 0xf0);
                        case 4 -> pixels[i][j] = Pixel.getPixel(Color.ORANGE, (char) 0xf0);
                        case 5 -> pixels[i][j] = Pixel.getPixel(Color.BLUE, (char) 0xf0);
                        case 6 -> pixels[i][j] = Pixel.getPixel(Color.RED, (char) 0xf0);
                        default -> {
                        }
                    }
                }
            }
            return new PGraphicItem(pixels);
        }
    }

    public static void main(String[] args) {
        WorldGenerate generate = new WorldGenerate(500, 500, 2000000, 20, 2, 20, 2);
        generate.generate();
        PGraphicItem item = generate.toPGraphicItem();
        PGraphicItem item2 = new PGraphicItem(Pixel.pixelsScaleLarger(item.getPixels(), 5));
        PHeadWidget pHeadWidget = new PHeadWidget(null, null);
        PGraphicScene scene = new PGraphicScene(2500, 2500);
        scene.addItem(item2, Position.getPosition(0, 0));
        PGraphicView view = new PGraphicView(pHeadWidget, null, scene);
        view.setViewPosition(Position.getPosition(0, 0));
        generate.item = item2;
        generate.scene = scene;
        pHeadWidget.startRepaintThread();
    }
}
