package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {
	
	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListMovies() {
		 // WHEN
		 MovieDao movieDao = new MovieDao();
		 List<Movie> movies = movieDao.listMovies();
		 
		 // THEN
		 assertThat(movies).hasSize(3);
		 assertThat(movies).extracting("title").containsExactlyInAnyOrder("Title 1", "My Title 2", "Third title");
		 // On vérifie qu'un des films a bien son genre associé
		 assertThat(movies.get(0).getGenre()).isNotNull();
		 assertThat(movies.get(0).getGenre().getName()).isIn("Drama", "Comedy");
	 }
	
	 @Test
	 public void shouldListMoviesByGenre() {
		 // WHEN
		 MovieDao movieDao = new MovieDao();
		 List<Movie> comedyMovies = movieDao.listMoviesByGenre("Comedy");
		 
		 // THEN
		 assertThat(comedyMovies).hasSize(2);
		 assertThat(comedyMovies).extracting("title").containsExactlyInAnyOrder("My Title 2", "Third title");
	 }
	
	 @Test
	 public void shouldAddMovie() throws Exception {
		 // GIVEN
		 MovieDao movieDao = new MovieDao();
		 Genre drama = new Genre(1, "Drama");
		 Movie movie = new Movie("Interstellar", LocalDate.of(2014, 11, 5), drama, 169, "Christopher Nolan", "A great space odyssey");
		 
		 // WHEN
		 Movie resultMovie = movieDao.addMovie(movie);
		 
		 // THEN
		 assertThat(resultMovie.getId()).isNotNull();
		 assertThat(resultMovie.getId()).isEqualTo(4); // C'est le 4ème film inséré
		 
		 // On vérifie en base que le nombre total a augmenté
		 List<Movie> allMovies = movieDao.listMovies();
		 assertThat(allMovies).hasSize(4);
		 assertThat(allMovies).extracting("title").contains("Interstellar");
	 }
}