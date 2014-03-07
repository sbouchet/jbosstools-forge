package org.jboss.tools.forge.ui.ext.cli;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.ui.document.DelegateDocument;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;

public class F2TextViewer extends AeshTextViewer {
	
    public F2TextViewer(Composite parent) {
		super(parent);
	}

    protected void initializeDocument() {
    	aeshDocument = new DelegateDocument();
    	aeshDocument.addCursorListener(cursorListener);
    	aeshDocument.addDocumentListener(documentListener);
    }
    
	protected void initializeConsole() {
    	aeshConsole = new F2Console();
    }
	
	protected void initialize() {
		super.initialize();
		if (ForgeRuntimeState.RUNNING.equals(FurnaceRuntime.INSTANCE.getState())) {
			startConsole();
		}
	}
    
    public void startConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				((F2Console)aeshConsole).initialize();
				((F2Console)aeshConsole).connect(aeshDocument.getProxy());
		    	setDocument(aeshDocument);
		    	aeshConsole.start();
			}   		
    	});
    }
    
    public void stopConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
		    	aeshConsole.stop();
		    	((F2Console)aeshConsole).disconnect();
		    	aeshDocument.reset();
		    	setDocument(null);
			}    		
    	});
    }
    
    
}
    
