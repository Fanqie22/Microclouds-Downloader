package top.microclouds.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class MyJProgressBar extends JProgressBar implements TableCellRenderer {
	public MyJProgressBar() {
		super(0, 100);
		this.setStringPainted(true);
		this.setForeground(Color.green);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Float floatValue = Float.parseFloat(value.toString());
		int intValue = (int) floatValue.floatValue();
		this.setValue(intValue);
		this.setString(value.toString() + " %");
		return this;
	}
}