package com.pFrame.pgraphic;

import com.pFrame.Pixel;
import com.pFrame.Position;
import com.pFrame.pwidget.PFrameKeyListener;

import java.util.ArrayList;
import java.util.Collections;

public class PGraphicScene {
    protected int width;
    protected int height;

    protected ArrayList<PGraphicItem> Items;

    protected ArrayList<PGraphicItem>[][] blocks;
    protected int blockSize;

    protected PGraphicView parentView;

    public ArrayList<PGraphicItem> getItems() {
        return Items;
    }

    public void setParentView(PGraphicView view) {
        this.parentView = view;
    }

    public PGraphicView getParentView() {
        return parentView;
    }

    public PGraphicScene(int width, int height) {
        this.width = width;
        this.height = height;
        this.Items = new ArrayList<>();
        blockSize = 100;
        blocks = new ArrayList[height / blockSize + 1][width / blockSize + 1];

        for (int i = 0; i < height / blockSize + 1; i++)
            for (int j = 0; j < width / blockSize + 1; j++)
                blocks[i][j] = new ArrayList<PGraphicItem>();
    }

    public ArrayList<PGraphicItem> getItemsAt(Position p) {
        Block block = positionToBlock(p);
        ArrayList<PGraphicItem> items = new ArrayList<>();
        ArrayList<PGraphicItem> copy;
        synchronized (blocks[block.x][block.y]) {
            copy = (ArrayList<PGraphicItem>) blocks[block.x][block.y].clone();
        }
        for (PGraphicItem item : copy) {
            if (item.includePosition(p)) {
                items.add(item);
            }
        }
        return items;
    }

    public void addKeyListener(char ch, PFrameKeyListener pFrameKeyListener) {
        if (parentView != null) {
            parentView.addPFrameKeyListener(ch, pFrameKeyListener);
        }
    }

    public void freeKeyListener(char ch, PFrameKeyListener pFrameKeyListener) {
        if (parentView != null) {
            parentView.addPFrameKeyListener(ch, null);
        }
    }


    record Block(int x, int y) {
    }

    public Block positionToBlock(Position position) {
        return new Block(position.getX() / blockSize, position.getY() / blockSize);
    }

    public PGraphicItem getTopItemAt(Position p) {
        ArrayList<PGraphicItem> items = getItemsAt(p);
        return items.get(items.size() - 1);
    }

    public Pixel[][] displayOutput(Position p, int width, int height) {

        int starty = p.getY() / blockSize;
        if (p.getY() < 0)
            starty--;
        int endy = (p.getY() + width) / blockSize;
        if (p.getY() + width < 0)
            endy--;
        int startx = p.getX() / blockSize;
        if (p.getX() < 0)
            startx--;
        int endx = (p.getX() + height) / blockSize;
        if (p.getX() + height < 0)
            endx--;

        int w = (endy - starty + 1) * blockSize;
        int h = (endx - startx + 1) * blockSize;


        Pixel[][] pixels = Pixel.emptyPixels(w, h);

        for (int i = starty; i <= endy; i++) {
            for (int j = startx; j <= endx; j++) {
                Pixel.pixelsAdd(pixels, calBlockPixels(new Block(j, i)), Position.getPosition((j - startx) * blockSize, (i - starty) * blockSize));
            }
        }

        return Pixel.subPixels(pixels, Position.getPosition(p.getX() - startx * blockSize, p.getY() - starty * blockSize), width, height);
    }

    public Pixel[][] calBlockPixels(Block block) {
        Pixel[][] pixels = Pixel.emptyPixels(blockSize, blockSize);
        if (block.x >= 0 && block.x < this.blocks.length && block.y >= 0 && block.y < this.blocks[0].length) {
            ArrayList<PGraphicItem> items;
            synchronized (blocks[block.x][block.y]) {
                items = (ArrayList<PGraphicItem>) this.blocks[block.x][block.y].clone();
            }
            for (PGraphicItem item : items) {
                Pixel.pixelsAdd(pixels, item.getPixels(), Position.getPosition(item.getPosition().getX() - block.x * blockSize, item.getPosition().getY() - block.y * blockSize));
            }
        }
        return pixels;
    }

    public void repaintItem(PGraphicItem item) {
        synchronized (item) {
            ArrayList<Block> oldBlocks = calBlock(item.getOldPos(), item.getWidth(), item.getHeight());
            ArrayList<Block> newBlocks = calBlock(item.getPosition(), item.getWidth(), item.getHeight());
            ArrayList<ArrayList<PGraphicItem>> toremove = new ArrayList<>();
            ArrayList<ArrayList<PGraphicItem>> toadd = new ArrayList<>();
            for (Block block : oldBlocks) {
                toremove.add(blocks[block.x][block.y]);
            }
            for (Block block : newBlocks) {
                synchronized (blocks[block.x][block.y]) {
                    if (toremove.contains(blocks[block.x][block.y])) {
                        toremove.remove(blocks[block.x][block.y]);
                    } else {
                        toadd.add(blocks[block.x][block.y]);
                    }
                }
            }
            for (ArrayList<PGraphicItem> list : toremove)
                synchronized (list) {
                    list.remove(item);
                }
            for (ArrayList<PGraphicItem> list : toadd) {
                synchronized (list) {
                    list.add(item);
                    Collections.sort(list);
                }
            }
        }
    }

    public ArrayList<Block> calBlock(Position p, int width, int height) {
        ArrayList<Block> res = new ArrayList<>();
        int starty = p.getY() / blockSize;
        int endy = (p.getY() + width) / blockSize;
        int startx = p.getX() / blockSize;
        int endx = (p.getX() + height) / blockSize;
        for (int i = Math.max(0, starty); i <= Math.min(endy, this.blocks.length - 1); i++) {
            for (int j = Math.max(0, startx); j <= Math.min(endx, this.blocks[0].length - 1); j++) {
                res.add(new Block(j, i));
            }
        }
        return res;
    }


    public boolean removeItem(PGraphicItem item) {
        synchronized (item) {
            boolean res;
            synchronized (this.Items) {
                res = this.Items.remove(item);
            }
            if (res) {
                item.removeParentScene();
                ArrayList<Block> blocks = calBlock(item.getPosition(), item.getWidth(), item.getHeight());
                for (Block block : blocks) {
                    synchronized (this.blocks[block.x][block.y]) {
                        this.blocks[block.x][block.y].remove(item);
                    }
                }
            }
            return res;
        }
    }

    public boolean addItem(PGraphicItem item) {
        synchronized (item) {
            synchronized (this.Items) {
                this.Items.add(item);
            }
            ArrayList<Block> blocks = calBlock(item.getPosition(), item.getWidth(), item.getHeight());
            for (Block block : blocks) {
                synchronized (this.blocks[block.x][block.y]) {
                    this.blocks[block.x][block.y].add(item);
                    Collections.sort(this.blocks[block.x][block.y]);
                }
            }
            item.setParentScene(this);
            return true;
        }
    }

    public boolean addItem(PGraphicItem item, Position p) {
        item.setPosition(p);
        return addItem(item);
    }
}
