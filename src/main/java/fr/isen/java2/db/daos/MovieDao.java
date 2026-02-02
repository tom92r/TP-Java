package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre";
		try (Connection connection = DataSourceFactory.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {
			
			while (resultSet.next()) {
				Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				movies.add(new Movie(
						resultSet.getInt("idmovie"),
						resultSet.getString("title"),
						resultSet.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
						genre,
						resultSet.getInt("duration"),
						resultSet.getString("director"),
						resultSet.getString("summary")
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movies;
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?";
		
		try (Connection connection = DataSourceFactory.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, genreName);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
					movies.add(new Movie(
							resultSet.getInt("idmovie"),
							resultSet.getString("title"),
							resultSet.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
							genre,
							resultSet.getInt("duration"),
							resultSet.getString("director"),
							resultSet.getString("summary")
					));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movies;
	}

	public Movie addMovie(Movie movie) {
		String sql = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES(?,?,?,?,?,?)";
		
		try (Connection connection = DataSourceFactory.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			statement.setString(1, movie.getTitle());
			statement.setString(2, movie.getReleaseDate().toString() + " 00:00:00.000");
			statement.setInt(3, movie.getGenre().getId());
			statement.setInt(4, movie.getDuration());
			statement.setString(5, movie.getDirector());
			statement.setString(6, movie.getSummary());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					movie.setId(generatedKeys.getInt(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movie;
	}
}