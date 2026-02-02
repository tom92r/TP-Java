package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // IMPORTANT : Ajout de l'import pour le Bonus 2

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

    public List<Genre> listGenres() {
        List<Genre> genres = new ArrayList<>();
        try (Connection connection = DataSourceFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM genre")) {
            while (resultSet.next()) {
                genres.add(new Genre(resultSet.getInt("idgenre"), resultSet.getString("name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return genres;
    }

    // Modification pour le Bonus 2 : Retourne Optional<Genre> au lieu de Genre
    public Optional<Genre> getGenre(String name) {
        try (Connection connection = DataSourceFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Si trouvé, on emballe dans un Optional
                    return Optional.of(new Genre(resultSet.getInt("idgenre"), resultSet.getString("name")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
        // Si non trouvé ou erreur, on renvoie un Optional vide (jamais null !)
        return Optional.empty();
    }

    public void addGenre(String name) {
        try (Connection connection = DataSourceFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")) {
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}