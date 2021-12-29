package com.pFrame.pwidget;

import com.pFrame.Pixel;
import com.pFrame.Position;
import log.Log;

import java.util.ArrayList;

public class PLayout extends PWidget {
    protected int rownum;
    protected int columnnum;
    protected String rowStyle;
    protected String columnStyle;
    protected PWidget[][] containedWidgets;
    protected boolean hasInset;

    public void setInset(boolean selection) {
        this.hasInset = selection;
        this.updateWidgetsLayout();
    }

    public void setRCNum(int x, int y) {
        this.rownum = x;
        this.columnnum = y;
        this.containedWidgets = new PWidget[rownum][columnnum];
        updateWidgetsLayout();
    }

    public void setRCNumStyle(int x, int y, String xstyle, String ystyle) {
        this.rownum = x;
        this.rowStyle = xstyle;
        this.columnStyle = ystyle;
        this.columnnum = y;
        this.containedWidgets = new PWidget[rownum][columnnum];
        updateWidgetsLayout();
    }

    public int getRowNum() {
        return this.rownum;
    }

    public int getColumnNum() {
        return this.columnnum;
    }

    protected String getRowStyle() {
        return this.rowStyle;
    }

    protected String getColumnStyle() {
        return this.columnStyle;
    }

    public PLayout(PWidget parent, Position p, int rownum, int columnnum) {
        super(parent, p);
        this.rownum = rownum;
        this.columnnum = columnnum;
        this.rowStyle = "";
        this.columnStyle = "";
        this.hasInset = false;
        containedWidgets = new PWidget[rownum][columnnum];
        for (int i = 0; i < rownum; i++)
            for (int j = 0; j < columnnum; j++)
                containedWidgets[i][j] = null;
        this.updateWidgetsLayout();
    }

    public PLayout(PWidget parent, Position p, int rownum, int columnnum, boolean hasInset) {
        super(parent, p);
        this.rownum = rownum;
        this.columnnum = columnnum;
        this.rowStyle = "";
        this.columnStyle = "";
        this.hasInset = hasInset;
        containedWidgets = new PWidget[rownum][columnnum];
        for (int i = 0; i < rownum; i++)
            for (int j = 0; j < columnnum; j++)
                containedWidgets[i][j] = null;
        this.updateWidgetsLayout();
    }

    public PLayout(PWidget parent, Position p) {
        super(parent, p);
        this.rownum = 1;
        this.columnnum = 1;
        this.rowStyle = "";
        this.columnStyle = "";
        containedWidgets = new PWidget[rownum][columnnum];
        for (int i = 0; i < rownum; i++)
            for (int j = 0; j < columnnum; j++)
                containedWidgets[i][j] = null;
        this.updateWidgetsLayout();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        updateWidgetsLayout();
    }

    @Override
    public void addChildWidget(PWidget widget, Position p) {
        if (this.layout != null)
            this.layout.addChildWidget(widget, p);
        else {
            widget.setParent(this);
            this.autoSetPosition(widget, p);
        }
    }

    @Override
    public synchronized void removeWidget(PWidget widget) {
        for (int i = 0; i < containedWidgets.length; i++)
            for (int j = 0; j < containedWidgets[0].length; j++) {
                if (containedWidgets[i][j] == widget) {
                    containedWidgets[i][j] = null;
                }
            }
    }

    @Override
    public Pixel[][] displayOutput() {
        Pixel[][] pixels = super.displayOutput();

        ArrayList<PWidget> childWidget = new ArrayList<>();
        for (int i = 0; i < this.getRowNum(); i++) {
            for (int j = 0; j < this.getColumnNum(); j++) {
                if (this.containedWidgets[i][j] != null) {
                    childWidget.add(this.containedWidgets[i][j]);
                }
            }
        }
        for (PWidget widget : childWidget) {
            Pixel[][] childPixels = widget.displayOutput();
            Pixel.pixelsAdd(pixels, childPixels, widget.getPosition());
        }
        return pixels;
    }

