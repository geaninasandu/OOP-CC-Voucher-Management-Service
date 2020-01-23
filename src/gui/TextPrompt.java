package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * The TextPrompt class will display a prompt over top of a text component when
 * the Document of the text field is empty. The Show property is used to
 * determine the visibility of the prompt.
 * <p>
 * The Font and foreground Color of the prompt will default to those properties
 * of the parent text component. You are free to change the properties after
 * class construction.
 */
public class TextPrompt extends JLabel
        implements FocusListener, DocumentListener {
    private JTextComponent component;
    private Document document;
    private Show show;
    private boolean showPromptOnce;
    private int focusLost;

    TextPrompt(String text, JTextComponent component) {
        this(text, component, Show.ALWAYS);
    }

    private TextPrompt(String text, JTextComponent component, Show show) {
        this.component = component;
        setShow(show);
        document = component.getDocument();

        setText(text);
        setFont(component.getFont());
        setForeground(component.getForeground());
        setBorder(new EmptyBorder(component.getInsets()));
        setHorizontalAlignment(JLabel.LEADING);

        component.addFocusListener(this);
        document.addDocumentListener(this);

        component.setLayout(new BorderLayout());
        component.add(this);
        checkForPrompt();
    }

    /**
     * Set the prompt Show property to control when the promt is shown.
     * Valid values are:
     * <p>
     * Show.AWLAYS (default) - always show the prompt
     * Show.Focus_GAINED - show the prompt when the component gains focus
     * (and hide the prompt when focus is lost)
     * Show.Focus_LOST - show the prompt when the component loses focus
     * (and hide the prompt when focus is gained)
     *
     * @param show a valid Show enum
     */
    private void setShow(Show show) {
        this.show = show;
    }

    /**
     * Check whether the prompt should be visible or not. The visibility
     * will change on updates to the Document and on focus changes.
     */
    private void checkForPrompt() {
        //  Text has been entered, remove the prompt

        if (document.getLength() > 0) {
            setVisible(false);
            return;
        }

        //  Prompt has already been shown once, remove it

        if (showPromptOnce && focusLost > 0) {
            setVisible(false);
            return;
        }

        //  Check the Show property and component focus to determine if the
        //  prompt should be displayed.

        if (component.hasFocus()) {
            if (show == Show.ALWAYS
                    || show == Show.FOCUS_GAINED)
                setVisible(true);
            else
                setVisible(false);
        } else {
            if (show == Show.ALWAYS
                    || show == Show.FOCUS_LOST)
                setVisible(true);
            else
                setVisible(false);
        }
    }

    public void focusGained(FocusEvent e) {
        checkForPrompt();
    }

//  Implement FocusListener

    public void focusLost(FocusEvent e) {
        focusLost++;
        checkForPrompt();
    }

    public void insertUpdate(DocumentEvent e) {
        checkForPrompt();
    }

//  Implement DocumentListener

    public void removeUpdate(DocumentEvent e) {
        checkForPrompt();
    }

    public void changedUpdate(DocumentEvent e) {
    }

    public enum Show {
        ALWAYS,
        FOCUS_GAINED,
        FOCUS_LOST
    }
}