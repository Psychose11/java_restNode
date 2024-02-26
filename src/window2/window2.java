package window2;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class window2 {

	private JFrame frame;
	private JTextField textField;
	private JTable table;
	private static JLabel lblNewLabel_7;
	private static JLabel lblNewLabel_8;
	private static JLabel lblNewLabel_9;


	static DefaultTableModel model;
	
	int salaireMinimal = 0;int salaireMaximal = 0;int totalSalaire = 0;

	/**
	 * Launch the application.
	 */
	public static void updateData() {

		model.setRowCount(0);
		updateSalaryData();
	    try {
	        URL url = new URL("http://localhost:2000/table-data");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");

	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();

	        List<String[]> dataList = processData(response.toString());

	        for (String[] data : dataList) {
	            model.addRow(data);
	        }

	        connection.disconnect();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private static List<String[]> processData(String responseData) {
	    List<String[]> dataList = new ArrayList<>();

	    // Supprimer les crochets de début et de fin pour obtenir un tableau JSON valide
	    responseData = responseData.substring(1, responseData.length() - 1);

	    // Séparer les objets JSON en fonction des virgules
	    String[] enseignantStrings = responseData.split("},");
	    for (String enseignantString : enseignantStrings) {
	        // Ajouter la virgule supprimée précédemment pour chaque objet JSON
	        if (!enseignantString.endsWith("}")) {
	            enseignantString += "}";
	        }

	        // Supprimer les caractères { et } restants
	        enseignantString = enseignantString.replace("{", "").replace("}", "");

	        // Séparer les paires clé-valeur pour chaque objet JSON en fonction des virgules
	        String[] parts = enseignantString.split(",");
	        String[] enseignantData = new String[5];
	        for (String part : parts) {
	            // Séparer la clé et la valeur pour chaque paire clé-valeur en fonction des deux-points
	            String[] keyValue = part.split(":");
	            // Supprimer les guillemets autour de la valeur
	            String value = keyValue[1].trim().replace("\"", "");
	            // Ajouter la valeur au tableau de données de l'enseignant
	            if (keyValue[0].trim().equals("\"id\"")) {
	                enseignantData[0] = value;
	            } else if (keyValue[0].trim().equals("\"nom\"")) {
	                enseignantData[1] = value;
	            } else if (keyValue[0].trim().equals("\"nbheures\"")) {
	                enseignantData[2] = value;
	            } else if (keyValue[0].trim().equals("\"taux\"")) {
	                enseignantData[3] = value;
	            } else if (keyValue[0].trim().equals("\"salaire\"")) {
	                enseignantData[4] = value;
	            }
	        }
	        // Ajouter les données de l'enseignant à la liste de données
	        dataList.add(enseignantData);
	    }

	    return dataList;
	}
	
	
	
	
 public static void updateSalaryData() {
        try {
            // Créer une URL pour l'endpoint salary-data de votre serveur Node.js
            URL url = new URL("http://localhost:2000/salary-data");

            // Ouvrir une connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configurer la méthode de requête
            connection.setRequestMethod("GET");

            // Lire la réponse de l'API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Traiter les données de réponse JSON sans utiliser JSONObject
            Map<String, Integer> jsonData = parseJsonData(response.toString());

            // Extraire les données de salaire
            int totalSalaire = jsonData.get("totalSalaire");
            int salaireMaximal = jsonData.get("salaireMaximal");
            int salaireMinimal = jsonData.get("salaireMinimal");

            // Utilisez les données de salaire mises à jour pour mettre à jour votre interface utilisateur
            System.out.println("Total des salaires : " + totalSalaire);
            System.out.println("Salaire maximal : " + salaireMaximal);
            System.out.println("Salaire minimal : " + salaireMinimal);
            
         // Mettre à jour les labels avec les nouvelles valeurs
            lblNewLabel_7.setText(Integer.toString(salaireMinimal));
            lblNewLabel_8.setText(Integer.toString(salaireMaximal));
            lblNewLabel_9.setText(Integer.toString(totalSalaire));

            // Fermer la connexion
            
           
            connection.disconnect();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> parseJsonData(String jsonData) {
        Map<String, Integer> parsedData = new HashMap<>();
      
        String[] keyValuePairs = jsonData.replaceAll("[{}\"]", "").split(",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            parsedData.put(keyValue[0].trim(), Integer.parseInt(keyValue[1].trim()));
        }
        return parsedData;
    }

	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window2 window = new window2();
					window.frame.setVisible(true);
					
					updateData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public window2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 581, 374);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(139, 52, 129, 20);
		panel.add(textField);
		textField.setColumns(10);
		
		JSpinner spinner = new JSpinner();
		spinner.setBounds(197, 83, 71, 20);
		panel.add(spinner);
		
		JSpinner spinner_1 = new JSpinner();
		spinner_1.setBounds(197, 114, 71, 20);
		panel.add(spinner_1);
		
		JSpinner spinner_2 = new JSpinner();
		spinner_2.setEnabled(false);
		spinner_2.setBounds(237, 21, 30, 20);
		panel.add(spinner_2);
		
		JLabel lblNewLabel = new JLabel("Nom enseignant");
		lblNewLabel.setBounds(29, 55, 100, 14);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("taux horaires");
		lblNewLabel_1.setBounds(29, 86, 100, 14);
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Nb d'heures");
		lblNewLabel_2.setBounds(29, 117, 100, 14);
		panel.add(lblNewLabel_2);
		
		
		
		JButton btnNewButton = new JButton("Ajouter");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 String nom = textField.getText(); 
				 int nbheures = (int) spinner.getValue();
				 int taux = (int) spinner_1.getValue();
				
				 
				 if (nom != "" && nbheures != 0 && taux != 0) {
					 try {
			            // Créez une URL
			            URL url = new URL("http://localhost:2000/get-data");

			            // Ouvrez une connexion
			            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			            // Configurez la connexion pour une requête POST
			            connection.setRequestMethod("POST");
			            connection.setDoOutput(true);

			            // Ajoutez les paramètres de la requête POST
			            String postData = "param-1=" + nom +
	                              "&param-2=" + nbheures +
	                              "&param-3=" + taux;
			            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			                wr.writeBytes(postData);
			            }

			            // Lisez la réponse
			            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			                String line;
			                while ((line = reader.readLine()) != null) {
			                    //System.out.println(line);
			                    if(line != null) {
			                    
			                    	 JOptionPane.showMessageDialog(null, "Enseignant ajouté", "Succès", JOptionPane.INFORMATION_MESSAGE);
			                    	 updateData();
			                    	
			                    }
			                    else {
			                    	
			                    	 JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout", "Erreur", JOptionPane.ERROR_MESSAGE);       	
			                   }
			                            
			                }		                
			            }
			        } catch (Exception e1) {
			     
			            JOptionPane.showMessageDialog(null, "Utilisateur existant ou autre", "Erreur", JOptionPane.ERROR_MESSAGE);
			        }
				 				 
				 }
				 else {
					 
					 JOptionPane.showMessageDialog(null, "Certains de vos champs sont vides ou nulls", "Champs vides", JOptionPane.ERROR_MESSAGE);
				 }
				 		
			}
						
		});
		btnNewButton.setBounds(29, 155, 89, 23);
		panel.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Supprimer");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					
				 int id = (int) spinner_2.getValue();
						 
				 try {
			            // Créez une URL
			            URL url = new URL("http://localhost:2000/delete-data");

			            // Ouvrez une connexion
			            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			            // Configurez la connexion pour une requête POST
			            connection.setRequestMethod("POST");
			            connection.setDoOutput(true);

			            // Ajoutez les paramètres de la requête POST
			            String postData = "param-1=" + id;
			            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			                wr.writeBytes(postData);
			            }

			            // Lisez la réponse
			            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			                String line;
			                while ((line = reader.readLine()) != null) {
			                    //System.out.println(line);
			                    if(line != null) {
			                    
			                    	 JOptionPane.showMessageDialog(null, "Enseignant supprimer", "Succès", JOptionPane.INFORMATION_MESSAGE);
			                    	 updateData();
			                    	
			                    }
			                    else {
			                    	
			                    	 JOptionPane.showMessageDialog(null, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);       	
			                   }
			                            
			                }		                
			            }
			        } catch (Exception e1) {
			     
			            JOptionPane.showMessageDialog(null, "Erreur de la base lors de la suppression ", "Erreur", JOptionPane.ERROR_MESSAGE);
			        }
							
				
			}
		});
		btnNewButton_1.setBounds(179, 155, 89, 23);
		panel.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Mettre à jour");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String nom = textField.getText(); 
				 int nbheures = (int) spinner.getValue();
				 int taux = (int) spinner_1.getValue();
				 int id = (int) spinner_2.getValue();
				 
				 
				 
				 try {
			            // Créez une URL
			            URL url = new URL("http://localhost:2000/update-data");

			            // Ouvrez une connexion
			            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			            // Configurez la connexion pour une requête POST
			            connection.setRequestMethod("POST");
			            connection.setDoOutput(true);

			            // Ajoutez les paramètres de la requête POST
			            String postData = "param-1=" + nom +
	                              "&param-2=" + nbheures +
	                              "&param-3=" + taux +"&param-4=" + id;
			            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			                wr.writeBytes(postData);
			            }

			            // Lisez la réponse
			            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			                String line;
			                while ((line = reader.readLine()) != null) {
			                    //System.out.println(line);
			                    if(line != null) {
			                    
			                    	 JOptionPane.showMessageDialog(null, "Enseignant modifié", "Succès", JOptionPane.INFORMATION_MESSAGE);
			                    	 updateData();
			                    	
			                    }
			                    else {
			                    	
			                    	 JOptionPane.showMessageDialog(null, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);       	
			                   }
			                            
			                }		                
			            }
			        } catch (Exception e1) {
			     
			            JOptionPane.showMessageDialog(null, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
			        }
				
				
				
				
				
				
				
				
			}
		});
		btnNewButton_2.setBounds(29, 205, 89, 23);
		panel.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("Vider");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 textField.setText("");
                 spinner.setValue(0);
                 spinner_1.setValue(0);
                 spinner_2.setValue(0);
                 
			}
		});
		btnNewButton_3.setBounds(179, 205, 89, 23);
		panel.add(btnNewButton_3);
		
		JLabel lblNewLabel_3 = new JLabel("Salaire");
		lblNewLabel_3.setBounds(10, 310, 46, 14);
		panel.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Minimale");
		lblNewLabel_4.setBounds(83, 291, 46, 14);
		panel.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Total");
		lblNewLabel_5.setBounds(509, 291, 46, 14);
		panel.add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("Maximale");
		lblNewLabel_6.setBounds(222, 291, 46, 14);
		panel.add(lblNewLabel_6);
		
		lblNewLabel_7 = new JLabel("New label");
		lblNewLabel_7.setBounds(83, 310, 46, 14);
		panel.add(lblNewLabel_7);

		lblNewLabel_8 = new JLabel("New label");
		lblNewLabel_8.setBounds(222, 310, 46, 14);
		panel.add(lblNewLabel_8);

		lblNewLabel_9 = new JLabel("New label");
		lblNewLabel_9.setBounds(498, 310, 46, 14);
		panel.add(lblNewLabel_9);

		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(302, 52, 253, 194);
		panel.add(scrollPane);
		
		table = new JTable();
		model=new DefaultTableModel();
		Object[] column = {"Numero Enseignant" , "Nom" , "Taux horaire" , "Nombre d'heures" , "Salaire"};
		model.setColumnIdentifiers(column);
		table.setModel(model);
		
	
		
		lblNewLabel_7.setText(Integer.toString(salaireMinimal));
		
		lblNewLabel_8.setText(Integer.toString(salaireMaximal));
		
		lblNewLabel_9.setText(Integer.toString(totalSalaire));

		
		
		table.getSelectionModel().addListSelectionListener((ListSelectionListener) new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) { 
                        
                        Object id = table.getValueAt(selectedRow, 0);
                        Object nom = table.getValueAt(selectedRow, 1);
                        Object taux = table.getValueAt(selectedRow, 2);
                        Object nbheures = table.getValueAt(selectedRow, 3);
                        Object salaire = table.getValueAt(selectedRow, 4);

                        
                        textField.setText(nom.toString());
                        spinner.setValue(Integer.parseInt((String) nbheures));
                        spinner_1.setValue(Integer.parseInt((String)taux));
                        spinner_2.setValue(Integer.parseInt((String)id));
                         
                    }
                }				
			}
		});
		scrollPane.setViewportView(table);
	
	}
	
}
