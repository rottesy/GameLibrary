package com.example.gamelibrary.repository;

import com.example.gamelibrary.model.entity.Game;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByGenresNameIgnoreCase(String name);

    List<Game> findByGenresId(Long genreId);

    List<Game> findByRatingGreaterThanEqual(Integer rating);

    List<Game> findByDeveloperNameContainingIgnoreCase(String developer);

    List<Game> findByDeveloperId(Long developerId);

    List<Game> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Game> findByReleaseDateAfter(LocalDate date);

    Page<Game> findAllByOrderByRatingDesc(Pageable pageable);

    @Query(
            value = """
                    select distinct g
                    from Game g
                    join g.achievements a
                    where (:achievementName = ''
                        or lower(a.name) like lower(concat('%', :achievementName, '%')))
                        and (:achievementDescription = ''
                        or lower(coalesce(a.description, ''))
                        like lower(concat('%', :achievementDescription, '%')))
                        and (:minRating is null or g.rating >= :minRating)
            """,
            countQuery = """
                    select count(distinct g.id)
                    from Game g
                    join g.achievements a
                    where (:achievementName = ''
                        or lower(a.name) like lower(concat('%', :achievementName, '%')))
                        and (:achievementDescription = ''
                        or lower(coalesce(a.description, ''))
                        like lower(concat('%', :achievementDescription, '%')))
                        and (:minRating is null or g.rating >= :minRating)
            """
    )
    Page<Game> findByAchievementsWithJpql(
            @Param("achievementName") String achievementName,
            @Param("achievementDescription") String achievementDescription,
            @Param("minRating") Integer minRating,
            Pageable pageable
    );

    @Query(
            value = """
                    select distinct g.*
                    from games g
                    join achievements a on a.game_id = g.id
                    where (:achievementName = ''
                        or lower(a.name) like lower(concat('%', :achievementName, '%')))
                        and (:achievementDescription = ''
                        or lower(coalesce(a.description, ''))
                        like lower(concat('%', :achievementDescription, '%')))
                        and (:minRating is null or g.rating >= :minRating)
                    order by g.id
            """,
            countQuery = """
                    select count(distinct g.id)
                    from games g
                    join achievements a on a.game_id = g.id
                    where (:achievementName = ''
                        or lower(a.name) like lower(concat('%', :achievementName, '%')))
                        and (:achievementDescription = ''
                        or lower(coalesce(a.description, ''))
                        like lower(concat('%', :achievementDescription, '%')))
                        and (:minRating is null or g.rating >= :minRating)
            """,
            nativeQuery = true
    )
    Page<Game> findByAchievementsWithNative(
            @Param("achievementName") String achievementName,
            @Param("achievementDescription") String achievementDescription,
            @Param("minRating") Integer minRating,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Game g set g.developer = null where g.developer.id = :developerId")
    int clearDeveloperFromGames(@Param("developerId") Long developerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from user_games where game_id = :gameId", nativeQuery = true)
    int deleteUserGamesByGameId(@Param("gameId") Long gameId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from user_wishlist where game_id = :gameId", nativeQuery = true)
    int deleteUserWishlistByGameId(@Param("gameId") Long gameId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from collection_games where game_id = :gameId", nativeQuery = true)
    int deleteCollectionGamesByGameId(@Param("gameId") Long gameId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from game_genres where game_id = :gameId", nativeQuery = true)
    int deleteGameGenresByGameId(@Param("gameId") Long gameId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from game_genres where genre_id = :genreId", nativeQuery = true)
    int deleteGameGenresByGenreId(@Param("genreId") Long genreId);

    @EntityGraph(attributePaths = "reviews")
    @Query("select g from Game g")
    List<Game> findAllWithReviews();

    @EntityGraph(attributePaths = "achievements")
    @Query("select g from Game g")
    List<Game> findAllWithAchievements();
}
