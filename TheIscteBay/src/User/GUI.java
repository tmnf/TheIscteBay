package User;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import SearchClasses.FileDetails;

public class GUI {

	private JFrame mainFrame;

	private DefaultListModel<FileDetails> files;

	private Client client;

	public GUI(Client client) {
		this.client = client;
		files = new DefaultListModel<>();

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

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!searchField.getText().isEmpty())
					client.requestFileSearch(searchField.getText());
			}
		});

		topPanel.add(txt);
		topPanel.add(searchField);
		topPanel.add(searchButton);
		// ====================================================== \\

		// ==================Bot Panel======================== \\
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(2, 1));

		JList<FileDetails> list = new JList<>(files);

		JButton download = new JButton("Descarregar");
		JProgressBar downProgress = new JProgressBar();

		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.sendDowloadRequest(list.getSelectedValue());
			}
		});

		rightPanel.add(download);
		rightPanel.add(downProgress);

		bottPanel.add(new JScrollPane(list));
		bottPanel.add(rightPanel, BorderLayout.EAST);
		// =======================================================\\

		mainFrame.add(topPanel, BorderLayout.NORTH);
		mainFrame.add(bottPanel, BorderLayout.SOUTH);
	}

	public void showOnList(FileDetails[] list) {
		files.clear();
		for (int i = 0; i != list.length; i++)
			files.addElement(list[i]);
	}

	public void open() {
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

}
