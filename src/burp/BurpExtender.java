package burp;

import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender, IExtensionStateListener {
	
	static public IBurpExtenderCallbacks callbacks;
	static public IExtensionHelpers helpers;
	static public PrintWriter stdout;

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		// First steps
		BurpExtender.callbacks = callbacks;
		BurpExtender.callbacks.setExtensionName("BMC Poison");
		BurpExtender.helpers = callbacks.getHelpers();
		stdout = new PrintWriter(BurpExtender.callbacks.getStdout(),true);
		stdout.println("I got the Poison, I got the Remedy");
		
		// Register the editor tab
		PoisonTabFactory poison = new PoisonTabFactory();
		BurpExtender.callbacks.registerMessageEditorTabFactory(poison);
	}
	
	@Override
	public void extensionUnloaded() {
		stdout.println("Yeah... Yeah... Yeah... Yeah... Yeah...");
	}
}
