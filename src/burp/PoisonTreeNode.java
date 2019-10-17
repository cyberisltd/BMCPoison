package burp;

public class PoisonTreeNode {
	private String nodeType;
	private String nodeId;
	private String text;
	
	public PoisonTreeNode(String text, String nodeType, String nodeId) {		
		this.nodeId = nodeId;
		this.nodeType = nodeType;
		this.text = text;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public String getNodeType() {
		return nodeType;
	}
	
	public String toString() {
		return text;
	}
}
