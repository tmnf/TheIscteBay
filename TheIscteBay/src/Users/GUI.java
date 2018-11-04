package Users;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class GUI {

	private JFrame mainFrame;

	private JList<File> list;

	public GUI() {
		initWindow();
		initComponents();
	}

	private void initWindow() {
		mainFrame = new JFrame("TheIscteBay");
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private void initComponents() {
		mainFrame.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		JPanel bottPanel = new JPanel();

		topPanel.setLayout(new GridLayout(1, 3, 0, 2));
		bottPanel.setLayout(new BorderLayout());

		// ==================Top Panel======================== \\
		JTextField txt = new JTextField("Texto a procurar:  ");
		JTextField searchField = new JTextField();
		JButton searchButton = new JButton("Procurar");

		txt.setEditable(false);

		topPanel.add(txt);
		topPanel.add(searchField);
		topPanel.add(searchButton);
		// ====================================================== \\

		// ==================Bot Panel======================== \\
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(2, 1));

		list = new JList<>(); // 1 Fase. Nao importa o tipo

		JButton download = new JButton("Descarregar");
		JProgressBar downProgress = new JProgressBar();

		rightPanel.add(download);
		rightPanel.add(downProgress);

		bottPanel.add(rightPanel, BorderLayout.WEST);
		bottPanel.add(rightPanel, BorderLayout.EAST);
		// =======================================================\\

		mainFrame.add(topPanel, BorderLayout.NORTH);
		mainFrame.add(bottPanel, BorderLayout.SOUTH);
	}

	public void open() {
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

}
