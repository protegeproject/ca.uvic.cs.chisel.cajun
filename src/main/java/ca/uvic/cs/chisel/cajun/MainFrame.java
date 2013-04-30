package ca.uvic.cs.chisel.cajun;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ca.uvic.cs.chisel.cajun.actions.CajunAction;
import ca.uvic.cs.chisel.cajun.graph.FlatGraph;
import ca.uvic.cs.chisel.cajun.graph.arc.DefaultGraphArcStyle;
import ca.uvic.cs.chisel.cajun.graph.node.DefaultGraphNodeStyle;
import ca.uvic.cs.chisel.cajun.graph.ui.DefaultFlatGraphView;

class MainFrame extends JFrame {

	private FlatGraph graph;

	public MainFrame() {
		super("Baby Shrimp Test");

		SampleGraphModel model = new SampleGraphModel();
		this.graph = new FlatGraph(model);
		// color the nodes based on node type
		DefaultGraphNodeStyle nodeStyle = new DefaultGraphNodeStyle();
		nodeStyle.setNodeTypes(model.getNodeTypes());
		this.graph.setGraphNodeStyle(nodeStyle);
		// color the arcs based on arc type
		DefaultGraphArcStyle arcStyle = new DefaultGraphArcStyle();
		arcStyle.setArcTypes(model.getArcTypes());
		this.graph.setGraphArcStyle(arcStyle);

		initialize();

		// run the initial layout on the nodes
		//SwingUtilities.invokeLater(new Runnable() {
		//	public void run() {
		//		try {
		//			Thread.sleep(100);
		//		} catch (InterruptedException e) {
		//		}
		//		graph.performLayout();
		//}
		//});

	}

	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		DefaultFlatGraphView view = new DefaultFlatGraphView(graph);
		getContentPane().add(view, BorderLayout.CENTER);

		graph.getNodeContextMenu().add(new CajunAction("Test #1"));
		graph.getNodeContextMenu().add(new CajunAction("Test #2"));

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton(new AbstractAction(" Close ") {
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.dispose();
			}
		}));

		pack();
		Dimension d = new Dimension(800, 600);
		setPreferredSize(d);
		setSize(d);
		setLocation(100, 50);

		setVisible(true);

	}

	public static void main(String[] args) {
		new MainFrame();
	}

}