    public void updateWidgetsLayout() {
        int[] r;
        int[] c;
        if (this.getRowStyle() == "" || this.getRowStyle() == null) {
            int[] row = new int[this.getRowNum()];
            for (int i = 0; i < this.getRowNum(); i++) {
                row[i] = this.getWidgetHeight() / this.getRowNum();
            }
            r = row;
        } else {
            String[] row = this.getRowStyle().split(",");
            int[] rowRes = new int[this.getRowNum()];
            if (row.length < this.getRowNum()) {
                Log.WarningLog(this, "rowstyle length is not equal with rownum,will use default style");
                String Style = getColumnStyle();
                rowStyle = "";
                this.updateWidgetsLayout();
                rowStyle = Style;
                return;
            } else {
                try {
                    int numSec = 0;
                    int numSum = 0;
                    for (int i = 0; i < this.getRowNum(); i++) {
                        if (row[i].endsWith("x")) {
                            row[i] = row[i].substring(0, row[i].length() - 1);
                            numSec += Integer.valueOf(row[i]);
                        } else {
                            numSum += Integer.valueOf(row[i]);
                            rowRes[i] = Integer.valueOf(row[i]);
                            row[i] = "";
                        }
                    }
                    if (numSum >= this.getWidgetHeight()) {
                        Log.ErrorLog(this, "fixed rows too big!");
                        String Style = getColumnStyle();
                        rowStyle = "";
                        this.updateWidgetsLayout();
                        rowStyle = Style;
                        return;
                    }
                    for (int i = 0; i < this.getRowNum(); i++) {
                        if (row[i] != "") {
                            rowRes[i] = Integer.valueOf(row[i]) * (this.getWidgetHeight() - numSum) / numSec;
                        }
                    }
                } catch (Exception e) {
                    Log.ErrorLog(this, "read rowstyle failed");
                    String Style = getColumnStyle();
                    rowStyle = "";
                    this.updateWidgetsLayout();
                    rowStyle = Style;
                    return;
                }
            }
            r = rowRes;
        }

        if (this.getColumnStyle() == "" || this.getColumnStyle() == null) {
            int[] row = new int[this.getColumnNum()];
            for (int i = 0; i < this.getColumnNum(); i++) {
                row[i] = this.getWidgetWidth() / this.getColumnNum();
            }
            c = row;
        } else {
            String[] row = this.getColumnStyle().split(",");
            int[] rowRes = new int[this.getColumnNum()];
            if (row.length < this.getColumnNum()) {
                Log.WarningLog(this, "rowstyle length is not equal with rownum,will use default style");
                String Style = getColumnStyle();
                columnStyle = "";
                this.updateWidgetsLayout();
                columnStyle = Style;
                return;
            } else {
                try {
                    int numSec = 0;
                    int numSum = 0;
                    for (int i = 0; i < this.getColumnNum(); i++) {
                        if (row[i].endsWith("x")) {
                            row[i] = row[i].substring(0, row[i].length() - 1);
                            numSec += Integer.valueOf(row[i]);
                        } else {
                            numSum += Integer.valueOf(row[i]);
                            rowRes[i] = Integer.valueOf(row[i]);
                            row[i] = "";
                        }
                    }
                    if (numSum >= this.getWidgetWidth()) {
                        Log.ErrorLog(this, "fixed rows too big!");
                        String Style = getColumnStyle();
                        columnStyle = "";
                        this.updateWidgetsLayout();
                        columnStyle = Style;
                        return;
                    }
                    for (int i = 0; i < this.getColumnNum(); i++) {
                        if (row[i] != "") {
                            rowRes[i] = Integer.valueOf(row[i]) * (this.getWidgetWidth() - numSum) / numSec;
                        }
                    }
                } catch (Exception e) {
                    Log.ErrorLog(this, "read rowstyle failed");
                    String Style = columnStyle;
                    columnStyle = "";
                    this.updateWidgetsLayout();
                    columnStyle = Style;
                    return;
                }
            }
            c = rowRes;
        }
        for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < c.length; j++) {
                if (this.containedWidgets[i][j] != null) {

                    int pos_x = 0;
                    int pos_y = 0;
                    for (int a = 0; a < i; a++) {
                        pos_x += r[a];
                    }
                    for (int b = 0; b < j; b++) {
                        pos_y += c[b];
                    }

                    if (this.hasInset == true) {
                        this.containedWidgets[i][j].changeWidgetSize(c[j] - 2, r[i] - 2);
                        this.containedWidgets[i][j].setPosition(Position.getPosition(pos_x + 1, pos_y + 1));
                    } else {
                        this.containedWidgets[i][j].changeWidgetSize(c[j], r[i]);
                        this.containedWidgets[i][j].setPosition(Position.getPosition(pos_x, pos_y));
                    }
                }
            }
        }
    }

    public void setRowLayout(String args) {
        this.rowStyle = args;
        this.updateWidgetsLayout();
    }

    public void setColumnLayout(String args) {
        this.columnStyle = args;
        this.updateWidgetsLayout();
    }

    public void autoSetPosition(PWidget widget, Position p) {
        boolean operation = false;
        if (p == null) {
            for (int i = 0; i < this.getRowNum(); i++) {
                for (int j = 0; j < this.getColumnNum(); j++) {
                    if (this.containedWidgets[i][j] == null) {
                        this.containedWidgets[i][j] = widget;
                        operation = true;
                        break;
                    }
                }
                if (operation)
                    break;
            }
        } else {
            if (p.getX() <= this.getRowNum() && p.getY() <= this.getColumnNum() && p.getX() >= 1 && p.getY() >= 1
                    && this.containedWidgets[p.getX() - 1][p.getY() - 1] == null) {
                this.containedWidgets[p.getX() - 1][p.getY() - 1] = widget;
                operation = true;
            } else {
                this.containedWidgets[p.getX() - 1][p.getY() - 1] = widget;
                operation = true;
                Log.WarningLog(this, "something else has been put on position ");
            }
        }
        if (!operation) {
            Log.ErrorLog(this, "add widget to layout failed");
        } else {
            this.updateWidgetsLayout();
        }
    }

    @Override
    public ArrayList<PWidget> getChildWidget() {
        ArrayList<PWidget> res = new ArrayList<>();
        res.add(this);
        for (int i = 0; i < this.getRowNum(); i++) {
            for (int j = 0; j < this.getColumnNum(); j++) {
                if (this.containedWidgets[i][j] != null) {
                    res.addAll(this.containedWidgets[i][j].getChildWidget());
                }
            }
        }
        return res;
    }

    @Override
    public ArrayList<PWidget> getWidgetsAt(Position p) {
        ArrayList<PWidget> res = new ArrayList<>();
        res.add(this);
        for (int i = 0; i < this.getRowNum(); i++) {
            for (int j = 0; j < this.getColumnNum(); j++) {
                if (this.containedWidgets[i][j] != null) {
                    PWidget w = this.containedWidgets[i][j];
                    if (WidgetRange.inRange(w.getPosition(), w.getWidgetWidth(), w.getWidgetHeight(),
                            p))
                        res.addAll(this.containedWidgets[i][j].getWidgetsAt(Position
                                .getPosition(p.getX() - w.getPosition().getX(), p.getY() - w.getPosition().getY())));
                }
            }
        }
        return res;
    }
}
