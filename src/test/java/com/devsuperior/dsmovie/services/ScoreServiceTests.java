package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	@Mock
	private UserService userService;
	
	private Long existingMovieId, nonExistingMovieId;
	private MovieEntity existingMovie, nonExistingMovie;
	private ScoreEntity existingMovieScore, nonExistingMovieScore;
	private ScoreDTO existingMovieScoreDTO, nonExistingMovieScoreDTO;
	private UserEntity user;
	
	@BeforeEach
	void setUp() throws Exception {
		existingMovieId = 1L;
		nonExistingMovieId = 1000L;
		
		existingMovie = MovieFactory.createCustomMovieEntity(existingMovieId);
		nonExistingMovie = MovieFactory.createCustomMovieEntity(nonExistingMovieId);
		existingMovieScore = ScoreFactory.createCustomScoreEntity(existingMovieId);
		nonExistingMovieScore = ScoreFactory.createCustomScoreEntity(nonExistingMovieId);
		existingMovieScoreDTO = ScoreFactory.createCustomScoreDTO(existingMovieScore);
		nonExistingMovieScoreDTO = ScoreFactory.createCustomScoreDTO(nonExistingMovieScore);
		user = UserFactory.createUserEntity();
		
		// adicionar o novo score no movie
		existingMovie.getScores().add(existingMovieScore);
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(existingMovie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		
		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(existingMovieScore);
		Mockito.when(movieRepository.save(any())).thenReturn(existingMovie);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		MovieDTO result = service.saveScore(existingMovieScoreDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingMovieId);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(nonExistingMovieScoreDTO);
		});
		
	}
}
