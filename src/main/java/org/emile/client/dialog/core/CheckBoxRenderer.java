package org.emile.client.dialog.core;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CheckBoxRenderer  implements ListCellRenderer {

    private boolean[] selected;
    private String[] items;

    public CheckBoxRenderer(String[] items) {
        this.items = items;
        this.selected = new boolean[items.length];
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = null;
        JCheckBox box = null;
        if (value instanceof JCheckBox) {
            label = new JLabel(((JCheckBox)value).getText());
            box = new JCheckBox(label.getText());
        }
        return box;
    }
    
    public void setSelected(int i, boolean selected) {
        this.selected[i] = selected;
    }

}
