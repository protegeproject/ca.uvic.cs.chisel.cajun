package ca.uvic.cs.chisel.cajun.graph.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class StatusProgressBar extends JPanel {

	private static final int MIN_REPAINT = 100;
	
	private boolean inProgress;
	private long lastStatusPaint;
	private JLabel statusLabel;
	private long lastProgressPaint;
	private JProgressBar progressBar;

	public StatusProgressBar() {
		super(new BorderLayout(5, 0));
		this.inProgress = false;
		this.lastStatusPaint = 0;
		this.lastProgressPaint = 0;
		
		initialize();
	}

	private void initialize() {
		setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createRaisedBevelBorder(),
						BorderFactory.createEmptyBorder(1, 2, 1, 2)));
		
		add(getStatusLabel(), BorderLayout.CENTER);
		add(getProgressBar(), BorderLayout.EAST);
		
		setPreferredSize(new Dimension(500, 28));
	}
	
	private JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel("", JLabel.LEFT);
		}
		return statusLabel;
	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar(0, 100) {
				@Override
				public void repaint(long tm, int x, int y, int width, int height) {
					long diff = System.currentTimeMillis() - lastProgressPaint;
					if (isInProgress() && (diff > MIN_REPAINT)) {
						//System.out.println("repaint()");
						if (SwingUtilities.isEventDispatchThread()) {
							paintImmediately(x, y, width, height);
							lastProgressPaint = System.currentTimeMillis();
							return;
						}
					}
					super.repaint(tm, x, y, width, height);
				}
			};
			progressBar.setValue(0);
			progressBar.setBorderPainted(true);
			progressBar.setIndeterminate(false);
			progressBar.setStringPainted(false);
			progressBar.setVisible(false);
			progressBar.setPreferredSize(new Dimension(100, 24));
			progressBar.setMaximumSize(new Dimension(200, 32));
		}
		return progressBar;
	}

	/**
	 * Sets the progress cycle time in milliseconds.
	 * Default value is 3000 (3 seconds).
	 * This value should be a multiple of the repaint interval.
	 * @param cycleTime the total time for a progressbar cycle in milliseconds
	 */
	public void setProgressCycleTime(int cycleTime) {
		UIManager.put("ProgressBar.cycleTime", new Integer(cycleTime));
	}

	/**
	 * Sets the repaint interval for the progress bar in milliseconds.
	 * The default value is 50ms.
	 * @param repaintInterval the repaint time in milliseconds
	 */
	public void setProgressRepaintInterval(int repaintInterval) {
		UIManager.put("ProgressBar.repaintInterval", new Integer(repaintInterval));
	}

	public void setStatus(String msg) {
		setStatus(msg, false);
	}
	
	public void setStatus(Throwable t) {
		setStatus(t.getMessage(), true);
	}
	
	public void setStatus(String msg, boolean error) {
		statusLabel.setText(msg);
		statusLabel.setToolTipText(msg);
		statusLabel.setForeground(error ? Color.red : Color.black);
		
		// force a repaint
		if (SwingUtilities.isEventDispatchThread()) {
			long diff = System.currentTimeMillis() - lastStatusPaint;
			if (diff > MIN_REPAINT) {
				statusLabel.paintImmediately(statusLabel.getBounds());
				lastStatusPaint = System.currentTimeMillis();
			}
		}
	}
	
	/**
	 * Sets the string to be painted over top of the progress bar.
	 * If the string is empty or null the progress string is removed.
	 * @param str the string to paint on the progress bar
	 */
	public void setProgressString(String str) {
		JProgressBar pb = getProgressBar();
		if ((str == null) || (str.length() == 0)) {
			pb.setString("");
			pb.setStringPainted(false);
		} else {
			pb.setString(str);
			pb.setStringPainted(true);
		}
	}
	
	public boolean isInProgress() {
		return inProgress;
	}
	
	public void startProgress() {
		if (!inProgress) {
			inProgress = true;
			//showProgress();
		}
	}

	public void stopProgress() {
		if (inProgress) {
			inProgress = false;
			//hideProgress();
		}
	}
	
	protected void showProgress() {
		getProgressBar().setValue(0);
		getProgressBar().setVisible(true);
		getProgressBar().setIndeterminate(true);
		getProgressBar().invalidate();
	}
	
	protected void hideProgress() {
		getProgressBar().setIndeterminate(false);
		getProgressBar().setVisible(false);
		getProgressBar().invalidate();
	}
	
	public static void main(String[] args) {
		JDialog dlg = new JDialog();
		dlg.setTitle("Test");
		dlg.setModal(true);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dlg.getContentPane().setLayout(new BorderLayout());
		
		final StatusProgressBar spb = new StatusProgressBar();
		
		JPanel pnl = new JPanel();
		pnl.add(new JButton(new AbstractAction("Start/Stop") {
			public void actionPerformed(ActionEvent e) {
				if (spb.isInProgress()) {
					spb.stopProgress();
				} else {
					spb.startProgress();
				}
			}
		}));
		dlg.getContentPane().add(pnl, BorderLayout.CENTER);
		dlg.getContentPane().add(spb, BorderLayout.SOUTH);

		dlg.pack();
		dlg.setLocation(100, 50);
		dlg.setPreferredSize(new Dimension(400, 200));
		dlg.setVisible(true);
		
		System.exit(0);
	}
	
}
