package eu.fbk.das.challenge.gui;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

public class TableRowTransferHandler extends TransferHandler {

	private static final long serialVersionUID = -5005950304881134370L;

	private final DataFlavor localObjectFlavor = new DataFlavor(Integer.class,
			"Integer Row Index");
	private JTable table = null;

	public TableRowTransferHandler(JTable table) {
		this.table = table;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		assert (c == table);
		return new DataHandler(new Integer(table.getSelectedRow()),
				localObjectFlavor.getMimeType());
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		boolean b = info.getComponent() == table && info.isDrop()
				&& info.isDataFlavorSupported(localObjectFlavor);
		table.setCursor(b ? DragSource.DefaultMoveDrop
				: DragSource.DefaultMoveNoDrop);
		return b;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
		JTable target = (JTable) info.getComponent();
		JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
		int index = dl.getRow();
		int max = table.getModel().getRowCount();
		if (index < 0 || index > max) {
			index = max;
		}
		target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		try {
			Integer rowFrom = (Integer) info.getTransferable().getTransferData(
					localObjectFlavor);
			if (rowFrom != -1 && rowFrom != index) {

				int[] rows = table.getSelectedRows();
				int dist = 0;
				for (int row : rows) {
					if (index > row) {
						dist++;
					}
				}
				index -= dist;

				// **TableUtil** is a simple class that just copy, remove and
				// select rows.

				Vector<Object> list = TableUtil.getSelectedList(table, rowFrom);
				TableUtil.removeSelected(table);
				TableUtil.addRowAt(table, list, index);
				// TableUtil.selectMultipleRow(table, sels);

				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable t, int act) {
		if (act == TransferHandler.MOVE) {
			table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
