package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.Tiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TilesRepository extends JpaRepository<Tiles, Long> {
    Tiles findByTileId(String tilesId);

    List<Tiles> findAll();

    List<Tiles> findAllByTileIdIn(List<String> tilesIdList);

    List<Tiles> findAllByIdIn(List<Long> idList);
}
