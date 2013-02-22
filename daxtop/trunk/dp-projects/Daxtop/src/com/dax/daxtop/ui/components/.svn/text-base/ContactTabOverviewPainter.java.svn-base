package com.dax.daxtop.ui.components;

import com.dax.Main;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import org.pushingpixels.lafwidget.tabbed.DefaultTabPreviewPainter;
import org.pushingpixels.lafwidget.utils.LafConstants.TabOverviewKind;

/**
 * @author Dax Frost
 */
public class ContactTabOverviewPainter extends DefaultTabPreviewPainter
{
    public static TabOverviewKind OverviewKind = TabOverviewKind.ROUND_CAROUSEL;

    public static ContactTabOverviewPainter TabOverviewPainter = new ContactTabOverviewPainter();

    @Override
    public TabOverviewKind getOverviewKind(JTabbedPane tabPane)
    {
        return OverviewKind;
    }

    @Override
    public JFrame getModalOwner(JTabbedPane tabPane) {
        return Main.frmChat;
    }
}
