package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.UserTiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTilesRepository extends JpaRepository<UserTiles, Long> {
    List<UserTiles> findAllByUserId(int userId);

    List<UserTiles> findAllByUserIdAndTilesIdIn(int userId, List<Long> tilesIdList);

}
