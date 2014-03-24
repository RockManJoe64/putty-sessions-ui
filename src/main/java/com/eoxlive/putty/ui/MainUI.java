package com.eoxlive.putty.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import com.eoxlive.putty.icon.IconManager;

public class MainUI {

	private static final String KEY_PUTTY_EXECUTABLE = "putty.executable";

	private List<Session> sessions;
	private PropertiesConfiguration configuration;
	private String executablePath;

	private JFrame jframe;

	private JList<Session> list;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new MainUI();
			}
		});
	}
	
	class SessionListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;
		
		private ImageIcon sessionIcon;

		public SessionListCellRenderer() {
			sessionIcon = new ImageIcon(IconManager.getInstance().getIcon("network-server", 20));
		}
		
		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			setIcon(sessionIcon);
			return this;
		}
	}

	class ChangePuttyExecutable extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ChangePuttyExecutable() {
			super("Change PuTTY Executable");
			IconManager iconManager = IconManager.getInstance();
			putValue(LARGE_ICON_KEY, new ImageIcon(iconManager.getIcon("utilities-terminal", 24)));
			putValue(SMALL_ICON, new ImageIcon(iconManager.getIcon("utilities-terminal", 16)));
			putValue(SHORT_DESCRIPTION, "Change PuTTY executable");
			putValue(LONG_DESCRIPTION, "Change the location of the PuTTY executable");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			changePuttyExecutable();
		}
	}
	
	class ChangePasswordAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ChangePasswordAction() {
			super("Change Password");
			IconManager iconManager = IconManager.getInstance();
			putValue(LARGE_ICON_KEY, new ImageIcon(iconManager.getIcon("emblem-readonly", 24)));
			putValue(SMALL_ICON, new ImageIcon(iconManager.getIcon("emblem-readonly", 16)));
			putValue(SHORT_DESCRIPTION, "Change session password");
			putValue(LONG_DESCRIPTION, "Change the password of the selected PuTTY session");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Session session = list.getSelectedValue();
			if (session != null) {
				promptPassword(session);
			}
		}
	}
	
	class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExitAction() {
			super("Exit");
			IconManager iconManager = IconManager.getInstance();
			putValue(LARGE_ICON_KEY, new ImageIcon(iconManager.getIcon("emblem-unreadable", 24)));
			putValue(SMALL_ICON, new ImageIcon(iconManager.getIcon("emblem-unreadable", 16)));
			putValue(SHORT_DESCRIPTION, "Exit");
			putValue(LONG_DESCRIPTION, "Exit application");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			attemptExit();
		}
	}

	public MainUI() {
		queryRegistry();
		loadProperties();
		createUI();
	}

	private void loadProperties() {
		String directory = System.getProperty("java.io.tmpdir");
		String sep = System.getProperty("file.separator");
		String configPath = directory + sep + "PuTTYSessions" + sep
				+ "sessions.properties";
		try {
			configuration = new PropertiesConfiguration(configPath);
		} catch (ConfigurationException e) {
			configuration = new PropertiesConfiguration();
			configuration.setFile(new File(configPath));
		}
	}

	private void queryRegistry() {
		PuttyRegistry registry = new PuttyRegistry();
		sessions = registry.getRegistrySessions();
		Collections.sort(this.sessions, new Comparator<Session>() {
			@Override
			public int compare(Session o1, Session o2) {
				return o1.getPuttyConfigName().compareTo(
						o2.getPuttyConfigName());
			}
		});
	}

	private void createUI() {
		jframe = new JFrame();
		jframe.setTitle("PuTTY Sessions");
		jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jframe.setIconImages(IconManager.getInstance().getIconSet("applications-internet"));
		
		jframe.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
					attemptExit();
				}
			}
		});
		
		JMenuItem menuItem = new JMenuItem(new ChangePuttyExecutable());
		JMenu menu = new JMenu("File");
		menu.add(menuItem);
		menu.add(new JSeparator());
		menuItem = new JMenuItem(new ExitAction());
		menu.add(menuItem);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		jframe.setJMenuBar(menuBar);

		menuItem = new JMenuItem(new ChangePasswordAction());
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(menuItem);
		
		JToolBar toolBar = new JToolBar("PuTTY");
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.add(new ChangePuttyExecutable());
		toolBar.add(new ChangePasswordAction());
		
		list = new JList<Session>(
				sessions.toArray(new Session[] {}));
		list.getActionMap().put("ChangePasswordAction", new ChangePasswordAction());
		list.getInputMap().put(KeyStroke.getKeyStroke("control P"), "ChangePasswordAction");
		
		DefaultListCellRenderer cellRenderer = new SessionListCellRenderer();
		list.setCellRenderer(cellRenderer);
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					Session session = list.getSelectedValue();
					String key = session.getPuttyConfigName() + ".password";
					String password = configuration.getString(key);
					if (StringUtils.isBlank(password)) {
						promptPassword(session);
					} else {
						session.setPassword(password);						
					}
					
					if (StringUtils.isNotBlank(session.getPassword())) {
						openSession(session);						
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(list);

		Container cp = jframe.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(scrollPane);
		cp.add(toolBar, BorderLayout.NORTH);
		// jframe.pack();
		jframe.setSize(640, 480);

		jframe.setLocationByPlatform(true);
		jframe.setVisible(true);

		checkPuttyExecLocation();
	}
	
	private void attemptExit() {
		int option = JOptionPane.showConfirmDialog(jframe, "Do you wish to exit?", "Exit Application", JOptionPane.YES_NO_OPTION);
		if (JOptionPane.YES_OPTION == option) {
			jframe.dispose();				
		}			
	}

	private void promptPassword(Session session) {
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(null, pf,
				"Please Enter Password for " + session.getPuttyConfigName(),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (okCxl == JOptionPane.OK_OPTION) {
			String password = new String(pf.getPassword());
			session.setPassword(password);
		}
		
		if (StringUtils.isNotBlank(session.getPassword())) {
			String key = session.getPuttyConfigName() + ".password";
			configuration.setProperty(key, session.getPassword());
			try {
				configuration.save();
			} catch (ConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void checkPuttyExecLocation() {
		executablePath = configuration.getString(KEY_PUTTY_EXECUTABLE);
		if (StringUtils.isBlank(executablePath)) {
			changePuttyExecutable();
		}
	}
	
	private void changePuttyExecutable() {
		JFileChooser chooser = new JFileChooser(
				System.getProperty("user.home"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Executables", "exe");
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Select the PuTTY Executable");

		int value = chooser.showOpenDialog(jframe);

		if (value == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if (selectedFile != null) {
				executablePath = selectedFile.getAbsolutePath();
				configuration.setProperty(KEY_PUTTY_EXECUTABLE, executablePath);
				try {
					configuration.save();
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				list.setEnabled(false);
			}
		}
	}

	private void openSession(Session session) {
		String command = new StringBuilder(this.executablePath).append(" ")
				.append("-pw").append(" ").append(session.getPassword())
				.append(" ").append("-load").append(" ")
				.append(session.getPuttyConfigName()).append(" ").toString();

		try {
			Process process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
