package game.screen;

import com.pFrame.Pixel;
import com.pFrame.Position;
import com.pFrame.pwidget.PButton;
import com.pFrame.pwidget.PImage;
import com.pFrame.pwidget.PLayout;
import com.pFrame.pwidget.PWidget;
import log.Log;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;

public class ArchiveListView extends PWidget {
    String archivePath;
    UI ui;
    int boarder=10;

    public ArchiveListView(PWidget parent, Position p, String path, UI ui) {
        super(parent, p);
        archivePath = path;
        this.ui = ui;
        layout = null;
        try {
            load_archive();
        } catch (Exception e) {
            e.printStackTrace();
            Log.ErrorLog(this, "load game archive failed...");
        }
        addBackground(PImage.getPureImage(Color.LIGHT_GRAY));
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        try {
            load_archive();
        } catch (Exception e) {
            e.printStackTrace();
            Log.ErrorLog(this, "load game archive failed...");
        }
    }

    ArrayList<PWidget> items = new ArrayList<>();

    private void load_archive() throws NoSuchMethodException {
        items = new ArrayList<>();
        if (widgetWidth > boarder && widgetHeight > 5) {
            File dir = new File(archivePath);
            String[] files = dir.list();
            if (files == null)
                return;
            for (String file : files) {
                PLayout pLayout = new PLayout(null, null, 1, 2);
                pLayout.changeWidgetSize(widgetWidth - boarder, 15);
                pLayout.setColumnLayout("4x,1x");
                GameStartButton pButton = new GameStartButton(pLayout, Position.getPosition(1, 1), file);
                pButton.addBackground(PImage.getPureImage(Color.GRAY));
                pButton.setText(file.split("\\.")[0], 1, Color.BLUE);
                ArchiveDeleteButton pButton1 = new ArchiveDeleteButton(pLayout, Position.getPosition(1, 2), file);
                pButton1.addBackground(PImage.getPureImage(Color.RED));
                pButton1.setText("Del", 1, Color.BLUE);
                items.add(pLayout);
            }
        } else {
            Log.WarningLog(this, "widget size too small,some features have been disable");
        }
    }

    int start = 0;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        if (e.getWheelRotation() == 1) {
            start+=4;
            start = Math.min(start, 15 * items.size() - widgetHeight);
        } else {
            start-=4;
        }
        start = Math.max(0, start);
    }

    @Override
    public ArrayList<PWidget> getWidgetsAt(Position p) {
        int height = p.getX() + start;
        int index = height / 15;
        ArrayList<PWidget> res = new ArrayList<>();
        res.add(this);
        if (index < items.size() && p.getY() < widgetWidth - boarder) {
            res.addAll(items.get(index).getWidgetsAt(Position.getPosition(height - 15 * index, p.getY())));
        }
        return res;
    }


    @Override
    public Pixel[][] displayOutput() {
        pixels = super.displayOutput();
        if (!items.isEmpty()) {
            int index_start = start / 15;
            int index_end = Math.min(items.size() - 1, (start + widgetHeight) / 15);

            int board_start=(start*widgetHeight)/(items.size()*15);
            int board_length=Math.min(board_start+(widgetHeight*widgetHeight)/(items.size()*15),widgetHeight);
            for(int i=board_start;i<board_length;i++){
                for(int j=widgetWidth-boarder;j<widgetWidth;j++){
                    pixels[i][j]=Pixel.getPixel(Color.GRAY,(char)0xf0);
                }
            }

            Pixel[][] graphic = Pixel.emptyPixels(widgetWidth - boarder, (index_end - index_start + 1) * 15);
            for (int i = 0; i <= index_end - index_start; i++) {
                Pixel.pixelsAdd(graphic, items.get(index_start + i).displayOutput(), Position.getPosition(i * 15, 0));
            }
            graphic=Pixel.subPixels(graphic, Position.getPosition(start % 15, 0),widgetWidth - boarder, widgetHeight);
            pixels = Pixel.pixelsAdd(pixels, graphic, Position.getPosition(0, 0));
        }
        return pixels;
    }

    private class GameStartButton extends PButton {
        String gamePath;

        public GameStartButton(PWidget parent, Position p, String path) {
            super(parent, p);
            gamePath = path;
        }

        @Override
        public void mouseClicked(MouseEvent e, Position p) {
            super.mouseClicked(e, p);
            ui.loadSavedData(archivePath + "/" + gamePath);
        }
    }

    private class ArchiveDeleteButton extends PButton {
        String gamePath;

        public ArchiveDeleteButton(PWidget parent, Position p, String path) {
            super(parent, p);
            gamePath = path;
        }

        @Override
        public void mouseClicked(MouseEvent e, Position p) {
            super.mouseClicked(e, p);
            try {
                new File(archivePath + "/" + gamePath).delete();
                load_archive();
            } catch (Exception ep) {
                ep.printStackTrace();
                Log.ErrorLog(this, "delete archive failed");
            }
        }
    }
}
