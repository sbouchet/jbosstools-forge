package org.jboss.tools.forge.aesh.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.forge.aesh.console.AeshConsole;
import org.jboss.tools.forge.aesh.document.AeshDocument;
import org.jboss.tools.forge.aesh.document.AeshDocument.CursorListener;

public class AeshTextViewer extends TextViewer {
	
	private static final String AESH_CONSOLE_FONT = "org.jboss.tools.forge.aesh.font";

	private AeshConsole aeshConsole;
	private AeshDocument aeshDocument;
	
	private CursorListener cursorListener = new CursorListener() {		
		@Override
		public void cursorMoved() {
			StyledText textWidget = getTextWidget();
			if (textWidget != null && !textWidget.isDisposed()) {
				textWidget.setCaretOffset(aeshDocument.getCursorOffset());
			}
		}
	};
	
	private IDocumentListener documentListener = new IDocumentListener() {
    	@Override
        public void documentAboutToBeChanged(DocumentEvent event) {
        }
        @Override
        public void documentChanged(final DocumentEvent event) {
            StyledText textWidget = getTextWidget();
            if (textWidget != null && !textWidget.isDisposed()) {
                int lineCount = textWidget.getLineCount();
                textWidget.setTopIndex(lineCount - 1);
    			StyleRange styleRange = getDocument().getCurrentStyleRange();
    			if (styleRange != null) {
    				textWidget.setStyleRange(styleRange);
    			}
            }
        }
    };
	
    public AeshTextViewer(Composite parent) {
    	super(parent, SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    	initialize();
    }
    
    private void initialize() {
    	aeshConsole = new AeshConsole();
    	aeshDocument = new AeshDocument();
    	aeshDocument.connect(aeshConsole);
    	aeshDocument.addCursorListener(cursorListener);
    	aeshDocument.addDocumentListener(documentListener);
    	setDocument(aeshDocument);
    	getTextWidget().setFont(JFaceResources.getFont(AESH_CONSOLE_FONT));
    	aeshConsole.start();
    }
    
    public void cleanup() {
    	aeshConsole.stop();
    	aeshDocument.removeDocumentListener(documentListener);
    	aeshDocument.removeCursorListener(cursorListener);
    	aeshDocument.disconnect();
    }
    
    protected void handleVerifyEvent(VerifyEvent e) {
    	aeshConsole.sendInput(e.text);
		e.doit = false;    	
    }
    
    public AeshDocument getDocument() {
    	return (AeshDocument)super.getDocument();
    }
    
}
    
