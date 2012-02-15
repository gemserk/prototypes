package com.gemserk.prototypes.launcher;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.gemserk.highscores.gui.UserDataRegistrator.RequestUserDataListener;

public class RegisterUserJFrame extends JDialog {

	private static final long serialVersionUID = -1916638640282527452L;
	
	private JPanel contentPane;
	private JTextField usernameTextField;
	private JTextField nameTextField;
	private JPasswordField passwordTextField;
	
	private JButton submitButton;
	private JButton cancelButton;
	
	RequestUserDataListener requestUserDataListener;
	
	public void setRequestUserDataListener(RequestUserDataListener requestUserDataListener) {
		this.requestUserDataListener = requestUserDataListener;
	}

	/**
	 * Create the frame.
	 */
	public RegisterUserJFrame() {
		setResizable(false);
		setTitle("Register user");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 320, 160);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				requestUserDataListener.cancelled();
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(0, 20, 0, 20));
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		submitButton = new JButton("Submit");
		panel.add(submitButton, BorderLayout.WEST);
		
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RegisterUserJFrame.this.setVisible(false);
				requestUserDataListener.accepted(null, null, null);
			}
		});
		
		cancelButton = new JButton("Cancel");
		panel.add(cancelButton, BorderLayout.EAST);
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RegisterUserJFrame.this.setVisible(false);
				requestUserDataListener.cancelled();
			}
		});
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblNewLabel = new JLabel("Username: ");
		panel_1.add(lblNewLabel);
		
		usernameTextField = new JTextField();
		usernameTextField.setText("player9");
		panel_1.add(usernameTextField);
		usernameTextField.setColumns(15);
		
		JLabel lblNewLabel_1 = new JLabel("Name: ");
		panel_1.add(lblNewLabel_1);
		
		nameTextField = new JTextField();
		nameTextField.setText("");
		panel_1.add(nameTextField);
		nameTextField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Password:");
		panel_1.add(lblNewLabel_2);
		
		passwordTextField = new JPasswordField();
		panel_1.add(passwordTextField);
	}

	public void handle(RequestUserDataListener requestUserDataListener) {
		if (this.isVisible())
			return;
		this.requestUserDataListener = requestUserDataListener;
		this.setVisible(true);
	}
}
