package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		// Utilisation du bloc try-with-resources pour fermer la connexion automatiquement
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery("SELECT * FROM genre")) {
			
			while (resultSet.next()) {
				// On récupère idgenre et name de la table SQL
				Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				genres.add(genre);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genres;
	}

	public Genre getGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
			
			statement.setString(1, name); // Sécurisation contre l'injection SQL
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // Note: À modifier plus tard pour le Bonus 2 avec Optional
	}

	public void addGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")) {
			
			statement.setString(1, name);
			statement.executeUpdate(); // Utilisation de executeUpdate pour l'insertion
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}