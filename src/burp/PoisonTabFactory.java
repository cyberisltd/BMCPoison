package burp;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

class PoisonTabFactory implements IMessageEditorTabFactory, TreeModelListener {
	
	@Override
	public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
		return new PoisonTab(controller, editable);
	}
	
	class PoisonTab implements IMessageEditorTab {
	
		private boolean editable;
		private JTree tree;
		
		public PoisonTab(IMessageEditorController controller, boolean editable) {
			this.editable = editable;
			this.tree = new JTree();
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
			root.removeAllChildren();
			model.reload();
			this.tree.setEditable(editable);
		}
		
		@Override
		public String getTabCaption() {
			return "Poison";
		}
	
		@Override
		public Component getUiComponent() {
			return tree;
		}
	
		@Override
		public boolean isEnabled(byte[] content, boolean isRequest) {
			if (isRequest) {
				// Check that this is a request to remedy
				String request = BurpExtender.helpers.bytesToString(content);
				
				String[] rData = request.split("\r\n\r\n");
				if (rData.length > 1) {
					if (rData[1].matches("[0-9]+\\/[A-Za-z]+\\/.+")) {
						return true;
					}
				}
			}
			return false;
		}
	
		@Override
		public void setMessage(byte[] content, boolean isRequest) {
			//BurpExtender.stdout.println("Setting message in poison tab");
			if (this.isEnabled(content, isRequest) == true) {
				//BurpExtender.stdout.println("In setMessage: isEnabled returned true");
				String request = BurpExtender.helpers.bytesToString(content);
				String requestData = request.split("\r\n\r\n")[1];
				RemedyData remedyData = new RemedyData (requestData);
				remedyData.generate();
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
				root.removeAllChildren();
				root.add(remedyData.getTree());
				model.reload();
			}
		}
	
		@Override
		public byte[] getMessage() {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public boolean isModified() {
			// TODO Auto-generated method stub
			return false;
		}
	
		@Override
		public byte[] getSelectedData() {
			// TODO Auto-generated method stub
			return null;
		}
	
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());
		
	}

	@Override
	public void treeNodesInserted(TreeModelEvent arg0) {
		
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent arg0) {
		
	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0) {
		
	}
}